package com.nano.nano_weather.utils;

import com.nano.nano_weather.json.WeatherAPI;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {

    String HOST = "https://free-api.heweather.com/s6/";

    @GET("weather")
    Observable<WeatherAPI> mWeatherAPI(@Query("location") String location, @Query("key") String key);

}
