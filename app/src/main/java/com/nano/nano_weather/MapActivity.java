package com.nano.nano_weather;

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

import java.util.List;


public class MapActivity extends Activity {

    private static final String TAG = "MainActivity";   //日志的TAG
    private TextView locationText = null;   //显示位置信息的TextView
    private MapView mapView = null;         //显示地图的控件
    private BaiduMap baiduMap = null;       //地图管理器
    private Marker marker = null;          //覆盖物
    //初始化bitmap信息，不用的时候请及时回收recycle   //覆盖物图标
    private BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(R.drawable.location);
    //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
    private LocationClient locationClient = null;
    //是否是首次定位,实际上没有用到，因为我设置了不定时请求位置信息
    boolean isFirstLoc = true;

    //定位监听器（实现定位请求回调接口）,当客户端请求定位或者开始定位的时候会触发
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Log.d(TAG, "BDLocationListener -> onReceiveLocation");
            String addr; //定位结果
            //mapView销毁后不在处理新接收的位置
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
            //以下打印日志，打印一些详细信息，供参考
            //------------------------------------位置信息日志--------------------------------------------------
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("\nTime : " + bdLocation.getTime());            //服务器返回的当前定位时间
            sBuilder.append("\nError code : " + bdLocation.getLocType());   //定位结果码
            sBuilder.append("\nLatitude : " + bdLocation.getLatitude());    //获取纬度坐标
            sBuilder.append("\nLongtitude : " + bdLocation.getLongitude()); //获取经度坐标
            sBuilder.append("\nRadius : " + bdLocation.getRadius());        //位置圆心

