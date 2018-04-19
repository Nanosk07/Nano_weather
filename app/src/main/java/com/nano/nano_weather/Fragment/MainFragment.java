package com.nano.nano_weather.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.nano.nano_weather.MainActivity;
import com.nano.nano_weather.R;
import com.nano.nano_weather.json.DailyForecast;
import com.nano.nano_weather.json.LifeStyle;
import com.nano.nano_weather.json.Weather;
import com.nano.nano_weather.utils.DateUtil;
import com.nano.nano_weather.utils.HttpUtil;
import com.nano.nano_weather.utils.ResponseUtil;


import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.Response;


public class MainFragment extends Fragment {

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.icon_weather)
    ImageView icon_weather_image;
    @BindView(R.id.tmp_now_weather)
    TextView tmp_text;
    @BindView(R.id.hum_now_weather)
    TextView hum_text;
    @BindView(R.id.qlit_now_weather)
    TextView qlit_text;
    @BindView(R.id.aqi_now_weather)
    TextView aqi_text;
    @BindView(R.id.daily_forecast)
    LinearLayout daily_linear;
    @BindView(R.id.comf_image_life)
    ImageView comf_image;
    @BindView(R.id.drsg_image_life)
    ImageView drsg_image;
    @BindView(R.id.sport_image_life)
    ImageView sport_image;
    @BindView(R.id.air_image_life)
    ImageView air_image;
    @BindView(R.id.uv_image_life)
    ImageView uv_image;
    @BindView(R.id.comf_text_life)
    TextView comf_text;
    @BindView(R.id.drsg_text_life)
    TextView drsg_text;
    @BindView(R.id.sport_text_life)
    TextView sport_text;
    @BindView(R.id.air_text_life)
    TextView air_text;
    @BindView(R.id.uv_text_life)
    TextView uv_text;
//    @BindView(R.id.life_forecast)
//    LinearLayout life_linear;


    private String weatherID;
    private LinearLayout life_linear;
    private int i = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        life_linear = view.findViewById(R.id.life_forecast);

        weatherID = getArguments().getString("id");
        scrollView.setVisibility(View.INVISIBLE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherID);
            }
        });
        if (i == 1){
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                swipeRefreshLayout.setRefreshing(true);
                requestWeather(weatherID);
                }
            });
        }
        i++;
        return view;

    }

    private void requestWeather(String weatherID) {
        String url = "https://free-api.heweather.com/s6/weather?" +
                "location=" + weatherID + "&" +
                "key=e9e2bee55f2a49968ccafaadfea7918e" + "&" +
                "lang=en" + "&" +
                "unit=m";
        HttpUtil.sendOKHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Weather weather = ResponseUtil.WeatherResponse(data);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null) {
                            showWeather(weather);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });

            }
        });

    }

    //不能在主线程更新UI
    private void showWeather(Weather weather) {

        String cityName = weather.basic.city;
        String temp = weather.now.tmp + "℃";
        String hum = "降水量：" + weather.now.hum + "mm";
        String aqi;
        tmp_text.setText(temp);
        tmp_text.setTextSize(25);
        hum_text.setText(hum);

        daily_linear.removeAllViews();
        for (DailyForecast dailyForecast : weather.daily_forecasts) {
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_daily_forecast, daily_linear, false);
            ImageView image_daily = view1.findViewById(R.id.imageview_daily);
            TextView date_text = view1.findViewById(R.id.date_daily);
            TextView maxtemp_text = view1.findViewById(R.id.temp_daily);
            TextView pop_text = view1.findViewById(R.id.pop_daily);
            image_daily.setImageResource(R.drawable.ic_menu_camera);
            maxtemp_text.setText(dailyForecast.tmp_min+"~"+dailyForecast.tmp_max+"℃");
            pop_text.setText(dailyForecast.pop+"%");
            String date = DateUtil.DateConvert(dailyForecast.date);
            date_text.setText(date);
            daily_linear.addView(view1);
        }

        String comf = weather.lifeStyles.get(0).txt;
        String drsg = weather.lifeStyles.get(1).txt;
        String sprot = weather.lifeStyles.get(3).txt;
        String air = weather.lifeStyles.get(7).txt;
        String uv = weather.lifeStyles.get(5).txt;
        comf_text.setText(comf);
        drsg_text.setText(drsg);
        sport_text.setText(sprot);
        air_text.setText(air);
        uv_text.setText(uv);
        scrollView.setVisibility(View.VISIBLE);

    }
}
