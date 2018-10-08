package com.nano.nano_weather.Ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.nano.nano_weather.Fragment.MainFragment;
import com.nano.nano_weather.R;
import com.nano.nano_weather.json.Weather;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DetailCityActivity extends AppCompatActivity{
    @BindView(R.id.frame_detail)
    FrameLayout frameLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.coor_detail)
    CoordinatorLayout coordinatorLayout;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        ButterKnife.bind(this);
        initData();
        Log.e("a","initdata");
    }

    private void initData() {
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Weather weather = (Weather) intent.getSerializableExtra("weather");
        if (weather == null) {
            finish();
        }
        toolbar.setTitle(weather.basic.location);
        MainFragment mainFragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("id", weather.basic.city);
        bundle.putString("detail","detail");
        mainFragment.setArguments(bundle);
        fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_detail, mainFragment).commit();


//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        WeatherAdapter mAdapter = new WeatherAdapter(weather);
//        recyclerView.setAdapter(mAdapter);
    }

    public static void launch(Context context, Weather weather) {
        Intent intent = new Intent(context, DetailCityActivity.class);
        intent.putExtra("weather", weather);
        context.startActivity(intent);
    }
}
