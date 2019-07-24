package com.huntkey.software.sceo.ui.activity.home.storageadjust;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.AdjustOrderData;
import com.huntkey.software.sceo.entity.AdjustOrderItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.AdjustOrderAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
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
 * Created by chenl on 2017/5/3.
 */

public class AdjustOrderActivity extends KnifeBaseActivity {

    @BindView(R.id.sam_toolbar)
    Toolbar toolbar;
    @BindView(R.id.sam_cet)
    ControlEditText samCet;
    @BindView(R.id.sam_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.sam_emptyview)
    View emptyView;

    private AdjustOrderAdapter adapter;
    private LoadingUncancelable loadingDialog;
    private List<String> cacheLots = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_adjustorder;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "储位调整");

        samCet.requestFocus();
        samCet.setOnKeyListener(new MyOnKeyListener());

        loadingDialog = new LoadingUncancelable(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                emptyView.setVisibility(View.GONE);
                checkLot(samCet.getText().toString().replaceAll("\\s*", ""));
                samCet.setText("");
            }
            return false;
        }
    }

    private void checkLot(final String lot){
        for (String cl : cacheLots){
            if (cl.equalsIgnoreCase(lot)){
                Toasty.warning(AdjustOrderActivity.this, "该条码已经录入", Toast.LENGTH_SHORT, true).show();
                return;
            }
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP08AA&pcmd=CheckLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("lot", lot)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        AdjustOrderData data = SceoUtil.parseJson(s, AdjustOrderData.class);
                        if (data.getStatus() == 0){
                            adapter.addData(data.getData());
                            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                            cacheLots.add(lot);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AdjustOrderActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AdjustOrderActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AdjustOrderActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(AdjustOrderActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private void initAdapter() {
        adapter = new AdjustOrderAdapter(R.layout.item_adjustorder, new ArrayList<AdjustOrderItem>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
                AdjustOrderItem item = adapter.getData().get(position);
                delItem(item.getLd_lot(), item.getLd_part(), item.getLd_loc(), position, item.getLot_sn());
                return false;
            }
        });
    }

    private void delItem(final String sn, String part, final String loc, final int position, final String lot){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP08AA&pcmd=Del")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("sn", sn)
                .params("part", part)
                .params("loc", loc)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            for (String cl : cacheLots){
                                if (cl.equalsIgnoreCase(lot)){
                                    cacheLots.remove(cl);
                                }
                            }
                            adapter.remove(position);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AdjustOrderActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AdjustOrderActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AdjustOrderActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.sam_nextstep)
    void nextStep(){
        if (adapter.getData().size() == 0){
            Toasty.warning(AdjustOrderActivity.this, "请扫描待挪动物料", Toast.LENGTH_SHORT, true).show();
            return;
        }
        Intent intent = new Intent(AdjustOrderActivity.this, AdjustmentActivity.class);
        intent.putExtra("data", (Serializable) adapter.getData());
        startActivity(intent);
    }
}
