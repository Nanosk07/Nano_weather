package com.nano.nano_weather;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.internal.Utils;

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
