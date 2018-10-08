package com.nano.nano_weather.Ui;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.nano.nano_weather.R;

import java.util.List;


public class MapActivity extends Activity {

    private static final String TAG = "MainActivity";   //日志的TAG
    private TextView locationText = null;   //显示位置信息的TextView
    private MapView mapView = null;         //显示地图的控件
    private BaiduMap baiduMap = null;       //地图管理器
    private Marker marker = null;          //覆盖物
    private BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.location);
    private LocationClient locationClient = null;
    boolean isFirstLoc = true;


    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d(TAG, "BDLocationListener -> onReceiveLocation");
            String addr;
            if (bdLocation == null || mapView == null) {
                Log.d(TAG, "BDLocation or mapView is null");
                locationText.setText("定位失败...");
                return;
            }
            if (!bdLocation.getLocationDescribe().isEmpty()) {
                addr = bdLocation.getLocationDescribe();
            } else if (bdLocation.hasAddr()) {
                addr = bdLocation.getAddrStr();
            } else {
                Log.d(TAG, "BDLocation has no addr info");
                addr = "定位失败...";
                return;
            }
            MyLocationData myLocationData = new MyLocationData.Builder()
                    .direction(100)
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();   //构建生生定位数据对象

            baiduMap.setMyLocationData(myLocationData);

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                resetOverlay(latLng);
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 18.0f);
                baiduMap.animateMapStatus(mapStatusUpdate);

            }
            locationText.setText(addr);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_map);
        mapView = findViewById(R.id.mapview);
        locationText = findViewById(R.id.title);
        locationText.setText("正在定位...");
        baiduMap = mapView.getMap();

        baiduMap.setMyLocationEnabled(true);
        baiduMap.setOnMarkerDragListener(new MyMarkerDragListener());
        baiduMap.setOnMarkerClickListener(new MyMarkerClickListener());
        locationClient = new LocationClient(getApplicationContext());
        //注册定位监听函数，当开始定位.start()或者请求定位.requestLocation()时会触发
        locationClient.registerLocationListener(myListener);
        setLocationOption();
        //开始、请求定位
        locationClient.start();
        requestLocation();

    }


    //开始定位或者请求定位
    private void requestLocation() {
        if (locationClient != null && locationClient.isStarted()) {
            Log.d(TAG, "requestLocation.");
            locationText.setText("正在定位...");
            //请求定位，异步返回，结果在locationListener中获取.
            locationClient.requestLocation();
        } else if (locationClient != null && !locationClient.isStarted()) {
            Log.d(TAG, "locationClient is started : " + locationClient.isStarted());
            locationText.setText("正在定位...");
            //定位没有开启 则开启定位，结果在locationListener中获取.
            locationClient.start();
        } else {
            Log.e(TAG, "request location error!!!");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        locationClient.unRegisterLocationListener(myListener);
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        clearOverlay();
        bd.recycle();
        mapView.onDestroy();
        mapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    //设定定位服务客户端locationClient的定位方式
    private void setLocationOption() {
        //获取配置参数对象，用于配置定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        option.setIsNeedLocationPoiList(true);
        option.setNeedDeviceDirect(true);
        option.setIgnoreKillProcess(false);
        option.SetIgnoreCacheException(false);
        option.setEnableSimulateGps(false);

        locationClient.setLocOption(option);
    }

    //初始化添加覆盖物mark
    private void initOverlay(LatLng latLng) {
        Log.d(TAG, "Start initOverlay");

        //设置覆盖物添加的方式与效果
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(bd)
                .draggable(true)//mark可拖拽
                .animateType(MarkerOptions.MarkerAnimateType.jump);
        //添加mark
        marker = (Marker) (baiduMap.addOverlay(markerOptions));//地图上添加mark

        //弹出View(气泡，意即在地图中显示一个信息窗口)，显示当前mark位置信息
        setPopupTipsInfo(marker);

        Log.d(TAG, "End initOverlay");
    }

    //清除覆盖物
    private void clearOverlay() {
        baiduMap.clear();
        marker = null;
    }

    //重置覆盖物
    private void resetOverlay(LatLng latLng) {
        clearOverlay();
        initOverlay(latLng);
    }

    //覆盖物拖拽监听器
    public class MyMarkerDragListener implements BaiduMap.OnMarkerDragListener {

        @Override
        public void onMarkerDrag(Marker marker) {

        }

        //拖拽结束，调用方法，弹出View(气泡，意即在地图中显示一个信息窗口)，显示当前mark位置信息
        @Override
        public void onMarkerDragEnd(Marker marker) {
            setPopupTipsInfo(marker);
        }

        @Override
        public void onMarkerDragStart(Marker marker) {

        }
    }

    //覆盖物点击监听器
    public class MyMarkerClickListener implements BaiduMap.OnMarkerClickListener {

        @Override
        public boolean onMarkerClick(Marker marker) {
            //调用方法,弹出View(气泡，意即在地图中显示一个信息窗口)，显示当前mark位置信息
            setPopupTipsInfo(marker);
            return false;
        }
    }

    private void setPopupTipsInfo(Marker marker) {
        LatLng latLng = marker.getPosition();
        String[] addr = new String[1];
        //实例化一个地理编码查询对象
        GeoCoder geoCoder = GeoCoder.newInstance();
        //设置反地理编码位置坐标
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(latLng);
        //发起反地理编码请求

        //为地理编码查询对象设置一个请求结果监听器
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                Log.d(TAG, "地理编码信息 ---> \nAddress : " + geoCodeResult.getAddress()
                        + "\ntoString : " + geoCodeResult.toString()
                        + "\ndescribeContents : " + geoCodeResult.describeContents());
            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                //获取地理反编码位置信息
                addr[0] = reverseGeoCodeResult.getAddress();
                //获取地址的详细内容对象，此类表示地址解析结果的层次化地址信息。
                ReverseGeoCodeResult.AddressComponent addressDetail = reverseGeoCodeResult.getAddressDetail();

                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.infowindow, null);
                TextView textView = view.findViewById(R.id.info_text);
                Button button = view.findViewById(R.id.info_button);
                textView.setText(addr[0]);


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        String address = addr[0];
                        String extra = address.substring(address.indexOf("省") + 1, address.indexOf("市") + 1);
                        intent.putExtra("weather_id", extra);
                        intent.setClass(MapActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        baiduMap.hideInfoWindow();
                    }
                });
                InfoWindow infoWindow = new InfoWindow(view, latLng, -47);
                baiduMap.showInfoWindow(infoWindow);
            }
        });
        geoCoder.reverseGeoCode(option);
    }
}