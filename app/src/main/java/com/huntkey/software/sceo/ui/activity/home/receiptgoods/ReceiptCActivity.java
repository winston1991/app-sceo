package com.huntkey.software.sceo.ui.activity.home.receiptgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.github.clans.fab.FloatingActionButton;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.ReceiptCLv0Data;
import com.huntkey.software.sceo.entity.ReceiptCLv0Item;
import com.huntkey.software.sceo.entity.ReceiptCLv1Data;
import com.huntkey.software.sceo.entity.ReceiptCLv1Item;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.ReceiptCAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/7/10.
 */

public class ReceiptCActivity extends KnifeBaseActivity {

    @BindView(R.id.rpt_c_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rpt_c_cet)
    ControlEditText rptCet;
    @BindView(R.id.rpt_c_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.rpt_c_fab)
    FloatingActionButton floatingActionButton;

    private View emptyView;
    private View errorView;
    private LoadingUncancelable loadingDialog;

    private String nbrs;
    private ReceiptCAdapter adapter;

    private List<String> cacheLot = new ArrayList<>();//记录扫描的条码
    private List<ReceiptCLv0Item> lv0List = new ArrayList<>();
    private List<ReceiptCLv1Item> lv1List = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_receipt_c;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "收货点货");

        rptCet.requestFocus();
        rptCet.setOnKeyListener(new MyOnKeyListener());

        nbrs = getIntent().getStringExtra("nbrs");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0){
                    floatingActionButton.hide(true);
                }else {
                    floatingActionButton.show(true);
                }
            }
        });

        loadingDialog = new LoadingUncancelable(this);

        initEmptyAndErrorView();
        initAdapter();
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                doScan(rptCet.getText().toString().replaceAll("\\s*", ""));
                rptCet.setText("");
            }
            return false;
        }
    }

    private void doScan(final String lot){
        if (!lot.contains(",")){
            Toasty.warning(ReceiptCActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (lot.length() < 15){
            Toasty.warning(ReceiptCActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
            return;
        }
        for (String cl : cacheLot){
            if (cl.equalsIgnoreCase(lot)){
                Toasty.warning(ReceiptCActivity.this, "该条码已经录入", Toast.LENGTH_SHORT, true).show();
                return;
            }
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP16AA&pcmd=ScanLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("lot", lot)
                .params("nbr", nbrs)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        boolean flag = false;
                        ReceiptCLv1Data data = SceoUtil.parseJson(s, ReceiptCLv1Data.class);
                        if (data.getStatus() == 0){
                            cacheLot.add(lot);
                            if (data.getData().size() > 0){
                                ReceiptCLv1Item lv1Item = data.getData().get(0);
                                for (int i = 0; i < lv0List.size(); i++){
                                    adapter.collapse(i);//先关闭再打开
                                    if (lv1Item.getLd_part().equalsIgnoreCase(lv0List.get(i).getLstd_part())){
                                        flag = true;
//                                        if (!lv0List.get(i).getLstd_qty().equals(lv0List.get(i).getPointNum())){
                                            lv1Item.setLine(lv0List.get(i).getNum());

                                            //计算no
                                            if (lv0List.get(i).hasSubItem()){
                                                lv1Item.setNo(lv0List.get(i).getNum() + "." + (lv0List.get(i).getSubItems().size() + 1));
                                            }else {
                                                lv1Item.setNo(lv0List.get(i).getNum() + ".1");
                                            }

//                                            float f = Float.parseFloat(lv0List.get(i).getPointNum()) + Float.parseFloat(lv1Item.getLd_qty());
//                                            if (Math.abs(f-(int)f) < 0.000001){
//                                                lv0List.get(i).setPointNum(String.valueOf((int)Float.parseFloat(lv0List.get(i).getPointNum()) + (int)Float.parseFloat(lv1Item.getLd_qty())));
//                                            }else {
//                                                lv0List.get(i).setPointNum(String.valueOf(Float.parseFloat(lv0List.get(i).getPointNum()) + Float.parseFloat(lv1Item.getLd_qty())));
                                                BigDecimal bd1 = new BigDecimal(lv0List.get(i).getPointNum());
                                                BigDecimal bd2 = new BigDecimal(lv1Item.getLd_qty());
                                                lv0List.get(i).setPointNum(bd1.add(bd2).stripTrailingZeros().toPlainString());
//                                            }

                                            // -------------------------------
                                            if (lv0List.get(i).getSubItems() != null){
                                                try {
                                                    List<ReceiptCLv1Item> subItems = lv0List.get(i).getSubItems();
                                                    subItems.add(lv1Item);
                                                    Collections.sort(subItems, new Comparator<ReceiptCLv1Item>() {
                                                        @Override
                                                        public int compare(ReceiptCLv1Item o1, ReceiptCLv1Item o2) {
                                                            return o1.getLd_sn().substring(0, 4).compareTo(o2.getLd_sn().substring(0, 4));
                                                        }
                                                    });
                                                    lv0List.get(i).setSubItems(subItems);
                                                    adapter.notifyDataSetChanged();
                                                    adapter.expand(i);
                                                }catch (Exception e){
                                                    e.printStackTrace();
                                                }
                                            }else {
                                                lv0List.get(i).addSubItem(lv1Item);
                                                adapter.notifyDataSetChanged();
                                                adapter.expand(i);
                                            }
                                            // -------------------------------

//                                            lv0List.get(i).addSubItem(lv1Item);
//                                            adapter.notifyDataSetChanged();
//                                            adapter.expand(i);

                                            recyclerView.scrollToPosition(i);

                                            lv1List.add(lv1Item);

                                            // 相同批次标红
                                            Set<String> repeated = new HashSet<>();
                                            List<String> results = new ArrayList<>();
                                            for (ReceiptCLv1Item item : lv1List){
                                                if (!repeated.add(item.getLd_sn())){
                                                    results.add(item.getLd_sn());
                                                }
                                            }
                                            for (ReceiptCLv1Item item : lv1List){
                                                item.setFlag(false);
                                                for (String res : results){
                                                    if (item.getLd_sn().equalsIgnoreCase(res)){
                                                        item.setFlag(true);
                                                    }
                                                }
                                            }
                                            adapter.notifyDataSetChanged();

                                            break;
//                                        }else {
//                                            continue;
//                                        }
                                    }else {
                                        adapter.collapse(i);
                                    }
                                }

                                if (!flag){
                                    new SweetAlertDialog(ReceiptCActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("当前二维码没有匹配项")
                                            .setConfirmText("确定")
                                            .showCancelButton(false)
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                                                @Override
                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                    sweetAlertDialog.dismiss();
                                                }
                                            })
                                            .show();
                                }
                            }else {
                                Toasty.warning(ReceiptCActivity.this, "条码不存在", Toast.LENGTH_SHORT, true).show();
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(ReceiptCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(ReceiptCActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(ReceiptCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(ReceiptCActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private void initEmptyAndErrorView(){
        emptyView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup)recyclerView.getParent(), false);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNetwork();
            }
        });
        errorView = getLayoutInflater().inflate(R.layout.error_view, (ViewGroup)recyclerView.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doNetwork();
            }
        });
    }

    private void initAdapter(){
        adapter = new ReceiptCAdapter(new ArrayList<MultiItemEntity>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
                ReceiptCLv1Item delItem = (ReceiptCLv1Item) adapter1.getItem(position);
                cacheLot.remove(delItem.getLd_lot());
                lv1List.remove(delItem);

                // 相同批次标红
                Set<String> repeated = new HashSet<>();
                List<String> results = new ArrayList<>();
                for (ReceiptCLv1Item item : lv1List){
                    if (!repeated.add(item.getLd_sn())){
                        results.add(item.getLd_sn());
                    }
                }
                for (ReceiptCLv1Item item : lv1List){
                    item.setFlag(false);
                    for (String res : results){
                        if (item.getLd_sn().equalsIgnoreCase(res)){
                            item.setFlag(true);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

                for (int i = 0; i < lv0List.size(); i++){
                    adapter.collapse(i);//先关闭
                    if (delItem.getLd_part().equalsIgnoreCase(lv0List.get(i).getLstd_part())){
                        lv0List.get(i).removeSubItem(delItem);
//                        float f = Float.parseFloat(lv0List.get(i).getPointNum()) - Float.parseFloat(delItem.getLd_qty());
//                        if (Math.abs(f-(int)f) < 0.000001){
//                            lv0List.get(i).setPointNum(String.valueOf((int)Float.parseFloat(lv0List.get(i).getPointNum()) - (int)Float.parseFloat(delItem.getLd_qty())));
//                        }else {
//                            lv0List.get(i).setPointNum(String.valueOf(Float.parseFloat(lv0List.get(i).getPointNum()) - Float.parseFloat(delItem.getLd_qty())));
                            BigDecimal bd1 = new BigDecimal(lv0List.get(i).getPointNum());
                            BigDecimal bd2 = new BigDecimal(delItem.getLd_qty());
                            lv0List.get(i).setPointNum(bd1.subtract(bd2).stripTrailingZeros().toPlainString());
//                        }

                        //计算no
                        if (lv0List.get(i).hasSubItem()){
                            List<ReceiptCLv1Item> tmpItem = lv0List.get(i).getSubItems();
                            for (int j = 0; j < tmpItem.size(); j++){
                                tmpItem.get(j).setNo(tmpItem.get(j).getLine() + "." + (j + 1));
                            }
                        }

                        adapter.notifyDataSetChanged();
                        if (lv0List.get(i).hasSubItem()){
                            adapter.expand(i);
                        }
                    }else {
                        adapter.collapse(i);
                    }
                }
                return false;
            }
        });

        doNetwork();
    }

    private void doNetwork(){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP16AA&pcmd=QueryDetail")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrs)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        ReceiptCLv0Data data = SceoUtil.parseJson(s, ReceiptCLv0Data.class);
                        if (data.getStatus() == 0){
                            if (data.getData().size() == 0){
                                adapter.setEmptyView(emptyView);
                            }
                            lv0List = data.getData();
                            for (int i = 0; i < lv0List.size(); i++){
                                lv0List.get(i).setNum(String.valueOf(i + 1));
                            }
                            ArrayList<MultiItemEntity> res = new ArrayList<>();
                            for (ReceiptCLv0Item item : lv0List){
                                res.add(item);
                            }
                            adapter.setNewData(res);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(ReceiptCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(ReceiptCActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(ReceiptCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(ReceiptCActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private void doSubmit(){
        boolean isTrue = true;
        for (ReceiptCLv0Item item : lv0List){
            if (item.getLstd_qty().equals(item.getPointNum())){
                isTrue = isTrue && true;
            }else {
                isTrue = isTrue && false;
            }
        }
        if (!isTrue){
            Toasty.warning(ReceiptCActivity.this, "收货量和已扫量相等才可以提交", Toast.LENGTH_SHORT, true).show();
            return;
        }
        //批次重复校验
        List<String> snList = new ArrayList<>();
        for (ReceiptCLv1Item item : lv1List){
            snList.add(item.getLd_sn());
        }
        if (snList.size() != new HashSet<Object>(snList).size()){
            Toasty.warning(ReceiptCActivity.this, "存在相同批次条码，不允许提交", Toast.LENGTH_SHORT, true).show();
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP16AA&pcmd=Submit")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrs)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(ReceiptCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            AppManager.getInstance().finishSingleActivityByClass(ReceiptAActivity.class);
                            AppManager.getInstance().finishSingleActivityByClass(ReceiptBActivity.class);
                            Intent intent = new Intent(ReceiptCActivity.this, ReceiptAActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(ReceiptCActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(ReceiptCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(ReceiptCActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(ReceiptCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(ReceiptCActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.rpt_c_fab)
    void submit(){
        doSubmit();
    }
}
