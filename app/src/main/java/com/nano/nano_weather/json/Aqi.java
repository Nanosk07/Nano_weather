package com.nano.nano_weather.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 26039 on 2018/4/19.
 */

public class Aqi {
    @SerializedName("pm25")
    public String pm25;
    @SerializedName("pm10")
    public String pm10;
    @SerializedName("qlty")
    public String qlty;
    @SerializedName("aqi")
    public String aqi;
}
