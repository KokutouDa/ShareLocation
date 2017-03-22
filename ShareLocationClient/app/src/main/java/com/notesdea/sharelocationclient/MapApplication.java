package com.notesdea.sharelocationclient;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by notes on 2017/2/5.
 */

public class MapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
