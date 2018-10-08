package com.nano.nano_weather.json;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 26039 on 2018/4/21.
 */

public class AirQuality {
    @SerializedName("air_now_city")
    public Aqi aqi;
    @SerializedName("status")
    public String status;
}