            //根据定位结果码判断是何种定位以及定位请求是否成功
            if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                //GPS定位结果
                sBuilder.append("\nSpeed : " + bdLocation.getSpeed());//当前运动的速度
                sBuilder.append("\nSatellite number : " + bdLocation.getSatelliteNumber());//定位卫星数量
                sBuilder.append("\nHeight : " + bdLocation.getAltitude());  //位置高度
                sBuilder.append("\nDirection : " + bdLocation.getDirection());  //定位方向
                sBuilder.append("\nAddrStr : " + bdLocation.getAddrStr());  //位置详细信息
                sBuilder.append("\nStreet : " + bdLocation.getStreetNumber() + " " + bdLocation.getStreet());//街道号、路名
                sBuilder.append("\nDescribtion : GPS 定位成功");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                //网络定位结果
                sBuilder.append("\nAddrStr : " + bdLocation.getAddrStr()); //位置详细信息
                sBuilder.append("\nStreet : " + bdLocation.getStreetNumber() + " " + bdLocation.getStreet());//街道号、路名
                sBuilder.append("\nOperators : " + bdLocation.getOperators());//运营商编号
                sBuilder.append("\nDescribtion : 网络定位成功");
            } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {
                //离线定位结果
                sBuilder.append("\nAddrStr : " + bdLocation.getAddrStr()); //位置详细信息
                sBuilder.append("\nStreet : " + bdLocation.getStreetNumber() + " " + bdLocation.getStreet());//街道号、路名
                sBuilder.append("\nDescribtion : 离线定位成功");
            } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                sBuilder.append("\nDescribtion : 服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                sBuilder.append("\nDescribtion : 网络故障，请检查网络连接是否正常");
            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                sBuilder.append("\nDescribtion : 无法定位结果，一般由于定位SDK内部检测到没有有效的定位依据，" +
                        "比如在飞行模式下就会返回该定位类型， 一般关闭飞行模式或者打开wifi就可以再次定位成功");
            }

            //位置语义化描述
            sBuilder.append("\nLocation decribe : " + bdLocation.getLocationDescribe());
            //poi信息（就是附近的一些建筑信息）,只有设置可获取poi才有值
            List<Poi> poiList = bdLocation.getPoiList();
            if (poiList != null) {
                sBuilder.append("\nPoilist size : " + poiList.size());
                for (Poi p : poiList) {
                    sBuilder.append("\nPoi : " + p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            //打印以上信息
            Log.d(TAG, "定位结果详细信息 : " + sBuilder.toString());

            //----------------------------------------------定位----------------------------------------
            //构建生成定位数据对象            //定位数据构建器
            MyLocationData myLocationData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())   //设置定位数据的精度信息，单位米
                    .direction(100)                     //设定定位数据的方向信息？？啥意思？？
                    .latitude(bdLocation.getLatitude()) //设定定位数据的纬度
                    .longitude(bdLocation.getLongitude())//设定定位数据的经度
                    .build();   //构建生生定位数据对象

            //设置定位数据, 只有先允许定位图层后设置数据才会生效,setMyLocationEnabled(boolean)
            baiduMap.setMyLocationData(myLocationData);

            if (isFirstLoc) {
                isFirstLoc = false;
                //地理坐标基本数据结构：经度和纬度
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                //为当前定位到的位置设置覆盖物Marker
                resetOverlay(latLng);
                //描述地图状态将要发生的变化         //生成地图状态将要发生的变化,newLatLngZoom设置地图新中心点
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng, 18.0f);
                //MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(latLng);
                //以动画方式更新地图状态，动画耗时 300 ms  (聚焦到当前位置)
                baiduMap.animateMapStatus(mapStatusUpdate);

            }
            locationText.setText(addr);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        setContentView(R.layout.activity_map);//加载布局文件
        //获取地图控件的引用
        mapView = findViewById(R.id.mapview);
        //定位结果显示
        locationText = findViewById(R.id.title);
        locationText.setText("正在定位...");
        //获取地图控制器
        baiduMap = mapView.getMap();
        UiSettings uiSettings = baiduMap.getUiSettings();
        uiSettings.setAllGesturesEnabled(false);

        //允许定位图层,如果不设置这个参数，那么baiduMap.setMyLocationData(myLocationData);定位不起作用
        baiduMap.setMyLocationEnabled(true);
        //设置mark覆盖物拖拽监听器
        baiduMap.setOnMarkerDragListener(new MyMarkerDragListener());
        //设置mark覆盖物点击监听器
        baiduMap.setOnMarkerClickListener(new MyMarkerClickListener());
        //生成定位服务的客户端对象，此处需要注意：LocationClient类必须在主线程中声明
        locationClient = new LocationClient(getApplicationContext());
        //注册定位监听函数，当开始定位.start()或者请求定位.requestLocation()时会触发
        locationClient.registerLocationListener(myListener);
        //设定定位SDK的定位方式
        setLocationOption();
        //开始、请求定位
        //locationClient.start();
        requestLocation();

    }


    //开始定位或者请求定位
    private void requestLocation() {
        //如果请求定位客户端已经开启，就直接请求定位，否则开始定位
        //很重要的一点，是要在AndroidManifest文件中注册定位服务，否则locationClient.isStarted一直会是false,
        // 而且可能出现一种情况是首次能定位，之后再定位无效
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
        //注销定位监听器
        locationClient.unRegisterLocationListener(myListener);
        //停止定位
        locationClient.stop();
        //不允许图层定位
        baiduMap.setMyLocationEnabled(false);
        //清除覆盖物
        clearOverlay();
        //回收Bitmap资源
        bd.recycle();
        //在activity执行onDestroy时执行MapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
        mapView = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        //在activity执行onResume时执行MapView. onResume()，实现地图生命周期管理
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        //在activity执行onPause时执行MapView. onResume()，实现地图生命周期管理
        mapView.onPause();
    }

    //设定定位服务客户端locationClient的定位方式
    private void setLocationOption() {
        //获取配置参数对象，用于配置定位SDK各配置参数，比如定位模式、定位时间间隔、坐标系类型等
        LocationClientOption option = new LocationClientOption();
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        /*
        * 高精度定位模式：这种定位模式下，会同时使用网络定位和GPS定位，优先返回最高精度的定位结果；
        * 低功耗定位模式：这种定位模式下，不会使用GPS，只会使用网络定位（Wi-Fi和基站定位）；
        * 仅用设备定位模式：这种定位模式下，不需要连接网络，只使用GPS进行定位，这种模式下不支持室内环境的定位。*/
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType("bd09ll");
        //可选，默认0，即仅定位一次，设置定时发起定位请求的间隔需要大于等于1000ms才是有效的
        /*
        * 定位sdk提供2种定位模式，定时定位和app主动请求定位。
        * setScanSpan < 1000 则为 app主动请求定位；
        * setScanSpan >=1000,则为定时定位模式（setScanSpan的值就是定时定位的时间间隔））
        * 定时定位模式中，定位sdk会按照app设定的时间定位进行位置更新，定时回调定位结果。此种定位模式适用于希望获得连续定位结果的情况。
        * 对于单次定位类应用，或者偶尔需要一下位置信息的app，可采用app主动请求定位这种模式。*/
        //option.setScanSpan(2000);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //设置是否需要返回位置POI信息，可以在BDLocation.getPoiList()中得到数据
        option.setIsNeedLocationPoiList(true);
        //在网络定位时，是否需要设备方向 true:需要 ; false:不需要
        option.setNeedDeviceDirect(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setEnableSimulateGps(false);

        //设定定位SDK的定位方式
        locationClient.setLocOption(option);
    }

    //初始化添加覆盖物mark
    private void initOverlay(LatLng latLng) {
        Log.d(TAG, "Start initOverlay");

        //设置覆盖物添加的方式与效果
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)//mark出现的位置
                .icon(bd)       //mark图标
                .draggable(true)//mark可拖拽
                .animateType(MarkerOptions.MarkerAnimateType.jump);//从天而降的方式
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

    //想根据Mark中的经纬度信息，获取当前的位置语义化结果，需要使用地理编码查询和地理反编码请求
    //在地图中显示一个信息窗口
    private void setPopupTipsInfo(Marker marker) {
        //获取当前经纬度信息
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

            //当获取到反编码信息结果的时候会调用
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                //获取地理反编码位置信息
                addr[0] = reverseGeoCodeResult.getAddress();
                //获取地址的详细内容对象，此类表示地址解析结果的层次化地址信息。
                ReverseGeoCodeResult.AddressComponent addressDetail = reverseGeoCodeResult.getAddressDetail();
                Log.d(TAG, "反地理编码信息 ---> \nAddress : " + addr[0]
                        + "\nBusinessCircle : " + reverseGeoCodeResult.getBusinessCircle()//位置所属商圈名称
                        + "\ncity : " + addressDetail.city  //所在城市名称
                        + "\ndistrict : " + addressDetail.district  //区县名称
                        + "\nprovince : " + addressDetail.province  //省份名称
                        + "\nstreet : " + addressDetail.street      //街道名
                        + "\nstreetNumber : " + addressDetail.streetNumber);//街道（门牌）号码

                StringBuilder poiInfoBuilder = new StringBuilder();
                //poiInfo信息
                List<PoiInfo> poiInfoList = reverseGeoCodeResult.getPoiList();
                if (poiInfoList != null) {
                    poiInfoBuilder.append("\nPoilist size : " + poiInfoList.size());
                    for (PoiInfo p : poiInfoList) {
                        poiInfoBuilder.append("\n\taddress: " + p.address);//地址信息
                        poiInfoBuilder.append(" name: " + p.name);//名称
                        //还有其他的一些信息，我这里就不打印了，请参考API
                    }
                }
                Log.d(TAG, "poiInfo --> " + poiInfoBuilder.toString());

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