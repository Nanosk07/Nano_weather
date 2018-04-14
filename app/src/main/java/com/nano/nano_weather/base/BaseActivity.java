package com.nano.nano_weather.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import butterknife.ButterKnife;

public abstract class BaseActivity extends RxAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());
        ButterKnife.bind(this);
    }
    public abstract int layoutId();

    public void setTheme(AppCompatActivity appCompatActivity){
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        appCompatActivity.getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
        appCompatActivity.recreate();
    }
}
