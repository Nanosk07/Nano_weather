package com.nano.nano_weather;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolbar();
        Intent intent = getIntent();
        if (intent != null){
            cityName = intent.getStringExtra("city");
            weather = intent.getStringExtra("weather");
            tmp = intent.getStringExtra("tmp");
            iconID = intent.getIntExtra("icon",R.mipmap.unkonw);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    initNotification();
                }else if (manger != null){
                    manger.cancelAll();
                }
            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitle("设置");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void initNotification() {
        manger = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("1", "channel1", NotificationManager.IMPORTANCE_DEFAULT);
            manger.createNotificationChannel(channel);
        }
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);

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
