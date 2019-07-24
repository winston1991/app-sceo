package com.huntkey.software.sceo.ui.activity.attence;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.MapDevGroupData;
import com.huntkey.software.sceo.entity.MapDevGroupItem;
import com.huntkey.software.sceo.entity.MapDevGroupLv0;
import com.huntkey.software.sceo.entity.MapDevGroupLv1;
import com.huntkey.software.sceo.entity.MapDevGroupLv2;
import com.huntkey.software.sceo.entity.MapDevGroupLv3;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.adapter.MapDevGroupAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/9/8.
 */

public class BaiduMapActivity extends KnifeBaseActivity implements SensorEventListener {

    @BindView(R.id.bmap_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bmapView)
    MapView mapView;
    @BindView(R.id.bmapBtn)
    Button requestLocButton;
    @BindView(R.id.bmap_lola)
    TextView lolaTv;
    @BindView(R.id.bmap_submit)
    TextView submitBtn;

    private BaiduMap baiduMap;
    private LocationClient locationClient;
    private MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode currentMode;
    private SensorManager sensorManager;
    private Double lastX = 0.0;
    private int currentDirection = 0;
    private double currentLat = 0.0;
    private double currentLon = 0.0;
    private float currentAccracy;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private BitmapDescriptor currentMarker;
    private BitmapDescriptor pointMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_mark);
    private LoadingDialog loadingDialog;
    private String devGroupName;
    private String devGroupCode;
    private String iseff = "1";

    private TextView devGroTv;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_baidu_map;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "考勤设置");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        currentMode = MyLocationConfiguration.LocationMode.NORMAL;
        requestLocButton.setText("普通");
        requestLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentMode) {
                    case NORMAL:
                        requestLocButton.setText("跟随");
                        currentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                currentMode, true, currentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        requestLocButton.setText("普通");
                        currentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                currentMode, true, currentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        requestLocButton.setText("罗盘");
                        currentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                currentMode, true, currentMarker));
                        break;
                    default:
                        break;
                }
            }
        });

        // 地图初始化
        baiduMap = mapView.getMap();
        // 开启定位图层
        baiduMap.setMyLocationEnabled(true);
        // 定位初始化
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        locationClient.setLocOption(option);
        locationClient.start();

        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                String state = "";
                if (latLng == null){
                    state = "点击地图以获取经纬度";
                }else {
                    currentLon = latLng.longitude;
                    currentLat = latLng.latitude;
                    state = String.format("当前经度:%f  当前纬度:%f", currentLon, currentLat);
                    MarkerOptions ooA = new MarkerOptions().position(latLng).icon(pointMarker);
                    baiduMap.clear();
                    baiduMap.addOverlay(ooA);

                    OkGo.get("http://api.map.baidu.com/geocoder/v2/?callback=renderReverse")
                            .tag(BaiduMapActivity.this)
                            .params("location", currentLat + "," + currentLon)
                            .params("output", "json")
                            .params("pois", "1")
                            .params("ak", "ROX2faM3u6RFqcXq8yh2euOSb5E9uK6t")
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(String s, Call call, Response response) {

                                }
                            });
                }
                lolaTv.setText(state);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View dialogView = inflater.inflate(R.layout.dialog_map_radius, null);
                final EditText radiusEt = (EditText) dialogView.findViewById(R.id.mapRadiusEt);
                TextView radiusCancle = (TextView) dialogView.findViewById(R.id.mapRadiusCancle);
                TextView radiusConfirm = (TextView) dialogView.findViewById(R.id.mapRadiusConfirm);
                LinearLayout devGroupLayout = (LinearLayout) dialogView.findViewById(R.id.mapRadiusDevGroup);
                TextView mapRadiusLat = (TextView) dialogView.findViewById(R.id.mapRadiusLat);
                TextView mapRadiusLon = (TextView) dialogView.findViewById(R.id.mapRadiusLon);
                final EditText locationEt = (EditText) dialogView.findViewById(R.id.mapRadiusLocation);
                final EditText nameEt = (EditText) dialogView.findViewById(R.id.mapRadiusName);
                RadioGroup radioGroup = (RadioGroup) dialogView.findViewById(R.id.mapRadiusRg);
                devGroTv = (TextView) dialogView.findViewById(R.id.mapRadiusDevGro);
                AlertDialog.Builder builder = new AlertDialog.Builder(BaiduMapActivity.this);
                builder.setView(dialogView);
                final AlertDialog dialog = builder.show();

                mapRadiusLat.setText(currentLat+"");
                mapRadiusLon.setText(currentLon+"");
                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                        if (checkedId == R.id.mapRadiusRb1){
                            iseff = "1";
                        }else {
                            iseff = "0";
                        }
                    }
                });

                radiusCancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                radiusConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(locationEt.getText().toString().trim())){
                            Toasty.warning(BaiduMapActivity.this, "请输入地点", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(nameEt.getText().toString().trim())){
                            Toasty.warning(BaiduMapActivity.this, "请输入名称", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (TextUtils.isEmpty(devGroupCode)){
                            Toasty.warning(BaiduMapActivity.this, "请选择卡机组别", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        dialog.dismiss();
                        submitLoLa(radiusEt.getText().toString().trim(), locationEt.getText().toString().trim(),
                                nameEt.getText().toString().trim());
                    }
                });

                devGroupLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDeviceGroup();
                    }
                });
            }
        });

        loadingDialog = new LoadingDialog(this);
    }

    private void submitLoLa(String radius, String dglocation, String dgname) {
        if (TextUtils.isEmpty(radius)){
            radius = "100";
        }
        if (currentLat <= 0 || currentLon <= 0){
            Toasty.warning(BaiduMapActivity.this, "定位出错", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestParams params = new RequestParams();
        params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(this));
        params.addBodyParameter("lontitude", String.valueOf(currentLon));
        params.addBodyParameter("latitude", String.valueOf(currentLat));
        params.addBodyParameter("circleRadius", radius);
        params.addBodyParameter("adgroup", devGroupCode);
        params.addBodyParameter("site", dglocation);
        params.addBodyParameter("adname", dgname);
        params.addBodyParameter("iseff", iseff);
        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.POST,
                Conf.SERVICE_URL + "CWASysV4/csp/CWASysV4.dll?page=EWA99AA&pcmd=APPSaveAddr&charset=utf8",
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        loadingDialog.dismiss();
                        BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(BaiduMapActivity.this, "提交成功", Toast.LENGTH_SHORT).show();
                        }else {
                            Toasty.error(BaiduMapActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingDialog.show();
                    }
                });
    }

    private void getDeviceGroup(){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View devGroView = inflater.inflate(R.layout.dialog_map_devgroup, null);
        RecyclerView recyclerView = (RecyclerView) devGroView.findViewById(R.id.mapDevRecyclerView);
        AlertDialog.Builder builder = new AlertDialog.Builder(BaiduMapActivity.this);
        builder.setView(devGroView);
        final AlertDialog dialog = builder.show();

        recyclerView.setLayoutManager(new LinearLayoutManager(BaiduMapActivity.this));

        final ArrayList<MultiItemEntity> list = new ArrayList<>();
        final MapDevGroupAdapter adapter = new MapDevGroupAdapter(list);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                dialog.dismiss();
                if (adapter.getItemViewType(position) == 0){
                    devGroupName = ((MapDevGroupLv0)adapter.getItem(position)).getAdeg_name();
                    devGroupCode = ((MapDevGroupLv0)adapter.getItem(position)).getAdeg_code();
                    devGroTv.setText(devGroupName);
                }else if (adapter.getItemViewType(position) == 1){
                    devGroupName = ((MapDevGroupLv1)adapter.getItem(position)).getAdeg_name();
                    devGroupCode = ((MapDevGroupLv1)adapter.getItem(position)).getAdeg_code();
                    devGroTv.setText(devGroupName);
                }else if (adapter.getItemViewType(position) == 2){
                    devGroupName = ((MapDevGroupLv2)adapter.getItem(position)).getAdeg_name();
                    devGroupCode = ((MapDevGroupLv2)adapter.getItem(position)).getAdeg_code();
                    devGroTv.setText(devGroupName);
                }else if (adapter.getItemViewType(position) == 3){
                    devGroupName = ((MapDevGroupLv3)adapter.getItem(position)).getAdeg_name();
                    devGroupCode = ((MapDevGroupLv3)adapter.getItem(position)).getAdeg_code();
                    devGroTv.setText(devGroupName);
                }
                return false;
            }
        });

        OkGo.post(Conf.SERVICE_URL + "CWASysV4/csp/CWASysV4.dll?page=EWA99AA&pcmd=APPQueryGroup")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        MapDevGroupData mapDevGroup = SceoUtil.parseJson(s, MapDevGroupData.class);
                        if (mapDevGroup.getStatus() == 0 && mapDevGroup.getData().size() > 0){
                            List<MapDevGroupItem> dgList = mapDevGroup.getData();
                            List<MapDevGroupLv0> lv0List = new ArrayList<>();
                            List<MapDevGroupLv1> lv1List = new ArrayList<>();
                            List<MapDevGroupLv2> lv2List = new ArrayList<>();
                            List<MapDevGroupLv3> lv3List = new ArrayList<>();
                            for (MapDevGroupItem item : dgList){
                                if (TextUtils.isEmpty(item.getAdeg_level())){
                                    MapDevGroupLv0 lv0 = new MapDevGroupLv0(item.getAdeg_code(), item.getAdeg_name(), item.getAdeg_level());
                                    lv0List.add(lv0);
                                }
                                if (item.getAdeg_level().length() == 3){
                                    MapDevGroupLv1 lv1 = new MapDevGroupLv1(item.getAdeg_code(), item.getAdeg_name(), item.getAdeg_level());
                                    lv1List.add(lv1);
                                }
                                if (item.getAdeg_level().length() == 6){
                                    MapDevGroupLv2 lv2 = new MapDevGroupLv2(item.getAdeg_code(), item.getAdeg_name(), item.getAdeg_level());
                                    lv2List.add(lv2);
                                }
                                if (item.getAdeg_level().length() == 9){
                                    MapDevGroupLv3 lv3 = new MapDevGroupLv3(item.getAdeg_code(), item.getAdeg_name(), item.getAdeg_level());
                                    lv3List.add(lv3);
                                }
                            }

                            for (MapDevGroupLv3 lv3 : lv3List){
                                for (MapDevGroupLv2 lv2 : lv2List){
                                    if (lv3.getAdeg_level().substring(0, 6).equals(lv2.getAdeg_level())){
                                        lv2.addSubItem(lv3);
                                    }
                                }
                            }
                            for (MapDevGroupLv2 lv2 : lv2List){
                                for (MapDevGroupLv1 lv1 : lv1List){
                                    if (lv2.getAdeg_level().substring(0, 3).equals(lv1.getAdeg_level())){
                                        lv1.addSubItem(lv2);
                                    }
                                }
                            }
                            for (MapDevGroupLv1 lv1 : lv1List){
                                lv0List.get(0).addSubItem(lv1);
                            }

                            list.add(lv0List.get(0));
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        loadingDialog.show();
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        loadingDialog.dismiss();
                    }
                });
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            currentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(currentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(currentDirection).latitude(currentLat)
                    .longitude(currentLon).build();
            baiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private class MyLocationListenner implements BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mapView == null) {
                return;
            }
            currentLat = location.getLatitude();
            currentLon = location.getLongitude();
            currentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(currentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            lolaTv.setText(String.format("当前经度： %f 当前纬度：%f", currentLon, currentLat));
        }
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStart() {
        sensorManager.unregisterListener(this);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        locationClient.stop();
        // 关闭定位图层
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }
}
