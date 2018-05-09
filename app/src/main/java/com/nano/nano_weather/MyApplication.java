package com.nano.nano_weather;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePal;

/**
 * Created by 26039 on 2018/5/8.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
        LitePal.initialize(this);
    }
}
