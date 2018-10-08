package com.nano.nano_weather.Other;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import java.io.Closeable;
import java.io.IOException;

public class Util {

    /**
     * 只关注是否联网
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static String safeText(String msg) {
        return TextUtils.isEmpty(msg) ? "" : msg;
    }

    /**
     * 天气代码 100 为晴 101-213 500-901 为阴 300-406为雨
     *
     * @param code 天气代码
     * @return 天气情况
     */
    public static String getWeatherType(int code) {
        if (code == 100) {
            return "晴";
        }
        if ((code >= 101 && code <= 213) || (code >= 500 && code <= 901)) {
            return "阴";
        }
        if (code >= 300 && code <= 406) {
            return "雨";
        }
        return "错误";
    }

    /**
     * 匹配掉错误信息
     */
    public static String replaceCity(String city) {
        city = safeText(city).replaceAll("(?:省|市|自治区|特别行政区|地区|盟)", "");
        return city;
    }

    /**
     * 匹配掉无关信息
     */

    public static String replaceInfo(String city) {
        city = safeText(city).replace("API没有", "");
        return city;
    }

}