package com.nano.nano_weather.utils;

import com.nano.nano_weather.DataBase.City;
import com.nano.nano_weather.DataBase.Country;
import com.nano.nano_weather.DataBase.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 26039 on 2018/4/17.
 * 参考
 */

public class ResponseUtil {

    //处理省级信息
    public static boolean provinceResponce(String responce){
        if(responce!=null){
            try {
                JSONArray data_province = new JSONArray(responce);
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
    public static boolean cityResponse(String responce,int provinceID){
        if(responce!=null){
            try {
                JSONArray data_city = new JSONArray(responce);
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
    public static boolean countryResponse(String responce,int cityID){
        if(responce!=null){
            try {
                JSONArray data_country = new JSONArray(responce);
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

}
