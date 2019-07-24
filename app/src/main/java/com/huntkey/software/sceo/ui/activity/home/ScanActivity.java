package com.huntkey.software.sceo.ui.activity.home;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import es.dmoral.toasty.Toasty;

/**
 * Created by chenl on 2017/9/22.
 */

public class ScanActivity extends KnifeBaseActivity implements QRCodeView.Delegate {

    @BindView(R.id.scan_toolbar)
    Toolbar toolbar;
    @BindView(R.id.scan_zxingview)
    QRCodeView qrCodeView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_scan;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "扫描");
        qrCodeView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermission(new CheckPermListener() {
                            @Override
                            public void superPermission() {
                                qrCodeView.startCamera();
                                qrCodeView.showScanRect();
                                qrCodeView.startSpot();
                            }
                        }, R.string.perssion_CAMERA,
                Manifest.permission.CAMERA);
    }

    @Override
    protected void onStop() {
        qrCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        qrCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        vibrate();
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Toasty.warning(ScanActivity.this, "打开相机出错", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.scan_open_light)
    void openLight(){
        qrCodeView.openFlashlight();
    }

    @OnClick(R.id.scan_close_light)
    void closeLight(){
        qrCodeView.closeFlashlight();
    }
}
