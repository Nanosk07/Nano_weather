package com.nano.nano_weather.Fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.nano.nano_weather.R;
import com.nano.nano_weather.Ui.MainActivity;
import com.nano.nano_weather.json.AirQuality;
import com.nano.nano_weather.json.DailyForecast;
import com.nano.nano_weather.json.Weather;
import com.nano.nano_weather.utils.AqiResponseUtil;
import com.nano.nano_weather.utils.DateUtil;
import com.nano.nano_weather.utils.HttpUtil;
import com.nano.nano_weather.utils.OtherUtil;
import com.nano.nano_weather.utils.ResponseUtil;


import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;

import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class MainFragment extends Fragment {

    @BindView(R.id.swipe)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.icon_now_weather)
    ImageView icon_weather_image;
    @BindView(R.id.tmp_now_weather)
    TextView tmp_text;
    @BindView(R.id.txt_now_weather)
    TextView txt_text;
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

    private String weatherID;
    private LinearLayout life_linear;
    private int i = 1;
    private MyCallBack callBack;
    private LocationClient mLocationClient;
    private String city;
    private Bundle bundle;
    private View view;
    private SharedPreferences sharedPreferences;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MyCallBack) {
            callBack = (MyCallBack) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.main_fragment, container, false);
        ButterKnife.bind(this, view);
        life_linear = view.findViewById(R.id.life_forecast);
        bundle = getArguments();
        if (bundle == null) {
            initLocation();
        } else {
            requestWeather(bundle.getString("id"));
            swipeRefreshLayout.setOnRefreshListener(() -> requestWeather(bundle.getString("id")));
        }
        scrollView.setVisibility(View.INVISIBLE);

        return view;

    }

    private void initLocation() {

        MyLocationListener myListener = new MyLocationListener();
        mLocationClient = new LocationClient(getContext());
        //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //高精度模式
        option.setIsNeedAddress(true);

        option.setOpenGps(true);//打开Gps
        option.setScanSpan(2000);//2000毫秒定位一次
        mLocationClient.setLocOption(option);
        //mLocationClient为第二步初始化过的LocationClient对象
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.start();

    }

    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            city = location.getCity();    //获取城市

            Log.e("t", city);
            if (city == null) {
                Toast.makeText(getActivity(), "定位失败，使用默认城市", Toast.LENGTH_SHORT).show();
                city = "成都";
            }else {
                swipeRefreshLayout.setOnRefreshListener(() -> requestWeather(city));
                if (i == 1) {
                    swipeRefreshLayout.post(() -> {
                        swipeRefreshLayout.setRefreshing(true);
                        requestWeather(city);
                    });
                }
                i++;
            }
            if (mLocationClient != null) {
                mLocationClient.stop();
            }
        }
    }

    private void requestWeather(String weatherID) {
        String url1 = "https://free-api.heweather.com/s6/weather?" +
                "location=" + weatherID + "&" +
                "key=e9e2bee55f2a49968ccafaadfea7918e" + "&" +
                "lang=ch" + "&" +
                "unit=m";
        String url2 = "https://free-api.heweather.com/s6/air/now?" +
                "location=" + weatherID + "&" +
                "key=e9e2bee55f2a49968ccafaadfea7918e" + "&" +
                "lang=ch" + "&" +
                "unit=m";
        HttpUtil.sendOKHttpRequest(url1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                Weather weather = ResponseUtil.WeatherResponse(data);
                getActivity().runOnUiThread(() -> {
                    if (weather != null) {
                        showWeather(weather, null);
                    }
                    swipeRefreshLayout.setRefreshing(false);
                });

            }
        });
        HttpUtil.sendOKHttpRequest(url2, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getContext(), "请求数据失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String data = response.body().string();
                AirQuality airQuality = AqiResponseUtil.airQuality(data);
                getActivity().runOnUiThread(() -> {
                    if (airQuality != null) {
                        showWeather(null, airQuality);
                    }
                });
            }
        });

    }
    private void initNotification(int icon,String name,String weather,String tmp) {
        sharedPreferences = getActivity().getSharedPreferences("appSetting", MODE_PRIVATE);
        boolean isOpen = sharedPreferences.getBoolean("isOpen", false);
        if (isOpen) {
            NotificationManager manger = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("1", "channel1", NotificationManager.IMPORTANCE_DEFAULT);
                manger.createNotificationChannel(channel);
            }
            Intent intent = new Intent(getActivity(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), "1");//与channelId对应
            //icon title text必须包含，不然影响桌面图标小红点的展示
            builder.setSmallIcon(icon)
                    .setContentTitle(name)
                    .setContentText(weather + " " + tmp)
                    .setContentIntent(pendingIntent);//设置在通知栏中点击该信息之后的跳转，参数是一个pendingIntent
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            manger.notify(233, notification);
        }
    }

    private void showWeather(@Nullable Weather weather, @Nullable AirQuality airQuality) {
        if (weather != null) {
            String cityName = weather.basic.location;
            String temp = weather.now.tmp + "℃";
            String hum = "降水量：" + weather.now.hum + "mm";
            String txt = weather.now.cond_txt;
            int code = weather.now.cond_code;
            initNotification(R.mipmap.logo,weather.basic.city,txt,temp);
            if (bundle == null||bundle.getString("weather_id")!=null) {
                callBack.sendMessage(cityName, temp, txt, code);
            }
            OtherUtil.getInstance().putString("city",weather.basic.city);
            OtherUtil.getInstance().putString("tmp",temp);
            OtherUtil.getInstance().putString("txt",txt);
            OtherUtil.getInstance().putInt("id",R.mipmap.logo);
            tmp_text.setText(temp);
            tmp_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            hum_text.setText(hum);
            txt_text.setText(txt);
            if (weather.now.cond_code == 100) {
                icon_weather_image.setImageResource(R.mipmap.sunny);
            }
            if (weather.now.cond_code == 101) {
                icon_weather_image.setImageResource(R.mipmap.cloudy4);
            }
            if (weather.now.cond_code == 102 || weather.now.cond_code == 103) {
                icon_weather_image.setImageResource(R.mipmap.cloudy2);
            }
            if (weather.now.cond_code == 104) {
                icon_weather_image.setImageResource(R.mipmap.overcast);
            }
            if (weather.now.cond_code >= 200 && weather.now.cond_code <= 213) {
                icon_weather_image.setImageResource(R.mipmap.windy);
            }
            if (weather.now.cond_code == 300 || weather.now.cond_code == 301) {
                icon_weather_image.setImageResource(R.mipmap.light_rain);
            }
            if (weather.now.cond_code == 302 || weather.now.cond_code == 303) {
                icon_weather_image.setImageResource(R.mipmap.tstorm3);
            }
            if (weather.now.cond_code == 304) {
                icon_weather_image.setImageResource(R.mipmap.hail);
            }
            if (weather.now.cond_code >= 305 && weather.now.cond_code <= 313) {
                icon_weather_image.setImageResource(R.mipmap.light_rain);
            }
            if (weather.now.cond_code == 400 || weather.now.cond_code == 401) {
                icon_weather_image.setImageResource(R.mipmap.snow4);
            }
            if (weather.now.cond_code == 402 || weather.now.cond_code == 403) {
                icon_weather_image.setImageResource(R.mipmap.snow5);
            }
            if (weather.now.cond_code >= 404 && weather.now.cond_code <= 406) {
                icon_weather_image.setImageResource(R.mipmap.sleet);
            }
            if (weather.now.cond_code == 407) {
                icon_weather_image.setImageResource(R.mipmap.snow4);
            }
            if (weather.now.cond_code == 500 || weather.now.cond_code == 501) {
                icon_weather_image.setImageResource(R.mipmap.fog);
            }
            if (weather.now.cond_code == 502) {
                icon_weather_image.setImageResource(R.mipmap.fog);
            }
            if(weather.now.cond_code>=503 && weather.now.cond_code<=508){
                icon_weather_image.setImageResource(R.mipmap.sand);
            }
            if (weather.now.cond_code >= 900 && weather.now.cond_code <= 999) {
                icon_weather_image.setImageResource(R.mipmap.unkonw);
            }

            daily_linear.removeAllViews();
            for (int j = 0; j < weather.daily_forecasts.size(); j++) {
                View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.item_daily_forecast, daily_linear, false);
                ImageView image_daily = view1.findViewById(R.id.imageview_daily);
                TextView date_text = view1.findViewById(R.id.date_daily);
                TextView maxtemp_text = view1.findViewById(R.id.temp_daily);
                TextView pop_text = view1.findViewById(R.id.pop_daily);
                if (weather.now.cond_code == 100) {
                    image_daily.setImageResource(R.mipmap.sunny);
                }
                if (weather.now.cond_code == 101) {
                    image_daily.setImageResource(R.mipmap.cloudy4);
                }
                if (weather.now.cond_code == 102 || weather.now.cond_code == 103) {
                    image_daily.setImageResource(R.mipmap.cloudy2);
                }
                if (weather.now.cond_code == 104) {
                    image_daily.setImageResource(R.mipmap.overcast);
                }
                if (weather.now.cond_code >= 200 && weather.now.cond_code <= 213) {
                    image_daily.setImageResource(R.mipmap.windy);
                }
                if (weather.now.cond_code == 300 || weather.now.cond_code == 301) {
                    image_daily.setImageResource(R.mipmap.light_rain);
                }
                if (weather.now.cond_code == 302 || weather.now.cond_code == 303) {
                    image_daily.setImageResource(R.mipmap.tstorm3);
                }
                if (weather.now.cond_code == 304) {
                    image_daily.setImageResource(R.mipmap.hail);
                }
                if (weather.now.cond_code >= 305 && weather.now.cond_code <= 313) {
                    image_daily.setImageResource(R.mipmap.light_rain);
                }
                if (weather.now.cond_code == 400 || weather.now.cond_code == 401) {
                    image_daily.setImageResource(R.mipmap.snow4);
                }
                if (weather.now.cond_code == 402 || weather.now.cond_code == 403) {
                    image_daily.setImageResource(R.mipmap.snow5);
                }
                if (weather.now.cond_code >= 404 && weather.now.cond_code <= 406) {
                    image_daily.setImageResource(R.mipmap.sleet);
                }
                if (weather.now.cond_code == 407) {
                    image_daily.setImageResource(R.mipmap.snow4);
                }
                if (weather.now.cond_code == 500 || weather.now.cond_code == 501) {
                    image_daily.setImageResource(R.mipmap.fog);
                }
                if (weather.now.cond_code >= 502 && weather.now.cond_code <= 508) {
                    image_daily.setImageResource(R.mipmap.overcast);
                }
                if (weather.now.cond_code >= 900 && weather.now.cond_code <= 999) {
                    image_daily.setImageResource(R.mipmap.unkonw);
                }
                maxtemp_text.setText(weather.daily_forecasts.get(j).tmp_min + "~" + weather.daily_forecasts.get(j).tmp_max + "℃");
                pop_text.setText(weather.daily_forecasts.get(j).pop + "%");
                String date = DateUtil.DateConvert(weather.daily_forecasts.get(j).date);
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
        if (airQuality != null) {
            if (airQuality.aqi.pm25 != null && airQuality.aqi.aqi != null) {
                qlit_text.setText("空气" + airQuality.aqi.qlty + "  " + airQuality.aqi.aqi);
                aqi_text.setText("PM2.5  " + airQuality.aqi.pm25);
            } else {
                Toast.makeText(getContext(), "空气质量信息获取失败", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public interface MyCallBack {
        void sendMessage(String cityName, String tmp, String weather, int code);
    }
}
