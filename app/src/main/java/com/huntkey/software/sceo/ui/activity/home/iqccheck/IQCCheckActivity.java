package com.huntkey.software.sceo.ui.activity.home.iqccheck;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
import com.huntkey.software.sceo.widget.CustomRotateAnim;
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

public class IQCCheckActivity extends KnifeBaseActivity {

    @BindView(R.id.iqc_toolbar)
    Toolbar toolbar;
    @BindView(R.id.iqc_edittext)
    ControlEditText iqcBarEt;
    @BindView(R.id.iqc_imageview)
    ImageView resultIv;
    @BindView(R.id.iqc_checkresult)
    TextView resultTextView;

    private CustomRotateAnim rotateAnim;
    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_iqccheck;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "条码合格性检验");

        iqcBarEt.requestFocus();
        iqcBarEt.setOnKeyListener(new MyOnKeyListener());
        iqcBarEt.addTextChangedListener(new MyTextWatcher());
        loadingDialog = new LoadingUncancelable(this);

        rotateAnim = CustomRotateAnim.getCustomRotateAnim();
        // 一次动画执行1秒
        rotateAnim.setDuration(1000);
        // 设置为循环播放
        rotateAnim.setRepeatCount(-1);
        // 设置为匀速
        rotateAnim.setInterpolator(new LinearInterpolator());
    }

    private void doNetwork(String sn){
        if (sn.length() == 0){
            Toasty.warning(IQCCheckActivity.this, "待检条码不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if(sn.length() < 15){
            Toasty.warning(IQCCheckActivity.this, "请输入正确的条码", Toast.LENGTH_SHORT, true).show();
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP02AA&pcmd=CheckLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("sn", sn)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){//校验失败
                            resultIv.setImageResource(R.drawable.ic_iqc_no);
                            resultTextView.setText(data.getMessage());

                            resultIv.setVisibility(View.VISIBLE);
                            resultIv.startAnimation(rotateAnim);
                            resultTextView.setVisibility(View.VISIBLE);
                        }else if (data.getStatus() == 1){//校验成功
                            resultIv.setImageResource(R.drawable.ic_iqc_ok);
                            resultTextView.setText(data.getMessage());

                            resultIv.setVisibility(View.VISIBLE);
                            resultIv.startAnimation(rotateAnim);
                            resultTextView.setVisibility(View.VISIBLE);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(IQCCheckActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(IQCCheckActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(IQCCheckActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        loadingDialog.show();
                        resultIv.setVisibility(View.INVISIBLE);
                        resultIv.clearAnimation();
                        resultTextView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        loadingDialog.dismiss();
                    }
                });
    }

    @OnClick(R.id.iqc_check_btn)
    void check(){
        doNetwork(iqcBarEt.getText().toString().replaceAll("\\s*", ""));
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                doNetwork(iqcBarEt.getText().toString().replaceAll("\\s*", ""));
            }
            return false;
        }
    }

    private class MyTextWatcher implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (iqcBarEt.getText().toString().length() == 0){
                resultIv.setVisibility(View.INVISIBLE);
                resultIv.clearAnimation();
                resultTextView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
