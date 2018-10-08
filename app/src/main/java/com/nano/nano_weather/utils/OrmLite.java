package com.nano.nano_weather.utils;


import com.litesuits.orm.LiteOrm;
import com.nano.nano_weather.Ui.MyApplication;


public class OrmLite {

    static LiteOrm sLiteOrm;

    public static LiteOrm getInstance() {
        getOrmHolder();
        return sLiteOrm;
    }

    private static OrmLite getOrmHolder() {
        return OrmHolder.sInstance;
    }

    private OrmLite() {
        if (sLiteOrm == null) {
            sLiteOrm = LiteOrm.newSingleInstance(MyApplication.getAppContext(), "cities.db");
        }
        sLiteOrm.setDebugged(Boolean.parseBoolean("true"));
    }

    private static class OrmHolder {
        private static final OrmLite sInstance = new OrmLite();
    }
}