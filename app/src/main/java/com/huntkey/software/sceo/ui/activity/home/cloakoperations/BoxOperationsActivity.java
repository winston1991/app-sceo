package com.huntkey.software.sceo.ui.activity.home.cloakoperations;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.BoxOperationsItem;
import com.huntkey.software.sceo.entity.ScanBoxItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.BoxOperationsAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.math.BigDecimal;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/5/9.
 */

public class BoxOperationsActivity extends KnifeBaseActivity {

    @BindView(R.id.boxo_toolbar)
    Toolbar toolbar;
    @BindView(R.id.boxo_current)
    TextView currentTv;
    @BindView(R.id.boxo_nbr)
    TextView nbrTv;//箱号
    @BindView(R.id.boxo_num)
    TextView oldNumTv;//原数量
    @BindView(R.id.boxo_done)
    TextView doneNumTv;//已放数量
    @BindView(R.id.boxo_cet)
    ControlEditText boxCet;
    @BindView(R.id.boxo_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.boxo_float_menu)
    FloatingActionMenu floatingActionMenu;

    private String doneNum= "0";//已放数量
    private String currentStorage;//当前储位

    private ScanBoxItem scanBoxItem;
    private LoadingUncancelable loadingDialog;
    private BoxOperationsAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_boxoperations;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "散箱作业");

        boxCet.requestFocus();
        boxCet.setOnKeyListener(new MyOnKeyListener());

        scanBoxItem = (ScanBoxItem) getIntent().getSerializableExtra("data");
        try {
            nbrTv.setText("条码 " + scanBoxItem.getLd_part() + "," + scanBoxItem.getLd_lot() + "," + scanBoxItem.getLd_qty());
            oldNumTv.setText("原数量 " + scanBoxItem.getLd_qty());
        } catch (Exception e){
            Toasty.warning(BoxOperationsActivity.this, "请扫描/输入正确的条码", Toast.LENGTH_SHORT, true).show();
        }

        loadingDialog = new LoadingUncancelable(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initAdapter();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0){
                    floatingActionMenu.hideMenu(true);
                }else {
                    floatingActionMenu.showMenu(true);
                }
            }
        });
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                String tmpStr = boxCet.getText().toString().replaceAll("\\s*", "");

                if (tmpStr.length() < 10){//判断扫描的是储位还是箱号 暂定<10为储位
                    currentStorage = tmpStr;
                    currentTv.setText("当前储位:" + currentStorage);
                }else {
                    if (currentStorage == null){
                        Toasty.warning(BoxOperationsActivity.this, "请先扫描储位", Toast.LENGTH_SHORT, true).show();
                    }else {
                        BoxOperationsItem item = scanLot(tmpStr);
                        if (item != null){
                            try {
//                                float f = Float.parseFloat(doneNum) + Float.parseFloat(item.getQty());
//                                if (Math.abs(f-(int)f) < 0.000001){
//                                    doneNum = String.valueOf((int)Float.parseFloat(doneNum) + (int)Float.parseFloat(item.getQty()));
//                                }else {
//                                    doneNum = String.valueOf(Float.parseFloat(doneNum) + Float.parseFloat(item.getQty()));
                                    BigDecimal bd1 = new BigDecimal(doneNum);
                                    BigDecimal bd2 = new BigDecimal(item.getQty());
                                    doneNum = bd1.add(bd2).stripTrailingZeros().toPlainString();
//                                }
                                doneNumTv.setText("已放 " + doneNum);
                                adapter.addData(item);
                            }catch (Exception e){
                                Toasty.warning(BoxOperationsActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
                            }
                        }else {
                            Toasty.warning(BoxOperationsActivity.this, "请检查箱号条码正确且勿重复扫描", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                }
                boxCet.setText("");
            }
            return false;
        }
    }

    private BoxOperationsItem scanLot(String str){
        if (!str.contains(",")){
            return null;
        }
        if (str.length() < 15){
            return null;
        }
        for (BoxOperationsItem item : adapter.getData()){
            if (str.equalsIgnoreCase(item.getLot())){
                return  null;
            }
        }
        try {
            BoxOperationsItem item = new BoxOperationsItem();
            String tmp[] = str.split(",");
            item.setLot(str);
            item.setSn(tmp[1]);
            item.setQty(tmp[2]);
            item.setStorage(currentStorage);
            return item;
        }catch (Exception e){
            return null;
        }
    }

    private void initAdapter() {
        adapter = new BoxOperationsAdapter(R.layout.item_boxoperations, new ArrayList<BoxOperationsItem>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
//                float f = Float.parseFloat(doneNum) - Float.parseFloat(adapter.getData().get(position).getQty());
//                if (Math.abs(f-(int)f) < 0.000001) {
//                    doneNum = String.valueOf((int)Float.parseFloat(doneNum) - (int)Float.parseFloat(adapter.getData().get(position).getQty()));
//                }else {
//                    doneNum = String.valueOf(Float.parseFloat(doneNum) - Float.parseFloat(adapter.getData().get(position).getQty()));
                    BigDecimal bd1 = new BigDecimal(doneNum);
                    BigDecimal bd2 = new BigDecimal(adapter.getData().get(position).getQty());
                    doneNum = bd1.subtract(bd2).stripTrailingZeros().toPlainString();
//                }
                doneNumTv.setText("已放 " + doneNum);
                adapter1.remove(position);
                return false;
            }
        });
    }

    @OnClick(R.id.boxo_submit_fab)
    void submit(){
        floatingActionMenu.close(true);
        if (!doneNum.equals(scanBoxItem.getLd_qty())){
            Toasty.warning(BoxOperationsActivity.this, "已放数量等于原数量才可以提交", Toast.LENGTH_SHORT, true).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(this));
        params.addBodyParameter("pid", scanBoxItem.getTskp_id());
        for (BoxOperationsItem item : adapter.getData()){
            params.addBodyParameter("lot", item.getSn());
            params.addBodyParameter("ref", item.getStorage());
            params.addBodyParameter("qty", item.getQty());
        }
        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.POST,
                Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP09AA&pcmd=Submit",
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        loadingDialog.dismiss();
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            AppManager.getInstance().finishSingleActivityByClass(ScanBoxNumActivity.class);
                            Intent intent = new Intent(BoxOperationsActivity.this, ScanBoxNumActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(BoxOperationsActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(BoxOperationsActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        loadingDialog.dismiss();
                        Toasty.error(BoxOperationsActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingDialog.show();
                    }
                });
    }

    @OnClick(R.id.boxo_unusual_fab)
    void unusual(){
        floatingActionMenu.close(true);
        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(BoxOperationsActivity.this, SweetAlertDialog.WARNING_TYPE);
        sweetAlertDialog.setContentText("确定冻结整箱待稽核？");
        sweetAlertDialog.setCancelText("取消");
        sweetAlertDialog.setConfirmText("确定");
        sweetAlertDialog.showCancelButton(true);
        sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
            }
        });
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                doFreeze();
                sweetAlertDialog.dismiss();
            }
        });
        sweetAlertDialog.show();
    }

    private void doFreeze(){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP09AA&pcmd=FrzLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("pid", scanBoxItem.getTskp_id())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            AppManager.getInstance().finishSingleActivityByClass(ScanBoxNumActivity.class);
                            Intent intent = new Intent(BoxOperationsActivity.this, ScanBoxNumActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(BoxOperationsActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(BoxOperationsActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(BoxOperationsActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.boxo_unusual_cancel)
    void cancel(){
        doCancel(scanBoxItem.getTskp_id());
    }

    private void doCancel(String pid){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP09AA&pcmd=CancelLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("pid", pid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            AppManager.getInstance().finishSingleActivityByClass(ScanBoxNumActivity.class);
                            Intent intent = new Intent(BoxOperationsActivity.this, ScanBoxNumActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(BoxOperationsActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(BoxOperationsActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(BoxOperationsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(BoxOperationsActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
