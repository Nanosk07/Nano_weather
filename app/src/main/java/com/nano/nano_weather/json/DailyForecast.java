package com.nano.nano_weather.json;

import java.io.Serializable;

/**
 * Created by 26039 on 2018/4/19.
 */

public class DailyForecast implements Serializable{
    public String cond_txt_d;
    public String date;
    public String tmp_max;
    public String tmp_min;
    //降水概率
    public String pop;
}
