package com.nano.nano_weather.Ui;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

import org.litepal.LitePal;

/**
 * Created by 26039 on 2018/5/8.
 */

public class MyApplication extends Application{

    private static Context appContext;
    private static String sCacheDir;
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();
        SDKInitializer.initialize(this);
        LitePal.initialize(this);
        if (getApplicationContext().getExternalCacheDir() != null && ExistSDCard()) {
            sCacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            sCacheDir = getApplicationContext().getCacheDir().toString();
        }
    }
    private boolean ExistSDCard() {
        return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    }
    public static Context getAppContext() {
        return appContext;
    }
    public static String getAppCacheDir() {
        return sCacheDir;
    }
}
