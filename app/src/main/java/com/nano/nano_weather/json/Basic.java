package com.nano.nano_weather.json;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 26039 on 2018/4/19.
 */

public class Basic implements Serializable{
    @SerializedName("parent_city")
    public String city;
    @SerializedName("location")
    public String location;
}
