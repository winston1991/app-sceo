package com.huntkey.software.sceo.ui.activity.home.barsplit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.ui.LoadingDialog;
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
 * Created by chenl on 2017/5/15.
 */

public class BarSplitBActivity extends KnifeBaseActivity {

    @BindView(R.id.bsb_toolbar)
    Toolbar toolbar;
    @BindView(R.id.bsb_oldlot)
    TextView oldLotTv;
    @BindView(R.id.bsb_oldnum)
    TextView oldNumTv;
    @BindView(R.id.bsb_newnum1)
    ClearEditText newNumCet;
    @BindView(R.id.bsb_newnum2)
    TextView newNumTv;
    @BindView(R.id.bsb_submit)
    Button submitBtn;
    @BindView(R.id.bsb_close)
    Button cancelBtn;

    private String oldNum;
    private String lot;
    private String pid;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_barsplit_b;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "条码拆分");

        lot = getIntent().getStringExtra("lot");
        oldNum = getIntent().getStringExtra("oldNum");
        pid = getIntent().getStringExtra("pid");
        oldLotTv.setText(lot);
        oldNumTv.setText(oldNum);

        loadingDialog = new LoadingUncancelable(this);

        newNumCet.addTextChangedListener(new MyTextWatcher());
    }

    private class MyTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!TextUtils.isEmpty(s.toString())){
                if (Float.parseFloat(s.toString()) >= Float.parseFloat(oldNum)){
                    Toasty.warning(BarSplitBActivity.this, "拆分数量应小于原数量", Toast.LENGTH_SHORT, true).show();
                }
                newNumTv.setText(String.valueOf(Float.parseFloat(oldNum) - Float.parseFloat(s.toString())));
            }else {
                newNumTv.setText(String.valueOf(Float.parseFloat(oldNum) - 0));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    @OnClick(R.id.bsb_submit)
    void submit(){
        try {
            if (Float.parseFloat(newNumTv.getText().toString().trim()) < 0){
                Toasty.warning(BarSplitBActivity.this, "请输入正确的数量", Toast.LENGTH_SHORT, true).show();
                return;
            }
        }catch (Exception e){
            Toasty.warning(BarSplitBActivity.this, "请输入正确的数量", Toast.LENGTH_SHORT, true).show();
            return;
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP13AA&pcmd=Submit")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("lot", lot)
                .params("qty", newNumCet.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(BarSplitBActivity.this, BarSplitPrintActivity.class);
                            intent.putExtra("lot", lot);
                            startActivity(intent);
                            submitBtn.setEnabled(false);
                            cancelBtn.setEnabled(false);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(BarSplitBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(BarSplitBActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(BarSplitBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
                        Toasty.error(BarSplitBActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    /**
     * 取消拆分
     */
    @OnClick(R.id.bsb_close)
    void close(){
        doCancel(pid);
    }

    private void doCancel(String pid){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP13AA&pcmd=CancelLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("pid", pid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(BarSplitBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            AppManager.getInstance().finishSingleActivityByClass(BarSplitAActivity.class);
                            Intent intent = new Intent(BarSplitBActivity.this, BarSplitAActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(BarSplitBActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(BarSplitBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(BarSplitBActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(BarSplitBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(BarSplitBActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
