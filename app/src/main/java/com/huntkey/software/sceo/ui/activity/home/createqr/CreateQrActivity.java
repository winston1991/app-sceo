package com.huntkey.software.sceo.ui.activity.home.createqr;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.OutputPrintItem;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.activity.home.bluetoothprint.PrintActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/7/6.
 */

public class CreateQrActivity extends KnifeBaseActivity {

    @BindView(R.id.cqr_toolbar)
    Toolbar toolbar;
    @BindView(R.id.cqr_layout)
    LinearLayout linearLayout;

    List<ClearEditText> cetList = new ArrayList<>();
    List<OutputPrintItem> data = new ArrayList<>();
    private LoadingDialog loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_create_qr;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "生成条码二维码");
        loadingDialog = new LoadingDialog(this);
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

    @OnClick(R.id.cqr_nextstep)
    void createQr(){
        RequestParams params = new RequestParams();
        params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(this));
        for (ClearEditText ct : cetList){
            String lot = ct.getText().toString().replaceAll("\\s*", "");
            if (!TextUtils.isEmpty(lot) && lot.length() > 10 && lot.contains(",")){
                try {
                    String[] tmp = lot.split(",");
                    params.addBodyParameter("part", tmp[0]);
                    params.addBodyParameter("lot", tmp[1]);
                    params.addBodyParameter("qty", tmp[2]);
                }catch (Exception e){
                    Toasty.error(CreateQrActivity.this, "请检查输入的二维码是否正确", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }
        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.POST,
                Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=LotCheck",
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        loadingDialog.dismiss();
                        BaseData data1 = SceoUtil.parseJson(responseInfo.result, BaseData.class);
                        if (data1.getStatus() == 0){
//                            Intent intent = new Intent(CreateQrActivity.this, PrintActivity.class);
//                            intent.putExtra("data", (Serializable) data);
//                            intent.putExtra("flag", true);
//                            startActivity(intent);
//
//                            cetList.clear();
//                            data.clear();
//                            linearLayout.removeAllViews();
//                            createCet();
                            List<Boolean> status = new ArrayList<>();
                            for (ClearEditText ct : cetList){
                                if (!TextUtils.isEmpty(ct.getText().toString().replaceAll("\\s*", ""))){
                                    status.add(handleText(ct.getText().toString().replaceAll("\\s*", "")));
                                }
                            }
                            if (!status.contains(false)){
                                Intent intent = new Intent(CreateQrActivity.this, PrintActivity.class);
                                intent.putExtra("data", (Serializable) data);
                                intent.putExtra("flag", true);
                                startActivity(intent);

                                cetList.clear();
                                data.clear();
                                linearLayout.removeAllViews();
                                createCet();
                            }
                        }else if (data1.getStatus() == 88) {
                            flag = false;
                            Toasty.error(CreateQrActivity.this, data1.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(CreateQrActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            flag = false;
                            Toasty.error(CreateQrActivity.this, data1.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingDialog.show();
                    }
                });


//        List<Boolean> status = new ArrayList<>();
//        for (ClearEditText ct : cetList){
//            if (!TextUtils.isEmpty(ct.getText().toString().replaceAll("\\s*", ""))){
////                status.add(handleText(ct.getText().toString().replaceAll("\\s*", "")));
//                status.add(check(ct.getText().toString().replaceAll("\\s*", "")));
//            }
//        }
//        if (!status.contains(false)){
//            Intent intent = new Intent(CreateQrActivity.this, PrintActivity.class);
//            intent.putExtra("data", (Serializable) data);
//            intent.putExtra("flag", true);
//            startActivity(intent);
//
//            cetList.clear();
//            data.clear();
//            linearLayout.removeAllViews();
//            createCet();
//        }
    }

    private boolean handleText(String text){
        if (text.length() > 10 && text.contains(",")){
            try {
                String[] tmp = text.split(",");
                OutputPrintItem item = new OutputPrintItem();
                item.setBcls_part(tmp[0]);
                if (tmp[1].length() <= 4){
                    item.setBcls_dc(tmp[1]);
                    item.setBcls_vend("");
                    item.setBcls_sn("");
                }else if (tmp[1].length() > 4 && tmp[1].length() <= 12){
                    item.setBcls_dc(tmp[1].substring(0, 4));
                    item.setBcls_sn(tmp[1].substring(4, tmp[1].length()));
                    item.setBcls_vend("");
                }else {
                    item.setBcls_dc(tmp[1].substring(0, 4));
                    item.setBcls_sn(tmp[1].substring(tmp[1].length()-8, tmp[1].length()));
                    item.setBcls_vend(tmp[1].substring(4, tmp[1].length() - 8));
                }
                item.setBcls_qty(tmp[2]);
                item.setBcls_lot(text);
                data.add(item);
                return true;
            }catch (Exception e){
                Toasty.error(CreateQrActivity.this, "请检查输入的二维码是否正确", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toasty.error(CreateQrActivity.this, "请输入正确的二维码", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    boolean flag = false;
    private boolean check(final String text){
        if (text.length() > 20 && text.contains(",")){
            try {
                final String[] tmp = text.split(",");
                OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=LotCheck")
                        .tag(this)
                        .params("sessionkey", SceoUtil.getSessionKey(this))
                        .params("lot", tmp[1])
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                BaseData data1 = SceoUtil.parseJson(s, BaseData.class);
                                if (data1.getStatus() == 0){
                                    OutputPrintItem item = new OutputPrintItem();
                                    item.setBcls_part(tmp[0]);
                                    if (tmp[1].length() <= 4){
                                        item.setBcls_dc(tmp[1]);
                                        item.setBcls_vend("");
                                        item.setBcls_sn("");
                                    }else if (tmp[1].length() > 4 && tmp[1].length() <= 12){
                                        item.setBcls_dc(tmp[1].substring(0, 4));
                                        item.setBcls_sn(tmp[1].substring(4, tmp[1].length()));
                                        item.setBcls_vend("");
                                    }else {
                                        item.setBcls_dc(tmp[1].substring(0, 4));
                                        item.setBcls_sn(tmp[1].substring(tmp[1].length()-8, tmp[1].length()));
                                        item.setBcls_vend(tmp[1].substring(4, tmp[1].length() - 8));
                                    }
                                    item.setBcls_qty(tmp[2]);
                                    item.setBcls_lot(text);
                                    data.add(item);
                                    flag = true;
                                }else if (data1.getStatus() == 88) {
                                    flag = false;
                                    Toasty.error(CreateQrActivity.this, data1.getMessage(), Toast.LENGTH_SHORT, true).show();
                                    SceoUtil.gotoLogin(CreateQrActivity.this);
                                    SceoApplication.getInstance().exit();
                                }else {
                                    flag = false;
                                    Toasty.error(CreateQrActivity.this, data1.getMessage(), Toast.LENGTH_SHORT, true).show();
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
                return flag;
            }catch (Exception e){
                Toasty.error(CreateQrActivity.this, "请检查输入的二维码是否正确", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toasty.error(CreateQrActivity.this, "请输入正确的二维码", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
