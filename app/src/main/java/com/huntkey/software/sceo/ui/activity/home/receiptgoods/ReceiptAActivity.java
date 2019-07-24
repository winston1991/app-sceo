package com.huntkey.software.sceo.ui.activity.home.receiptgoods;

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
import com.huntkey.software.sceo.entity.ReceiptAData;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
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
 * Created by chenl on 2017/7/10.
 */

public class ReceiptAActivity extends KnifeBaseActivity {

    @BindView(R.id.rpt_a_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rpt_a_cet)
    ClearEditText rptCet;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_receipt_a;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "收货点货");

        rptCet.requestFocus();
        rptCet.setOnKeyListener(new MyOnKeyListener());

        loadingDialog = new LoadingUncancelable(this);
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                doNetwork(rptCet.getText().toString().trim());
            }
            return false;
        }
    }

    @OnClick(R.id.rpt_a_nextstep)
    void nextStep(){
        doNetwork(rptCet.getText().toString().trim());
    }

    private void doNetwork(final String vend){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP16AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("vend", vend)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ReceiptAData data = SceoUtil.parseJson(s, ReceiptAData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(ReceiptAActivity.this, ReceiptBActivity.class);
                            intent.putExtra("data", (Serializable) data.getData());
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(ReceiptAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(ReceiptAActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(ReceiptAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(ReceiptAActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }
}
