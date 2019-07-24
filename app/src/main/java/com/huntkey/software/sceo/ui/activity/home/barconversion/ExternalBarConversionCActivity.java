package com.huntkey.software.sceo.ui.activity.home.barconversion;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.ExternalBarConversionBData;
import com.huntkey.software.sceo.entity.ExternalBarConversionBItem;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.activity.home.personalprint.PersonalPrintActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/4/10.
 */

public class ExternalBarConversionCActivity extends KnifeBaseActivity {

    @BindView(R.id.ebc_c_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ebc_c_addr)
    TextView addTv;//供应商
    @BindView(R.id.ebc_c_nbr)
    TextView nbrTv;//收货单号
    @BindView(R.id.ebc_c_part)
    TextView partTv;//料号
    @BindView(R.id.ebc_c_dc)
    ClearEditText dcEt;//生产周期
    @BindView(R.id.ebc_c_qty)
    ClearEditText qtyEt;//数量
    @BindView(R.id.ebc_c_min_pack)
    ClearEditText minpackEt;//最小包装
    @BindView(R.id.ebc_c_box)
    ClearEditText boxEt;//每箱包数
    @BindView(R.id.ebc_c_lot)
    ClearEditText lotEt;//批次
    @BindView(R.id.ebc_c_btn)
    Button addBtn;
    @BindView(R.id.ebc_c_layout2)
    LinearLayout nbrLayout;

    private ExternalBarConversionBData bdata;
    private String tmpLine;
    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_externalbarconversion_c;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "外部条码转换");

        bdata = (ExternalBarConversionBData) getIntent().getSerializableExtra("data");
        if (bdata == null || bdata.getData() == null || bdata.getData().size() == 0){
            return;
        }
        ExternalBarConversionBItem item = bdata.getData().get(0);
        if ("0".equals(item.getLstd_line())){
            nbrLayout.setVisibility(View.GONE);
        }else {
            nbrLayout.setVisibility(View.VISIBLE);
            nbrTv.setText(item.getLstm_nbr());
        }
        addTv.setText(item.getLstm_addr());
        partTv.setText(item.getLstd_part());
        dcEt.setText(item.getLstd_dc());
        qtyEt.setText(item.getLstd_qty());
        minpackEt.setText(item.getLstd_min_pack());
        boxEt.setText(item.getLstd_box());
        lotEt.setText(item.getLstd_lot());
        tmpLine = item.getLstd_line();

        dcEt.addTextChangedListener(textWatcher);
        qtyEt.addTextChangedListener(textWatcher);
        minpackEt.addTextChangedListener(textWatcher);
        boxEt.addTextChangedListener(textWatcher);
        lotEt.addTextChangedListener(textWatcher);

        loadingDialog = new LoadingUncancelable(this);
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

    @OnClick(R.id.ebc_c_btn)
    void add2PrintLine(){
        if (TextUtils.isEmpty(dcEt.getText().toString().trim()) || !dcEt.getText().toString().trim().matches("[0-9]+") || dcEt.getText().toString().trim().length() != 4){
            Toasty.warning(ExternalBarConversionCActivity.this, "请输入正确的周期", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (TextUtils.isEmpty(qtyEt.getText().toString().trim())){
            Toasty.warning(ExternalBarConversionCActivity.this, "请输入正确的数量", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (TextUtils.isEmpty(minpackEt.getText().toString().trim())){
            Toasty.warning(ExternalBarConversionCActivity.this, "请输入正确的最小包装", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (TextUtils.isEmpty(boxEt.getText().toString().trim())){
            Toasty.warning(ExternalBarConversionCActivity.this, "请输入正确的每箱包数", Toast.LENGTH_SHORT, true).show();
            return;
        }

        addBtn.setEnabled(false);
        addBtn.setBackgroundColor(getResources().getColor(R.color.send_btn_click));
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=SavePrint")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(ExternalBarConversionCActivity.this))
                .params("nbr", nbrTv.getText().toString().trim())
                .params("addr", addTv.getText().toString().trim())
                .params("part", partTv.getText().toString().trim())
                .params("line", tmpLine)
                .params("dc", dcEt.getText().toString().trim())
                .params("lot", lotEt.getText().toString().trim())
                .params("minpack", minpackEt.getText().toString().trim())
                .params("box", boxEt.getText().toString().trim())
                .params("qty", qtyEt.getText().toString().trim())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Intent intent = new Intent(ExternalBarConversionCActivity.this, PersonalPrintActivity.class);
                            intent.putExtra("src", "EWP01AA");
                            startActivity(intent);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(ExternalBarConversionCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(ExternalBarConversionCActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(ExternalBarConversionCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(ExternalBarConversionCActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.ebc_c_close)
    void close(){
        AppManager.getInstance().finishSingleActivityByClass(ExternalBarConversionBActivity.class);
        AppManager.getInstance().finishSingleActivityByClass(ExternalBarConversionCActivity.class);
    }
}
