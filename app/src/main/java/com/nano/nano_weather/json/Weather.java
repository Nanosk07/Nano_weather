package com.nano.nano_weather.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 26039 on 2018/4/19.
 */

public class Weather {
    public String status;
    public Now now;
    public Basic basic;
    @SerializedName("daily_forecast")
    public List<DailyForecast> daily_forecasts;
    @SerializedName("lifestyle")
    public List<LifeStyle> lifeStyles;
}
