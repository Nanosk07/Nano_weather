package com.nano.nano_weather.Ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.Toast;
import com.nano.nano_weather.Adapter.ViewPageAdapter;
import com.nano.nano_weather.Fragment.MainFragment;
import com.nano.nano_weather.Fragment.MultiCityFragment;
import com.nano.nano_weather.R;
import com.nano.nano_weather.utils.OtherUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements MainFragment.MyCallBack{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.draw_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.coor_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.navi_view)
    NavigationView navigationView;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tablayout)
    TabLayout tabLayout;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    //    @BindView(R.id.pic_main)
    //    ImageView imageView;

    private int iconID;
    private String nameNotifi;
    private String tmpNotifi;
    private String weatherNotifi;

    private long exitTime = 0;

    private android.support.v4.app.FragmentManager fragmentManager;

    private MainFragment fragment;
    private MultiCityFragment multiCityFragment;
    private ViewPageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initPermission();
//        initPic();
        initNavigation();
        initView();
        initIcon();
    }

    //请求权限
    public void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionList = new ArrayList<>();
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissionList.isEmpty()) {
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
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
        String cityname = intent.getStringExtra("cityid");
        if (city != null) {
            if (cityname != null){
                toolbar.setTitle(cityname);
            } else {
                toolbar.setTitle(city);
            }
            MainFragment mainFragment = new MainFragment();
            MultiCityFragment fragment = new MultiCityFragment();
            Bundle bundle = new Bundle();
            bundle.putString("id", city);
            mainFragment.setArguments(bundle);
            adapter.replaceFragment(0,mainFragment);
            adapter.replaceFragment(1,fragment);
        }
    }




    @Override
    public void sendMessage(String cityName, String tmp, String weather, int code) {
        //回调Fragment数据
        toolbar.setTitle(cityName);
        nameNotifi = cityName;
        tmpNotifi = tmp;
        weatherNotifi = weather;
    }

    private void initNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.add_city:
                    startActivity(new Intent(MainActivity.this, ChooseCity.class));
                    drawerLayout.closeDrawers();
                    break;
                case R.id.map_city:
                    startActivity(new Intent(MainActivity.this, MapActivity.class));
                    drawerLayout.closeDrawers();
                    break;
                case R.id.multy_city:
                    viewPager.setCurrentItem(1);
                    drawerLayout.closeDrawers();
                    break;
                case R.id.about:
                    startActivity(new Intent(MainActivity.this,AboutActivity.class));
                    break;
                case R.id.setting:
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();
                    break;
            }
            return false;
        });
    }

    private void initView() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        toggle.syncState();
        drawerLayout.addDrawerListener(toggle);
        fragment = new MainFragment();
        multiCityFragment = new MultiCityFragment();
        adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.addTab(fragment, "主页面");
        adapter.addTab(multiCityFragment, "多城市");
        viewPager.setAdapter(adapter);
        FabVisibilityChangedListener listener = new FabVisibilityChangedListener();
        tabLayout.setupWithViewPager(viewPager, false);
        fab.hide();
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (fab.isShown()) {
                    listener.position = position;
                    fab.hide(listener);
                } else {
                    listener.position = position;
                    changeFabState(position);
                }
            }
        });
    }

    private class FabVisibilityChangedListener extends FloatingActionButton.OnVisibilityChangedListener {
        private int position;

        @Override
        public void onHidden(FloatingActionButton fab) {
            changeFabState(position);
            fab.hide();
        }
    }

    private void changeFabState(int position) {
        if (position == 1) {
            fab.show();
            fab.setImageResource(R.drawable.add);
            fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(MainActivity.this, R.color.colorWhite)));
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChooseCity.class);
                intent.putExtra("multi_check", true);
                startActivity(intent);
            });
        } else {
            fab.hide();
        }
    }

    public void initIcon(){
       OtherUtil.getInstance().putInt("999", R.mipmap.unkonw);
       OtherUtil.getInstance().putInt("100", R.mipmap.sunny);
       OtherUtil.getInstance().putInt("104", R.mipmap.overcast);
       OtherUtil.getInstance().putInt("101", R.mipmap.cloudy4);
       OtherUtil.getInstance().putInt("102", R.mipmap.cloudy2);
       OtherUtil.getInstance().putInt("103", R.mipmap.cloudy2);
       OtherUtil.getInstance().putInt("305", R.mipmap.light_rain);
       OtherUtil.getInstance().putInt("306", R.mipmap.light_rain);
       OtherUtil.getInstance().putInt("307", R.mipmap.light_rain);
       OtherUtil.getInstance().putInt("300", R.mipmap.light_rain);
       OtherUtil.getInstance().putInt("302", R.mipmap.tstorm3);
       OtherUtil.getInstance().putInt("502", R.mipmap.fog);
       OtherUtil.getInstance().putInt("501", R.mipmap.mist);
       OtherUtil.getInstance().putInt("503", R.mipmap.sand);
       OtherUtil.getInstance().putInt("504", R.mipmap.sand);
       OtherUtil.getInstance().putInt("507", R.mipmap.sand);
       OtherUtil.getInstance().putInt("508", R.mipmap.sand);
       OtherUtil.getInstance().putInt("400", R.mipmap.snow4);
       OtherUtil.getInstance().putInt("401", R.mipmap.snow4);
       OtherUtil.getInstance().putInt("402", R.mipmap.snow5);
       OtherUtil.getInstance().putInt("403", R.mipmap.snow5);
       OtherUtil.getInstance().putInt("404", R.mipmap.sleet);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                finish();
                System.exit(0);
            }

        }
    }
}
