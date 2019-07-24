package com.huntkey.software.sceo.ui.activity.home.orderinstore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.OrderInStoreItem;
import com.huntkey.software.sceo.entity.OrderInstoreDeptItem;
import com.huntkey.software.sceo.entity.OrderInstoreDeptResult;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.OrderInstoreBSpinnerAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/8/21.
 */

public class OrderInstoreBActivity extends KnifeBaseActivity {

    @BindView(R.id.ois_b_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ois_b_nbr)
    TextView nbrTv;//制令单号
    @BindView(R.id.ois_b_part)
    TextView partTv;//料号
    @BindView(R.id.ois_b_spinner)
    Spinner partSpinner;
    @BindView(R.id.ois_b_group)
    TextView groupTv;//生产班组
    @BindView(R.id.ois_b_dept)
    ClearEditText deptCet;//输入记账部门
    @BindView(R.id.ois_b_notgood)
    CheckBox notgoodCheckbox;//不良入库
    @BindView(R.id.ois_b_store)
    ClearEditText storeCet;//入库仓
    @BindView(R.id.ois_b_num)
    ClearEditText numCet;//入库量

    private List<OrderInStoreItem> data = new ArrayList<>();
    private String did;
    private String part;
    private String type;
    private String lot;
    private String ref;
    private String um;
    private String oldQty;
    private String deptId;
    private String storeId;
    private LoadingUncancelable loadingDialog;
    private OrderInstoreDeptPop orderInstoreDeptPop;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_orderinstore_b;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "制令入库");
        loadingDialog = new LoadingUncancelable(this);
        data = (List<OrderInStoreItem>) getIntent().getSerializableExtra("data");
        if (data.size() == 1){
            OrderInStoreItem item = data.get(0);
            nbrTv.setText(item.getWo_nbr());
            partTv.setVisibility(View.VISIBLE);
            partSpinner.setVisibility(View.GONE);
            partTv.setText(item.getWo_part());
            groupTv.setText(item.getWo_dept());
            storeCet.setText(item.getWo_loc());
            numCet.setText(item.getWo_qty());
            did = item.getWo_id();
            part = item.getWo_part();
            type = item.getWo_type();
            lot = item.getWo_lot();
            ref = item.getWo_ref();
            um = item.getWo_um();
            oldQty = item.getWo_qty();
        }else if (data.size() > 1){
            OrderInStoreItem item = data.get(0);
            nbrTv.setText(item.getWo_nbr());
            partTv.setVisibility(View.GONE);
            partSpinner.setVisibility(View.VISIBLE);
            partSpinner.setAdapter(new OrderInstoreBSpinnerAdapter(this, data));
            partSpinner.setOnItemSelectedListener(new MyOnItemSelectedListener());
            groupTv.setText(item.getWo_dept());
            storeCet.setText(item.getWo_loc());
            numCet.setText(item.getWo_qty());
            did = item.getWo_id();
            part = item.getWo_part();
            type = item.getWo_type();
            lot = item.getWo_lot();
            ref = item.getWo_ref();
            um = item.getWo_um();
            oldQty = item.getWo_qty();
        }

        deptCet.setImeOptions(EditorInfo.IME_ACTION_DONE);
        deptCet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    getDepts(deptCet.getText().toString().replaceAll("\\s*", ""));
                    return true;
                }
                return false;
            }
        });
        storeCet.setImeOptions(EditorInfo.IME_ACTION_DONE);
        storeCet.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    getStores(storeCet.getText().toString().replaceAll("\\s*", ""));
                    return true;
                }
                return false;
            }
        });
    }

    private void getStores(String keyword){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP19AA&pcmd=GetLoc")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("keyword", keyword)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        final OrderInstoreDeptResult result = SceoUtil.parseJson(s, OrderInstoreDeptResult.class);
                        if (result != null && result.getResults() != null && result.getResults().size() > 0){
                            OrderInstoreDeptPop.Builder builder = new OrderInstoreDeptPop.Builder(OrderInstoreBActivity.this);
                            for (int i = 0; i < result.getResults().size(); i++){
                                builder.addItem(i, result.getResults().get(i).getValue());
                            }
                            orderInstoreDeptPop = builder.build();
                            orderInstoreDeptPop.showPopupWindow();
                            orderInstoreDeptPop.setOnListPopupItemClickListener(new OrderInstoreDeptPop.OnListPopupItemClickListener() {
                                @Override
                                public void onItemClick(int what) {
                                    storeCet.setText(result.getResults().get(what).getValue());
                                    storeId = result.getResults().get(what).getId();
                                    orderInstoreDeptPop.dismiss();
                                }
                            });
                        }
                    }
                });
    }

    private void getDepts(String keyword){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP19AA&pcmd=GetDept")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("keyword", keyword)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        final OrderInstoreDeptResult result = SceoUtil.parseJson(s, OrderInstoreDeptResult.class);
                        if (result != null && result.getResults() != null && result.getResults().size() > 0){
                            OrderInstoreDeptPop.Builder builder = new OrderInstoreDeptPop.Builder(OrderInstoreBActivity.this);
                            for (int i = 0; i < result.getResults().size(); i++){
                                builder.addItem(i, result.getResults().get(i).getValue());
                            }
                            orderInstoreDeptPop = builder.build();
                            orderInstoreDeptPop.showPopupWindow();
                            orderInstoreDeptPop.setOnListPopupItemClickListener(new OrderInstoreDeptPop.OnListPopupItemClickListener() {
                                @Override
                                public void onItemClick(int what) {
                                    deptCet.setText(result.getResults().get(what).getValue());
                                    deptId = result.getResults().get(what).getId();
                                    orderInstoreDeptPop.dismiss();
                                }
                            });
                        }
                    }
                });
    }

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            OrderInStoreItem item = data.get(position);
            nbrTv.setText(item.getWo_nbr());
            groupTv.setText(item.getWo_dept());
            storeCet.setText(item.getWo_loc());
            numCet.setText(item.getWo_qty());
            did = item.getWo_id();
            part = item.getWo_part();
            type = item.getWo_type();
            lot = item.getWo_lot();
            ref = item.getWo_ref();
            um = item.getWo_um();
            oldQty = item.getWo_qty();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @OnClick(R.id.ois_b_btn)
    void submit(){
        if (deptCet.getText().toString().trim().length() == 0){
            Toasty.warning(OrderInstoreBActivity.this, "记账部门不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (storeCet.getText().toString().trim().length() == 0){
            Toasty.warning(OrderInstoreBActivity.this, "入库仓不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (numCet.getText().toString().trim().length() == 0){
            Toasty.warning(OrderInstoreBActivity.this, "入库量不能为空", Toast.LENGTH_SHORT, true).show();
            return;
        }
        try {
            int newQty = Integer.parseInt(numCet.getText().toString().replaceAll("\\s*", ""));
            int oldQty2 = Integer.parseInt(oldQty);
            if (newQty > oldQty2){
                Toasty.warning(OrderInstoreBActivity.this, "入库数量不能大于待提单数：" + oldQty, Toast.LENGTH_SHORT, true).show();
                return;
            }
            if (newQty <= 0){
                Toasty.warning(OrderInstoreBActivity.this, "入库数量必须大于0", Toast.LENGTH_SHORT, true).show();
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        String dept = TextUtils.isEmpty(deptId) ? deptCet.getText().toString().replaceAll("\\s*", "") : deptId;
        String store = TextUtils.isEmpty(storeId) ? storeCet.getText().toString().replaceAll("\\s*", "") : storeId;

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP19AA&pcmd=Submit")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrTv.getText().toString().replaceAll("\\s*", ""))
                .params("did", did)
                .params("dept", dept)
                .params("ckbad", notgoodCheckbox.isChecked() ? "1" : "0")
                .params("part", part)
                .params("type", type)
                .params("loc", store)
                .params("lot", lot)
                .params("ref", ref)
                .params("qty", numCet.getText().toString().replaceAll("\\s*", ""))
                .params("um", um)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(OrderInstoreBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            AppManager.getInstance().finishSingleActivityByClass(OrderInstoreAActivity.class);
                            Intent intent = new Intent(OrderInstoreBActivity.this, OrderInstoreAActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(OrderInstoreBActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OrderInstoreBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OrderInstoreBActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OrderInstoreBActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
}
