package com.huntkey.software.sceo.ui.activity.home.outstockgoods;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.InputPickingNumData;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/5/5.
 */

public class InputPickingNumActivity extends KnifeBaseActivity {

    @BindView(R.id.ipn_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ipn_layout)
    LinearLayout linearLayout;
    List<ClearEditText> cetList = new ArrayList<>();

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_inputpickingnum;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "出库点货");
        loadingDialog = new LoadingUncancelable(this);

        createCet();
    }

    private void createCet(){
        ClearEditText et = new ClearEditText(this);
        et.setPadding(15, 15, 15, 15);
        et.setTextSize(16);
        et.requestFocus();
        et.setTextColor(Color.BLACK);
        et.setHint("扫描/输入单号");
        et.setBackground(getResources().getDrawable(R.drawable.border_empty));
        cetList.add(et);
        et.setOnKeyListener(new MyOnKeyListener());
        linearLayout.addView(et);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(et.getLayoutParams());
        layoutParams.setMargins(0, 5, 0, 5);
        et.setLayoutParams(layoutParams);
    }
    private class MyOnKeyListener implements View.OnKeyListener{

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                createCet();
            }
            return false;
        }
    }

    private void doNetwork(final String str){
        if (TextUtils.isEmpty(str.trim())){
            Toasty.warning(InputPickingNumActivity.this, "单号不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP12AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", str)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        InputPickingNumData data = SceoUtil.parseJson(s, InputPickingNumData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(InputPickingNumActivity.this, ChooseStorekeeperActivity.class);
                            intent.putExtra("data", (Serializable) data.getData());
                            intent.putExtra("nbrs", str);
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(InputPickingNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(InputPickingNumActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(InputPickingNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(InputPickingNumActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.ipn_nextstep)
    void nextStep(){
        StringBuffer nbrBuf = new StringBuffer();
        for (ClearEditText et : cetList){
            if (!TextUtils.isEmpty(et.getText().toString().trim())){
                nbrBuf.append(et.getText().toString().trim())
                        .append(",");
            }
        }
        doNetwork(nbrBuf.toString());
    }
}
