package com.nano.nano_weather.Ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.CheckBox;

import com.nano.nano_weather.R;
import com.nano.nano_weather.utils.OtherUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingActivity extends AppCompatActivity {

    private String cityName;
    private String weather;
    private String tmp;
    private int iconID;
    @BindView(R.id.checkbox_set)
    CheckBox checkBox;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private NotificationManager manger;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolbar();
        sharedPreferences = getSharedPreferences("appSetting",MODE_PRIVATE);
//        Intent intent = getIntent();
//        if (intent != null){
//            cityName = intent.getStringExtra("city");
//            weather = intent.getStringExtra("weather");
//            tmp = intent.getStringExtra("tmp");
//            iconID = intent.getIntExtra("icon",R.mipmap.unkonw);
//        }
        cityName = OtherUtil.getInstance().getString("city","成都");
        weather = OtherUtil.getInstance().getString("txt","晴");
        tmp = OtherUtil.getInstance().getString("tmp","30");
        iconID = OtherUtil.getInstance().getInt("id",R.mipmap.logo);
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                initNotification();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isOpen",true);
                editor.apply();
            }else{
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isOpen",false);
                editor.apply();
                if (manger != null){
                    manger.cancelAll();
                }
            }
        });
        boolean isOpen = sharedPreferences.getBoolean("isOpen",false);
        checkBox.setChecked(isOpen);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("设置");
        toolbar.setNavigationOnClickListener(view -> finish());

    }
    private void initNotification() {
        manger = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "channel1", NotificationManager.IMPORTANCE_DEFAULT);
            manger.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"1");//与channelId对应
        //icon title text必须包含，不然影响桌面图标小红点的展示
        builder.setSmallIcon(iconID)
                .setContentTitle(cityName)
                .setContentText(weather+" "+tmp)
                .setContentIntent(pendingIntent);//设置在通知栏中点击该信息之后的跳转，参数是一个pendingIntent
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        manger.notify(233, notification);
    }
}
