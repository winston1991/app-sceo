package com.huntkey.software.sceo.ui.activity.home.barrepair;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.BarRepairItem;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.activity.home.personalprint.PersonalPrintActivity;
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
 * Created by chenl on 2017/4/14.
 */

public class BarRepairBActivity extends KnifeBaseActivity {

    @BindView(R.id.barrepair_b_toolbar)
    Toolbar toolbar;
    @BindView(R.id.barrepair_b_sn)
    TextView snTv;
    @BindView(R.id.barrepair_b_oldnum)
    TextView oldNumTv;
    @BindView(R.id.barrepair_b_newnum)
    ClearEditText newNumEt;
    @BindView(R.id.barrepair_b_btn)
    Button addBtn;

    private LoadingUncancelable loadingDialog;
    private BarRepairItem item;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_barrepair_b;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "条码复制修复");

        BarRepairItem item = (BarRepairItem) getIntent().getSerializableExtra("item");
        snTv.setText(item.getBcls_lot());
        oldNumTv.setText(item.getBcls_qty());
        if ("0".equals(item.getIsmod())){
            addBtn.setEnabled(false);
            newNumEt.setEnabled(false);
        }

        loadingDialog = new LoadingUncancelable(this);
        newNumEt.setOnKeyListener(new MyOnKeyListener());
        newNumEt.addTextChangedListener(textWatcher);
    }

    private void doNetwork(){
        try {
            if (Float.parseFloat(newNumEt.getText().toString().trim()) <= 0){
                Toasty.error(BarRepairBActivity.this, "新数量应大于0", Toast.LENGTH_SHORT).show();
                return;
            }
        }catch (Exception e){
            Toasty.error(BarRepairBActivity.this, "请输入正确的数量", Toast.LENGTH_SHORT).show();
            return;
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP03AA&pcmd=Save")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("sn", snTv.getText().toString().trim())
                .params("qty", newNumEt.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(BarRepairBActivity.this, PersonalPrintActivity.class);
                            intent.putExtra("src", "EWP03AA");
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(BarRepairBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(BarRepairBActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(BarRepairBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        loadingDialog.show();
                        addBtn.setEnabled(false);
                        addBtn.setBackgroundColor(getResources().getColor(R.color.send_btn_click));
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
                if (newNumEt.getText().toString().trim().length() > 0){
                    doNetwork();
                }else {
                    Toasty.warning(BarRepairBActivity.this, "新数量不能为空", Toast.LENGTH_SHORT, true).show();
                }
            }
            return false;
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            addBtn.setEnabled(true);
            addBtn.setBackgroundResource(R.drawable.login_btn);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @OnClick(R.id.barrepair_b_btn)
    void add2PrintLine(){
        if (newNumEt.getText().toString().trim().length() > 0){
            doNetwork();
        }else {
            Toasty.warning(BarRepairBActivity.this, "新数量不能为空", Toast.LENGTH_SHORT, true).show();
        }
    }

    @OnClick(R.id.barrepair_b_close)
    void close(){
        AppManager.getInstance().finishSingleActivityByClass(BarRepairAActivity.class);
        AppManager.getInstance().finishSingleActivityByClass(BarRepairBActivity.class);
    }
}
