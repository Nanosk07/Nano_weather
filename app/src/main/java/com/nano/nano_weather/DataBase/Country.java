package com.nano.nano_weather.DataBase;

import org.litepal.crud.DataSupport;

/**
 * Created by 26039 on 2018/4/17.
 */

public class Country extends DataSupport {
    private int id;
    private String country;
    private String weatherID;
    private int cityID;

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public String getWeatherID() {
        return weatherID;
    }

    public void setWeatherID(String weatherID) {
        this.weatherID = weatherID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    private int countryID;
}
