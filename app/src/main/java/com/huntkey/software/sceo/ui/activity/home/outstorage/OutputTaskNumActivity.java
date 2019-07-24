package com.huntkey.software.sceo.ui.activity.home.outstorage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.OutputPickData;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
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

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/4/26.
 */

public class OutputTaskNumActivity extends KnifeBaseActivity {

    @BindView(R.id.otn_toolbar)
    Toolbar toolbar;
    @BindView(R.id.otn_edittext)
    ClearEditText nbrCet;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_outputtasknum;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "出库扫描作业");

        nbrCet.requestFocus();
        loadingDialog = new LoadingUncancelable(this);

        String nbr = getIntent().getStringExtra("nbr");
        nbrCet.setText(nbr);
    }

    @OnClick(R.id.out_btn)
    void allocation(){
        if (nbrCet.getText().toString().trim().length() == 0){
            Toasty.warning(this, "任务单号不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=Pick")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrCet.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        OutputPickData data = SceoUtil.parseJson(s, OutputPickData.class);
                        if ("0".equals(data.getData().getIsscan())){
                            Toasty.info(OutputTaskNumActivity.this, "非扫码出库作业", Toast.LENGTH_SHORT).show();
                        }
                        if (data.getStatus() == 0){
                            Toasty.info(OutputTaskNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent(OutputTaskNumActivity.this, StorageLocationActivity.class);
                            intent.putExtra("nbr", nbrCet.getText().toString().trim());
                            intent.putExtra("isscan", data.getData().getIsscan()); //isscan 1-扫码  0-不扫码
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputTaskNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputTaskNumActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.info(OutputTaskNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputTaskNumActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.out_delete)
    void delete(){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=DelPick")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrCet.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(OutputTaskNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputTaskNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputTaskNumActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputTaskNumActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputTaskNumActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
