package com.nano.nano_weather.modules.main.ui;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toolbar;

import com.nano.nano_weather.R;
import com.nano.nano_weather.base.BaseActivity;

import butterknife.BindView;

public class MainActivity extends BaseActivity implements NavigationView.OnClickListener {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.coor_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.toolbar)
    android.support.v7.widget.Toolbar toolbar;
    @BindView(R.id.tableLayout)
    TableLayout tableLayout;
    @BindView(R.id.float_bar)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.navi_view)
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public int layoutId() {
        return 0;
    }

    public static void launch(Context context){
        context.startActivity(new Intent(context,MainActivity.class));
    }

    @Override
    public void onClick(View v) {

    }
}
