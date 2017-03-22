package com.notesdea.sharelocationclient;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //位置权限的请求码
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 100;
    //需要请求的权限
    private static final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    private LocationListener mLocationListener = new LocationListener();
    //另一个点相关
    private BitmapDescriptor mIconAnother = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);

    private LocationBc mLocationBc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
        initListener();

        String[] needPermissions = getRequestPermissions();
        if (needPermissions.length > 0) {
            ActivityCompat.requestPermissions(this, needPermissions, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            startPosition();
        }
    }

    private void initView() {
        mMapView = (MapView) findViewById(R.id.bmapView);
    }

    private void initData() {
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(mLocationListener); //注册定位监听器
    }

    private void initListener() {
        //注册广播接收器
        mLocationBc = new LocationBc();
        IntentFilter filter = new IntentFilter(LocationBc.BROADCAST_ACTION);
        registerReceiver(mLocationBc, filter);
    }

    //开启定位
    private void startPosition() {
        LocationClientOption locOption = new LocationClientOption(); //配置定位等各种参数
        locOption.setOpenGps(true); //设置打开gps
        locOption.setCoorType("bd09ll"); //返回的定位结果是百度经纬度,默认值gcj02
        mLocClient.setLocOption(locOption);
        mLocClient.start(); //启动定位
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mLocClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        unregisterReceiver(mLocationBc);
        super.onDestroy();
    }

    //获取需要请求的权限
    private String[] getRequestPermissions() {
        List<String> requestPermissions = new ArrayList<>();
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                requestPermissions.add(permission);
        }
        return requestPermissions.toArray(new String[requestPermissions.size()]);
    }

    //请求权限返回的结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                //权限通过时启动定位
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startPosition();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void initOverlay(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions marker = new MarkerOptions()
                .position(latLng).icon(mIconAnother).anchor(0.5f, 0.5f).title("notesdea");
        TextOptions text = new TextOptions().
                position(new LatLng(latitude + 0.0003, longitude)).text("notesdea").fontSize(30);
        mBaiduMap.addOverlay(marker);
        mBaiduMap.addOverlay(text);
        //todo 可以通过添加overlay 完成提示是谁的操作或是通过 extraInfo()方法设置额外信息
    }

    //todo 更新其他人的位置信息
    private void updateLocation(String data) {

    }

    //定位监听器
    private class LocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

            //当不没接收到位置或地图销毁时不处理操作
            if (bdLocation == null || mMapView == null) {
                return;
            }

            //构建定位数据
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius()) //设置定位数据的精度信息
                    .direction(100) //设置定位的方向
                    .latitude(bdLocation.getLatitude()) //设置定位的纬度
                    .longitude(bdLocation.getLongitude() ) //设置定位的经度
                    .build();
            mBaiduMap.setMyLocationData(locData);

            //获取经纬度对象
            LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            //target:设置中心点， zoom: 设置地图缩放级别
            builder.target(latLng).zoom(18.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            initOverlay(bdLocation.getLatitude() + 0.01, bdLocation.getLongitude() + 0.01);
        }
    }

    //广播接收器
    public class LocationBc extends BroadcastReceiver {
        public static final String BROADCAST_ACTION = "com.notesdea.sharelocationclient.LOCATION_BROADCAST";

        public static final String TAG = "LocationBc";

        @Override
        public void onReceive(Context context, Intent intent) {
            String data = intent.getStringExtra("location");
            Log.d(TAG, "Received location: " + data);
            if (data != null && data.startsWith(getString(R.string.socket_location))) {
                updateLocation(data);
            }

        }
    }
}
