package com.nano.nano_weather.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.nano.nano_weather.Ui.MyApplication;


public class OtherUtil {
    private SharedPreferences mPrefs;

    public static OtherUtil getInstance() {
        return SPHolder.sInstance;
    }

    private OtherUtil() {
        mPrefs = MyApplication.getAppContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
    }

    private static class SPHolder {
        private static final OtherUtil sInstance = new OtherUtil();
    }
    public int getInt(String key, int defValue) {
        return mPrefs.getInt(key, defValue);
    }
    public OtherUtil putInt(String key, int value) {
        mPrefs.edit().putInt(key, value).apply();
        return this;
    }
    public OtherUtil putString(String key, String value) {
        mPrefs.edit().putString(key, value).apply();
        return this;
    }
    public String getString(String key, String defValue) {
        return mPrefs.getString(key, defValue);
    }

}
