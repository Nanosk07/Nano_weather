package com.nano.nano_weather.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 26039 on 2018/5/25.
 */

public class WeatherAPI {

    @SerializedName("HeWeather6")
    @Expose
    public List<Weather> mWeathers = new ArrayList<>();
}
