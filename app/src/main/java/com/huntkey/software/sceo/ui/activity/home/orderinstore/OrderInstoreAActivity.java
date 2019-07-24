package com.huntkey.software.sceo.ui.activity.home.orderinstore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.OrderInstoreData;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.activity.home.ScanActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/8/21.
 */

public class OrderInstoreAActivity extends KnifeBaseActivity {

    @BindView(R.id.ois_a_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ois_a_sn)
    ClearEditText oisCet;

    private LoadingUncancelable loadingDialog;
    private int requestCode = 0x11;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_orderinstore_a;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "制令入库");
        loadingDialog = new LoadingUncancelable(this);
        oisCet.requestFocus();
        oisCet.setOnKeyListener(new MyOnKeyListener());
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                nextStep();
            }
            return false;
        }
    }

    @OnClick(R.id.ois_a_btn)
    void nextStep(){
        if (oisCet.getText().toString().length() == 0){
            Toasty.warning(OrderInstoreAActivity.this, "制令单号不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP19AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", oisCet.getText().toString().replaceAll("\\s*", ""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        OrderInstoreData data = SceoUtil.parseJson(s, OrderInstoreData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(OrderInstoreAActivity.this, OrderInstoreBActivity.class);
                            intent.putExtra("data", (Serializable) data.getData());
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OrderInstoreAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OrderInstoreAActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OrderInstoreAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.ois_a_scan)
    void doScan(){
        Intent intent = new Intent(OrderInstoreAActivity.this, ScanActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == requestCode && resultCode == Activity.RESULT_OK){
            oisCet.setText(data.getStringExtra("result"));
        }
    }
}
