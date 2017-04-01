package com.notesdea.sharelocationclient;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LocationService extends Service {

    private static final String TAG = "LocationServer";
    public Binder mBinder = new Binder();
    private String mUrl;
    private int mPort;

    private Socket mSocket;
    private BufferedReader mReader;
    private BufferedWriter mWriter;


    //再每次绑定服务时调用
    @Override
    public IBinder onBind(Intent intent) {
        mUrl = intent.getStringExtra("url");
        mPort = intent.getIntExtra("port", 8000);
        Log.d(TAG, "url = " + mUrl + ", port = " + mPort);
        connect(mUrl, mPort);
        return mBinder;
    }

    //Socket 连接服务端
    public void connect(final String url, final int port) {
        AsyncTask<Void, String, Void> task = new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mSocket = new Socket(url, port);
                    mReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    mWriter = new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream()));
                    Log.d(TAG, "connect success");
                    String data = null;
                    while ((data = mReader.readLine()) != null) {
                        publishProgress(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                String data = values[0];
                if (data == null) return;
                mBinder.updateLocation(data);
                Log.d(TAG, "onProgressUpdate " + data);
            }
        };
        task.execute();
    }

    public class Binder extends android.os.Binder {

        //上传自己的位置
        public void uploadLocation(String data) {
            if (mWriter == null) return;

            try {
                mWriter.write("@location" + data + "\n");
                mWriter.flush();
                Log.d(TAG, "uploadLocation " + data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //接受其他客户端的返回值并发送广播
        public void updateLocation(String data) {
            Log.d(TAG, "updateLocation " + data);
            Intent intent = new Intent(MainActivity.LocationChangeReceiver.BROADCAST_ACTION);
            intent.putExtra("location", data);
            sendBroadcast(intent);
        }
    }
}
