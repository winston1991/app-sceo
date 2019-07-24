package com.huntkey.software.sceo.ui.activity.home.cloakoperations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.ScanBoxData;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
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
 * Created by chenl on 2017/5/3.
 */

public class ScanBoxNumActivity extends KnifeBaseActivity {

    @BindView(R.id.sbn_toolbar)
    Toolbar toolbar;
    @BindView(R.id.sbn_cet)
    ControlEditText sbnCet;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_scanboxnum;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "散箱作业");

        sbnCet.requestFocus();
        sbnCet.setOnKeyListener(new MyOnKeyListener());

        loadingDialog = new LoadingUncancelable(this);
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                doNetwork(sbnCet.getText().toString().replaceAll("\\s*", ""));
            }
            return false;
        }
    }

    @OnClick(R.id.sbn_nextstep)
    void nextStep(){
        if (TextUtils.isEmpty(sbnCet.getText().toString().replaceAll("\\s*", ""))){
            Toasty.warning(ScanBoxNumActivity.this, "箱号不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }
        doNetwork(sbnCet.getText().toString().replaceAll("\\s*", ""));
    }

    private void doNetwork(String lot){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP09AA&pcmd=CheckLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("lot", lot)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ScanBoxData data = SceoUtil.parseJson(s, ScanBoxData.class);
                        if (data.getStatus() == 0){
                            if (data.getData().size() > 0){
                                Intent intent = new Intent(ScanBoxNumActivity.this, BoxOperationsActivity.class);
                                intent.putExtra("data", data.getData().get(0));
                                startActivity(intent);
                            }else {
                                Toasty.warning(ScanBoxNumActivity.this, "请扫描正确的箱号", Toast.LENGTH_SHORT, true).show();
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(ScanBoxNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(ScanBoxNumActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(ScanBoxNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(ScanBoxNumActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

}
