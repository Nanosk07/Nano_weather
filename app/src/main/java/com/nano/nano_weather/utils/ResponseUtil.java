package com.nano.nano_weather.utils;

import com.google.gson.Gson;
import com.nano.nano_weather.DataBase.City;
import com.nano.nano_weather.DataBase.Country;
import com.nano.nano_weather.DataBase.Province;
import com.nano.nano_weather.json.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 26039 on 2018/4/17.
 */

public class ResponseUtil {

    //处理省级信息
    public static boolean provinceResponse(String response) {
        if (response != null) {
            try {
                JSONArray data_province = new JSONArray(response);
                for (int i = 0; i < data_province.length(); i++) {
                    JSONObject provinceObject = data_province.getJSONObject(i);
                    Province province = new Province();
                    province.setProvince(provinceObject.getString("name"));
                    province.setProvinceID(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;

    }

    //处理市级信息
    public static boolean cityResponse(String response, int provinceID) {
        if (response != null) {
            try {
                JSONArray data_city = new JSONArray(response);
                for (int i = 0; i < data_city.length(); i++) {
                    JSONObject cityObject = data_city.getJSONObject(i);
                    City city = new City();
                    city.setCity(cityObject.getString("name"));
                    city.setCityID(cityObject.getInt("id"));
                    city.setProvinceID(provinceID);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    //处理县级信息
    public static boolean countryResponse(String response, int cityID) {
        if (response != null) {
            try {
                JSONArray data_country = new JSONArray(response);
                for (int i = 0; i < data_country.length(); i++) {
                    JSONObject countryObject = data_country.getJSONObject(i);
                    Country country = new Country();
                    country.setCountry(countryObject.getString("name"));
                    country.setCountryID(countryObject.getInt("id"));
                    country.setWeatherID("weather_id");
                    country.setCityID(cityID);
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public static Weather WeatherResponse(String data) {
        if (data != null) {
            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
                String weatherData = jsonArray.getJSONObject(0).toString();
                return new Gson().fromJson(weatherData,Weather.class);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return null;
    }

}
