package com.huntkey.software.sceo.ui.activity.home.storagequery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.StorageQueryData;
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
 * Created by chenl on 2018/2/8.
 */

public class StorageQueryAActivity extends KnifeBaseActivity {

    @BindView(R.id.stoq_a_toolbar)
    Toolbar toolbar;
    @BindView(R.id.stoq_a_cet)
    ClearEditText clearEditText;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_storage_query_a;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "库存查询");

        clearEditText.requestFocus();
        clearEditText.setOnKeyListener(new MyOnKeyListener());

        loadingDialog = new LoadingUncancelable(this);
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                // 料号要从扫描的条码中截取
                String lot = clearEditText.getText().toString().replaceAll("\\s*", "");
                if (!TextUtils.isEmpty(lot) && lot.length() > 10 && lot.contains(",")){
                    String[] tmp = lot.split(",");
                    doNetwork(tmp[0]);
                }else {
                    doNetwork(lot);
                }
            }
            return false;
        }
    }

    private void doNetwork(String queryStr) {
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP20AA&pcmd=QueryMain")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("part", queryStr)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        StorageQueryData data = SceoUtil.parseJson(s, StorageQueryData.class);
                        if (data.getStatus() == 0){
                            if (data.getData().size() > 0) {
                                for (int i = 0; i < data.getData().size(); i++){
                                    data.getData().get(i).setNum(i + 1);
                                }
                            }
                            Intent intent = new Intent(StorageQueryAActivity.this, StorageQueryBActivity.class);
                            intent.putExtra("data", (Serializable) data.getData());
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(StorageQueryAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(StorageQueryAActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(StorageQueryAActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
                        Toasty.error(StorageQueryAActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    @OnClick(R.id.stoq_a_nextstep)
    void query() {
        // 料号要从扫描的条码中截取
        String lot = clearEditText.getText().toString().replaceAll("\\s*", "");
        if (!TextUtils.isEmpty(lot) && lot.length() > 10 && lot.contains(",")){
            String[] tmp = lot.split(",");
            doNetwork(tmp[0]);
        } else {
            doNetwork(lot);
        }
    }
}
