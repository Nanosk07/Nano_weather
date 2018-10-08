package com.nano.nano_weather.Ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;


public class StartActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //延时2秒跳转主界面
        new Handler(msg -> {
            startActivity(new Intent(StartActivity.this, MainActivity.class));
            return false;
        }).sendEmptyMessageDelayed(0, 2000);

    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
