package com.huntkey.software.sceo.ui.activity.attence;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.utils.EmulatorCheckUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.List;
import java.util.Random;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

/**
 * Created by chenl on 2017/8/25.
 */

public class AttenceWebActivity extends KnifeBaseActivity {

    @BindView(R.id.jsbridge_toolbar)
    Toolbar toolbar;
    @BindView(R.id.jsbridge_srl)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.jsbridge_webview)
    BridgeWebView webView;

    private String urlStr;
    private String dataStr;
    private LocationServer locationServer;
    private String locationDataStr;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_attence;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        if (EmulatorCheckUtil.CheckDeviceIDS(this) || EmulatorCheckUtil.CheckEmulatorBuild(this)
                || EmulatorCheckUtil.CheckImsiIDS(this) || EmulatorCheckUtil.CheckPhoneNumber(this)
                || EmulatorCheckUtil.CheckQEmuDriverFile() || EmulatorCheckUtil.checkBlueStacksFiles()
                || EmulatorCheckUtil.readCpuInfo()) {
            Toasty.warning(AttenceWebActivity.this, "当前功能不能在模拟器上运行", Toast.LENGTH_SHORT).show();
            return;
        }

        setToolBar(toolbar, "移动考勤");
        loadUrl();
        checkPermission(new CheckPermListener() {
                            @Override
                            public void superPermission() {
                                doLocation();
                            }
                        }, R.string.perssion_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void onStop() {
        if (locationServer != null) {
            locationServer.unregisterListener(mListener); //注销掉监听
            locationServer.stop(); //停止定位服务
        }
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void doLocation() {
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用
        locationServer = ((SceoApplication) getApplication()).locationServer;
        locationServer.registerListener(mListener);
        //注册监听
        locationServer.setLocationOption(locationServer.getDefaultLocationClientOption());
        locationServer.start();// 定位SDK

        webView.registerHandler("submitFromWeb", new BridgeHandler() {

            @Override
            public void handler(String data, CallBackFunction function) {
                function.onCallBack(locationDataStr);
            }

        });
    }

    private BDLocationListener mListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                LocationData locationData = new LocationData();
                locationData.latitude = location.getLatitude();
                locationData.lontitude = location.getLongitude();
                locationData.addr = location.getAddrStr() + location.getLocationDescribe();
                locationDataStr = new Gson().toJson(locationData);
            }
        }
    };

    private void loadUrl() {
        urlStr = Conf.SERVICE_URL + "sceosrv/csp/dist/index.html#/";//"http://192.168.12.107/www/accence/dist/index.html#/"
        TransData transData = new TransData();
        transData.sessionkey = SceoUtil.getSessionKey(this);
        transData.empid = SceoUtil.getEmpCode(this);
        dataStr = new Gson().toJson(transData);

        webView.setDefaultHandler(new DefaultHandler());
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.loadUrl(urlStr);

        webView.callHandler("functionInJs", dataStr, new CallBackFunction() {
            @Override
            public void onCallBack(String data) {
//                Toasty.info(AttenceWebActivity.this, data, Toast.LENGTH_SHORT).show();
            }
        });

        webView.setWebViewClient(new BridgeWebViewClient(webView) {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
            }
        });
    }

    static class LocationData {
        double latitude;
        double lontitude;
        String addr;
    }

    static class TransData {
        String sessionkey;
        String empid;
    }

    protected void setToolBar(Toolbar toolBar, String title) {
        toolBar.setTitle(title);
        toolBar.setTitleTextColor(getResources().getColor(R.color.white));
        toolBar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private Location getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<String> providersList = locationManager.getAllProviders();
        for (String item : providersList) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }
            Location location = locationManager.getLastKnownLocation(item);
            if (location != null){
                return location;
            }
        }
        return null;
    }

    /**
     * 判断是否为模拟定位
     * 百度地图屏蔽了模拟定位,弃用该方法
     */
    private boolean isMockLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && getLocation() != null) {
            return getLocation().isFromMockProvider();
        }else {
            return true;
        }
    }
}
