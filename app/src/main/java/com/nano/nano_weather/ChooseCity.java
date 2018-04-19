package com.nano.nano_weather;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.nano.nano_weather.Adapter.Adapter_Choose;
import com.nano.nano_weather.DataBase.City;
import com.nano.nano_weather.DataBase.Country;
import com.nano.nano_weather.DataBase.Province;
import com.nano.nano_weather.utils.HttpUtil;
import com.nano.nano_weather.utils.ResponseUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseCity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTRY = 2;

    private ProgressDialog progressDialog;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;

    private Province selectedProvince;
    private City selectedCity;
    private Country selectedCountry;

    private int currentLevel;

    private RecyclerView.LayoutManager layoutManager;

    private List<String> data_list = new ArrayList<>();
    private Adapter_Choose adapter;

    @BindView(R.id.recy_choose)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar_choose;

    public ChooseCity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        ButterKnife.bind(this);
        initRecycler();
        initToolBar();
    }

    private void initRecycler() {
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter = new Adapter_Choose(data_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickLitener((view, position) -> {
            if (currentLevel == LEVEL_PROVINCE) {
                selectedProvince = provinceList.get(position);
                Log.d("TAG", "QueryCity");
                queryCity();
            } else if (currentLevel == LEVEL_CITY) {
                selectedCity = cityList.get(position);
                queryCountry();
            }else if (currentLevel == LEVEL_COUNTRY){
                String weatherId = countryList.get(position).getWeatherID();
                Intent intent =new Intent(ChooseCity.this,MainActivity.class);
                intent.putExtra("weather_id",weatherId);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initToolBar() {
        setSupportActionBar(toolbar_choose);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar_choose.setTitle("选择城市");
        toolbar_choose.setNavigationOnClickListener(view -> {
            if (currentLevel ==LEVEL_PROVINCE){
                finish();
            }
            else if (currentLevel == LEVEL_COUNTRY) {
                queryCity();
            } else if (currentLevel == LEVEL_CITY) {
                queryProvince();
            }
        });
        queryProvince();
    }


    /**
     * 查询省份
     */
    private void queryProvince() {
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) {
            data_list.clear();
            for (Province province : provinceList) {
                data_list.add(province.getProvince());
            }
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }
    /**
     * 查询城市
     */
    private void queryCity() {
        toolbar_choose.setTitle(selectedProvince.getProvince());
        cityList = DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            data_list.clear();
            for (City city : cityList) {
                data_list.add(city.getCity());
            }
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            currentLevel = LEVEL_CITY;
        } else {
            int provinceCode = selectedProvince.getProvinceID();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    } /**
     * 查询县城
     */
    private void queryCountry() {
        toolbar_choose.setTitle(selectedCity.getCity());
        countryList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(Country.class);
        if (countryList.size() > 0) {
            data_list.clear();
            for (Country country : countryList) {
                data_list.add(country.getCountry());
            }
            adapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(0);
            currentLevel = LEVEL_COUNTRY;
        } else {
            int provinceCode = selectedProvince.getProvinceID();
            int cityCode = selectedCity.getCityID();
            Log.d("TAG", String.valueOf(cityCode));
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "country");
            Log.d("queryCountry",address);

        }
    }
    /**
     * 在queryFromServer()方法中调用了HTTPsendOkHttpRequest()方法向服务器请求数据
     * 相应的数据会回调到onResponse()方法中
     */
    private void queryFromServer(String address, final String type) {
        showProgressDialog();
        HttpUtil.sendOKHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseCity.this, "加载失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            //在onResponse()方法中 调用Utility.handleProvinceResponse()进行数据解析和处理

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = ResponseUtil.provinceResponce(responseText);
                } else if ("city".equals(type)) {
                    result = ResponseUtil.cityResponse(responseText,selectedProvince.getId());
                } else if ("country".equals(type)) {
                    result = ResponseUtil.countryResponse(responseText,selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(() -> {
                        closeProgressDialog();
                        if ("province".equals(type)) {
                            queryProvince();
                        } else if ("city".equals(type)) {
                            queryCity();
                        } else if ("country".equals(type)) {
                            queryCountry();
                        }
                    });
                }
            }
        });
    }
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ChooseCity.this);
            progressDialog.setMessage("正在加载.....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel ==LEVEL_PROVINCE){
            finish();
        }
        else if (currentLevel == LEVEL_COUNTRY) {
            queryCity();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvince();
        }
    }
}
