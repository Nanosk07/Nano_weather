package com.nano.nano_weather;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.nano.nano_weather.Fragment.MainFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MainFragment.MyCallBack {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.draw_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.coor_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.navi_view)
    NavigationView navigationView;
//    @BindView(R.id.pic_main)
//    ImageView imageView;
    private String city;
    private int iconID;
    private String nameNotifi;
    private String tmpNotifi;
    private String weatherNotifi;

    private LocationClient mLocationClient;
    private long exitTime = 0;

    private android.support.v4.app.FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPermission();
//        initPic();
        initNavigation();
        initToolbar();
        initLocation();

    }

//    private void initPic() {
//        String url = "https://cn.bing.com/az/hprichbg/rb/GrandPrismatic_ZH-CN10343735220_1920x1080.jpg";
//        HttpUtil.sendOKHttpRequest(url, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Toast.makeText(MainActivity.this,"图片加载失败",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Glide.with(MainActivity.this).load(url).into(imageView);
//                    }
//                });
//            }
//        });
//    }

    public void handleWeatherID(String city){
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id",city);
        mainFragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.coor_layout,mainFragment).commit();
    }

    //请求权限
    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionList=new ArrayList<>();
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(!permissionList.isEmpty()){
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (int grantResult : grantResults
                ) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限申请失败", Toast.LENGTH_SHORT).show();
                openAppDetails();
                break;
            }
        }

    }

    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", (dialog, which) -> {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            startActivity(intent);
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String city = intent.getStringExtra("weather_id");
        if(city != null){
            toolbar.setTitle(city);
            MainFragment mainFragment = new MainFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id",city);
            mainFragment.setArguments(bundle);
            fragmentManager = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.coor_layout,mainFragment).commit();
        }
    }

    private void initLocation() {

        MyLocationListener myListener = new MyLocationListener();
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //高精度模式
        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        option.setOpenGps(true);//打开Gps
        option.setScanSpan(2000);//2000毫秒定位一次
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.start();

    }

    @Override
    public void sendMessage(String cityName,String tmp,String weather,int code) {
        nameNotifi = cityName;
        tmpNotifi = tmp;
        weatherNotifi = weather;

        if (code == 100) {
            iconID = R.mipmap.sunny;
        }else
        if (code == 101) {
            iconID = R.mipmap.cloudy4;
        }else
        if (code == 102 || code == 103) {
            iconID = R.mipmap.cloudy2;
        }else
        if (code == 104) {
            iconID = R.mipmap.overcast;
        }else
        if (code >= 200 && code <= 213) {
            iconID = R.mipmap.windy;
        }else
        if (code == 300 || code == 301) {
            iconID = R.mipmap.shower2;
        }else
        if (code == 302 || code == 303) {
            iconID = R.mipmap.tstorm3;
        }else
        if (code == 304) {
            iconID = R.mipmap.hail;
        }else
        if (code >= 305 && code <= 313) {
            iconID = R.mipmap.shower3;
        }else
        if (code == 400 || code == 401) {
            iconID = R.mipmap.snow4;
        }else
        if (code == 402 || code == 403) {
            iconID = R.mipmap.snow5;
        }else
        if (code >= 404 && code <= 406) {
            iconID = R.mipmap.sleet;
        }else
        if (code == 407) {
            iconID = R.mipmap.snow3;
        }else
        if (code == 500 || code == 501) {
            iconID = R.mipmap.fog;
        }else
        if (code >= 502 && code <= 508) {
            iconID = R.mipmap.overcast;
        }else
        if (code >= 900 && code <= 999) {
            iconID = R.mipmap.unkonw;
        }
    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取地址相关的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            String addr = location.getAddrStr();    //获取详细地址信息
            String country = location.getCountry();    //获取国家
            String province = location.getProvince();    //获取省份
            city = location.getCity();    //获取城市
            String district = location.getDistrict();    //获取区县
            String street = location.getStreet();    //获取街道信息
            Log.e("t", city);
            if (city==null){
                Toast.makeText(MainActivity.this,"定位失败，使用默认城市",Toast.LENGTH_SHORT).show();
                toolbar.setTitle("成都");
            }else {
                toolbar.setTitle(city);
                handleWeatherID(city);
                if (mLocationClient != null) {
                    mLocationClient.stop();
                }
            }
        }
    }

    private void initNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.add_city:
                    startActivity(new Intent(MainActivity.this,ChooseCity.class));
                case R.id.map_city:
                    startActivity(new Intent(MainActivity.this,MapActivity.class));
                case R.id.multy_city:
//                    startActivity(new Intent(MainActivity.this,MapActivity.class));
                case R.id.about:

                case R.id.setting:
                    Intent intent = new Intent();
                    intent.putExtra("city",nameNotifi);
                    intent.putExtra("tmp",tmpNotifi);
                    intent.putExtra("weather",weatherNotifi);
                    intent.putExtra("icon",iconID);
                    intent.setClass(MainActivity.this,SettingActivity.class);
                    startActivity(intent);

            }
            return false;
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
    }


    @Override
    public void onBackPressed() {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else {
            if (System.currentTimeMillis() - exitTime>2000){
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else{
                finish();
                System.exit(0);
            }

        }
    }

}
