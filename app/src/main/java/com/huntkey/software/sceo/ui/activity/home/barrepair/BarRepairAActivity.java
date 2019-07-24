package com.huntkey.software.sceo.ui.activity.home.barrepair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.BarRepairData;
import com.huntkey.software.sceo.entity.BarRepairItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/3/29.
 */

public class BarRepairAActivity extends KnifeBaseActivity {

    @BindView(R.id.barrepair_a_toolbar)
    Toolbar toolbar;
    @BindView(R.id.barrepair_a_sn)
    ControlEditText snEt;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_barrepair_a;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "条码复制修复");

        snEt.requestFocus();
        snEt.setOnKeyListener(new MyOnKeyListener());
        loadingDialog = new LoadingUncancelable(this);
    }

    private void doNetwork(String sn){
        if (snEt.getText().toString().length() == 0){
            Toasty.warning(BarRepairAActivity.this, "条码不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP03AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("sn", sn)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BarRepairData data = SceoUtil.parseJson(s, BarRepairData.class);
                        if (data.getStatus() == 1){
                            if (data.getData().size() > 0){
                                BarRepairItem item = data.getData().get(0);
                                Intent intent = new Intent(BarRepairAActivity.this, BarRepairBActivity.class);
                                intent.putExtra("item", item);
                                startActivity(intent);
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(BarRepairAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(BarRepairAActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(BarRepairAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                doNetwork(snEt.getText().toString().replaceAll("\\s*", ""));
            }
            return false;
        }
    }

    @OnClick(R.id.barrepair_a_btn)
    void nextStep(){
        doNetwork(snEt.getText().toString().replaceAll("\\s*", ""));
    }

}
