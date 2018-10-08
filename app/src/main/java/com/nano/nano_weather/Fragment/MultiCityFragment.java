package com.nano.nano_weather.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.litesuits.orm.db.assit.WhereBuilder;
import com.nano.nano_weather.Adapter.MultiCityAdapter;
import com.nano.nano_weather.DataBase.CityORM;

import com.nano.nano_weather.Other.C;
import com.nano.nano_weather.Other.MultiUpdateEvent;
import com.nano.nano_weather.Other.RetrofitSingleton;
import com.nano.nano_weather.Other.RxBus;
import com.nano.nano_weather.R;
import com.nano.nano_weather.Ui.DetailCityActivity;
import com.nano.nano_weather.json.AirQuality;
import com.nano.nano_weather.json.Weather;
import com.nano.nano_weather.utils.ApiInterface;
import com.nano.nano_weather.utils.DateUtil;
import com.nano.nano_weather.utils.OrmLite;
import com.nano.nano_weather.utils.RxUtil;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;



public class MultiCityFragment extends RxFragment{
    @BindView(R.id.recyclerview_multi)
    RecyclerView mRecyclerView;
    @BindView(R.id.swiperefresh_multi)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.empty)
    LinearLayout mLayout;

    private MultiCityAdapter mAdapter;
    private List<Weather> mWeathers;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_multi, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxBus.getDefault()
                .toObservable(MultiUpdateEvent.class)
                .doOnNext(event -> multiLoad())
                .subscribe();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        multiLoad();
    }

    private void initView() {
        mWeathers = new ArrayList<>();
        mAdapter = new MultiCityAdapter(mWeathers);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setMultiCityClick(new MultiCityAdapter.onMultiCityClick() {
            @Override
            public void longClick(String city) {
                new AlertDialog.Builder(getActivity())
                        .setMessage("是否删除该城市?")
                        .setPositiveButton("删除", (dialog, which) -> {
                            OrmLite.getInstance().delete(new WhereBuilder(CityORM.class).where("name=?", city));
                            multiLoad();
                            Snackbar.make(getView(), String.format(Locale.CHINA, "已经将%s删掉了", city), Snackbar.LENGTH_LONG)
                                    .setAction("撤销",
                                            v -> {
                                                OrmLite.getInstance().save(new CityORM(city));
                                                multiLoad();
                                            }).show();
                        })
                        .show();
            }

            @Override
            public void click(Weather weather) {
                Intent intent = new Intent(getActivity(),DetailCityActivity.class);
                intent.putExtra("weather", weather);
                startActivity(intent);
            }
        });

        if (mRefreshLayout != null) {
            mRefreshLayout.setColorSchemeResources(
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_blue_bright
            );
            mRefreshLayout.setOnRefreshListener(() -> mRefreshLayout.postDelayed(this::multiLoad, 1000));
        }
    }

    private void multiLoad() {
        mWeathers.clear();
        Observable.create((ObservableOnSubscribe<CityORM>) emitter -> {
            try {
                for (CityORM cityORM : OrmLite.getInstance().query(CityORM.class)) {
                    emitter.onNext(cityORM);
                }
                emitter.onComplete();
            } catch (Exception e) {
                emitter.onError(e);
            }
        }).doOnSubscribe(subscription -> mRefreshLayout.setRefreshing(true))
                .map(city -> DateUtil.replaceCity(city.getName()))//替换名称
                .distinct()
                .concatMap(cityName -> RetrofitSingleton.getInstance().fetchWeather(cityName))//异步执行
                .filter(weather -> !C.UNKNOWN_CITY.equals(weather.status))//过滤器
                .take(10)
                .compose(RxUtil.fragmentLifecycle(this))//防止内存泄漏
                .doOnNext(weather -> mWeathers.add(weather))
                .doOnComplete(() -> {
                    mRefreshLayout.setRefreshing(false);
                    mAdapter.notifyDataSetChanged();
                    if (mAdapter.isEmpty()) {
                        mLayout.setVisibility(View.VISIBLE);
                    } else {
                        mLayout.setVisibility(View.GONE);
                    }
                })
                .doOnError(error -> {
                    if (mAdapter.isEmpty() && mLayout != null) {
                        mLayout.setVisibility(View.VISIBLE);
                    }
                })
                .subscribe();
    }


}
