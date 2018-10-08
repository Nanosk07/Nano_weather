package com.nano.nano_weather.Adapter;

import android.support.v4.content.ContextCompat;
import android.view.View;

import com.nano.nano_weather.R;

import java.util.HashMap;
import java.util.Map;

public class CardCityAdapter {


    void applyStatus(int code, View view) {
        if (code >= 300 && code < 408) {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg));
        } else if (code > 100 && code < 300) {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg));
        } else {
            view.setBackground(ContextCompat.getDrawable(view.getContext(), R.drawable.bg));
        }
    }
}

