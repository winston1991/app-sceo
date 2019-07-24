package com.huntkey.software.sceo.ui.activity.home.barconversion;

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
import com.huntkey.software.sceo.entity.ExternalBarConversionAData;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * 外部条码转换
 * Created by chenl on 2017/3/28.
 */

public class ExternalBarConversionAActivity extends KnifeBaseActivity {

    @BindView(R.id.ebc_a_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ebc_a_et_nbr)
    ClearEditText nbrEt;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_externalbarconversion_a;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "外部条码转换");

        loadingDialog = new LoadingUncancelable(this);
        nbrEt.requestFocus();
        nbrEt.setOnKeyListener(new MyOnKeyListener());
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                initNetwork();
            }
            return false;
        }
    }

    private void initNetwork() {
        if (nbrEt.getText().toString().length() == 0){
            Toasty.warning(ExternalBarConversionAActivity.this, "编号/单号不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(ExternalBarConversionAActivity.this))
                .params("nbr", nbrEt.getText().toString().replaceAll("\\s*", ""), false)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ExternalBarConversionAData data = SceoUtil.parseJson(s, ExternalBarConversionAData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(ExternalBarConversionAActivity.this, ExternalBarConversionBActivity.class);
                            intent.putExtra("nbr", data);
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(ExternalBarConversionAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(ExternalBarConversionAActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(ExternalBarConversionAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.ebc_a_btn)
    void nextStep(){
        initNetwork();
    }

}
