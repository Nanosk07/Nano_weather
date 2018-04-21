package com.nano.nano_weather.utils;

import com.google.gson.Gson;
import com.nano.nano_weather.json.AirQuality;
import com.nano.nano_weather.json.Aqi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 26039 on 2018/4/21.
 */

public class AqiResponseUtil {
    public static AirQuality airQuality(String data) {
        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weatherData = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weatherData, AirQuality.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }
}
