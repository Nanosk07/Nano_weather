package com.nano.nano_weather.DataBase;

import org.litepal.crud.DataSupport;

/**
 * Created by 26039 on 2018/4/17.
 */

public class City extends DataSupport {
    private int id;
    private String city;
    private int cityID;
    private int provinceID;

    public int getId() {
        return id;
    }

    public int getProvinceID() {
        return provinceID;
    }

    public void setProvinceID(int provinceID) {
        this.provinceID = provinceID;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getCityID() {
        return cityID;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }
}
