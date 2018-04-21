package com.nano.nano_weather.json;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by 26039 on 2018/4/19.
 */

public class Now implements Serializable{
    @SerializedName("tmp")
    public int tmp;
    @SerializedName("cond_txt")
    public String cond_txt;
    @SerializedName("hum")
    public String hum;
    @SerializedName("cond_code")
    public int cond_code;
}
