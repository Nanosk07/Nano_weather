package com.nano.nano_weather.DataBase;

import org.litepal.crud.DataSupport;

/**
 * Created by 26039 on 2018/4/17.
 */

public class Province extends DataSupport {
    private int id;
    private String province;
    private int ProvinceID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getProvinceID() {
        return ProvinceID;
    }

    public void setProvinceID(int provinceCode) {
        ProvinceID = provinceCode;
    }
}
