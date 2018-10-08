package com.nano.nano_weather.utils;

import android.text.TextUtils;

/**
 * Created by 26039 on 2018/4/19.
 */

public class DateUtil {
    public static String DateConvert(String date){
        String s = date.substring(6,10);
        return s;
    }
    /**
     * 匹配掉错误信息
     */
    public static String replaceCity(String city) {
        city = safeText(city).replaceAll("(?:省|市|自治区|特别行政区|地区|盟)", "");
        return city;
    }
    public static String safeText(String msg) {
        return TextUtils.isEmpty(msg) ? "" : msg;
    }
}
