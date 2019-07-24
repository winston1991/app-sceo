package com.huntkey.software.sceo.ui.activity.home.barsplit;

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
import com.huntkey.software.sceo.entity.BarSplitAData;
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
 * Created by chenl on 2017/5/15.
 */

public class BarSplitAActivity extends KnifeBaseActivity {

    @BindView(R.id.bsa_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bsa_cet)
    ControlEditText bstCet;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_barsplit_a;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "条码拆分");

        bstCet.requestFocus();
        bstCet.setOnKeyListener(new MyOnKeyListener());

        loadingDialog = new LoadingUncancelable(this);
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                doNetwork(bstCet.getText().toString().replaceAll("\\s*", ""));
            }
            return false;
        }
    }

    @OnClick(R.id.bsa_nextstep)
    void nextStep(){
        doNetwork(bstCet.getText().toString().replaceAll("\\s*", ""));
    }

    private void doNetwork(final String lot){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP13AA&pcmd=CheckLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("lot", lot)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BarSplitAData data = SceoUtil.parseJson(s, BarSplitAData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(BarSplitAActivity.this, BarSplitBActivity.class);
                            intent.putExtra("pid", data.getData().get(0).getTskp_id());
                            intent.putExtra("lot", data.getData().get(0).getLot_sn());
                            intent.putExtra("oldNum", data.getData().get(0).getLd_qty());
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(BarSplitAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(BarSplitAActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(BarSplitAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
                        Toasty.error(BarSplitAActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }
}
