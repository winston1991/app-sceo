package com.huntkey.software.sceo.ui.activity.home.barconversion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.ExternalBarConversionAData;
import com.huntkey.software.sceo.entity.ExternalBarConversionAItem;
import com.huntkey.software.sceo.entity.ExternalBarConversionBData;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.ExterBarConverSpinnerAdapter;
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
 * Created by chenl on 2017/4/10.
 */

public class ExternalBarConversionBActivity extends KnifeBaseActivity {

    @BindView(R.id.ebc_b_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ebc_b_addr)
    TextView addrTv;//供应商
    @BindView(R.id.ebc_b_layout2)
    LinearLayout nbrLayout;//单号layout
    @BindView(R.id.ebc_b_nbr)
    TextView nbrTv;
    @BindView(R.id.ebc_b_layout3)
    LinearLayout lineLayout;//项次layout
    @BindView(R.id.ebc_b_spinner)
    Spinner spinner;//项次spinner
    @BindView(R.id.ebc_b_part)
    ClearEditText partEt;

    private ExternalBarConversionAData adata;
    private ExternalBarConversionAItem item;
    private String tmpLine;
    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_externalbarconversion_b;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "外部条码转换");

        loadingDialog = new LoadingUncancelable(this);

        adata = (ExternalBarConversionAData) getIntent().getSerializableExtra("nbr");
        if (adata == null || adata.getData() == null || adata.getData().size() == 0){
            return;
        }
        item = adata.getData().get(0);
        if ("0".equals(item.getLstd_line())){
            nbrLayout.setVisibility(View.GONE);
            lineLayout.setVisibility(View.GONE);
            partEt.setHint("扫描料号");
        }else {
            nbrTv.setText(item.getLstm_nbr());
            spinner.setAdapter(new ExterBarConverSpinnerAdapter(this, adata.getData()));
            spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
            partEt.setHint("选择项次或者扫描料号");
        }
        addrTv.setText(item.getLstm_addr());

        partEt.setOnKeyListener(new MyOnKeyListener());
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                initNetwork();
            }
            return false;
        }
    }

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            tmpLine = adata.getData().get(position).getLstd_line();
            partEt.setText(adata.getData().get(position).getLstd_part());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void initNetwork(){
        if (partEt.getText().toString().length() == 0){
            Toasty.warning(ExternalBarConversionBActivity.this, "料号不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=QueryDetail")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(ExternalBarConversionBActivity.this))
                .params("nbr", nbrTv.getText().toString().trim())
                .params("addr", addrTv.getText().toString().trim())
                .params("part", partEt.getText().toString().trim())
                .params("line", tmpLine)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ExternalBarConversionBData data = SceoUtil.parseJson(s, ExternalBarConversionBData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(ExternalBarConversionBActivity.this, ExternalBarConversionCActivity.class);
                            intent.putExtra("data", data);
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(ExternalBarConversionBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(ExternalBarConversionBActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(ExternalBarConversionBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
    }

    @OnClick(R.id.ebc_b_btn)
    void nextStep(){
        initNetwork();
    }

    @OnClick(R.id.ebc_b_close)
    void close(){
        AppManager.getInstance().finishSingleActivityByClass(ExternalBarConversionAActivity.class);
        AppManager.getInstance().finishSingleActivityByClass(ExternalBarConversionBActivity.class);
    }

}
