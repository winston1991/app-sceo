package com.huntkey.software.sceo.ui.activity.home.outstockgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.huntkey.software.sceo.entity.OutstockLv0Data;
import com.huntkey.software.sceo.entity.OutstockLv0Item;
import com.huntkey.software.sceo.entity.OutstockLv1Data;
import com.huntkey.software.sceo.entity.OutstockLv1Item;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.OutstockGoodsAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/5/18.
 */

public class OutstockGoodsActivity extends KnifeBaseActivity {

    @BindView(R.id.outstockg_toolbar)
    Toolbar toolbar;
    @BindView(R.id.outstockg_nbrs)
    TextView nbrsTv;
    @BindView(R.id.outstockg_cet)
    ControlEditText osgCet;
    @BindView(R.id.outstockg_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.outstockg_fab)
    FloatingActionButton floatingActionButton;

    private View emptyView;
    private View errorView;
    private LoadingUncancelable loadingDialog;

    private String nbrs;
    private String emp;
    private OutstockGoodsAdapter adapter;

    private List<OutstockLv0Item> lv0List = new ArrayList<>();
    private List<OutstockLv1Item> lv1List = new ArrayList<>();
    private List<String> cacheLot = new ArrayList<>();//记录扫描的条码

    @Override
    protected int getContentViewId() {
        return R.layout.activity_outstockgoods;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "点货");

        osgCet.requestFocus();
        osgCet.setOnKeyListener(new MyOnKeyListener());

        nbrs = getIntent().getStringExtra("nbrs");
        emp = getIntent().getStringExtra("emp");
        nbrsTv.setText(nbrs);

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
                doScan(osgCet.getText().toString().replaceAll("\\s*", ""));
                osgCet.setText("");
            }
            return false;
        }
    }

    private void doScan(final String lot){
        if (!lot.contains(",")){
            Toasty.warning(OutstockGoodsActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (lot.length() < 15){
            Toasty.warning(OutstockGoodsActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
            return;
        }
        for (String cl : cacheLot){
            if (cl.equalsIgnoreCase(lot)){
                Toasty.warning(OutstockGoodsActivity.this, "该条码已经录入", Toast.LENGTH_SHORT, true).show();
                return;
            }
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP12AA&pcmd=ScanLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("lot", lot)
                .params("nbr", nbrs)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        OutstockLv1Data data = SceoUtil.parseJson(s, OutstockLv1Data.class);
                        if (data.getStatus() == 0){
                            cacheLot.add(lot);
                            if (data.getData().size() > 0){
                                OutstockLv1Item lv1Item = data.getData().get(0);
                                for (int i = 0; i < lv0List.size(); i++){
                                    if ("1".equals(lv0List.get(i).getIsover())){
                                        continue;
                                    }
                                    adapter.collapse(i);//先关闭再打开
                                    if (lv1Item.getLd_part().equalsIgnoreCase(lv0List.get(i).getLad_part())){
                                        if (!lv0List.get(i).getQty_pick().equals(lv0List.get(i).getPointNum())){
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
                                            lv0List.get(i).addSubItem(lv1Item);
                                            adapter.notifyDataSetChanged();
                                            adapter.expand(i);

                                            recyclerView.scrollToPosition(i);

                                            lv1List.add(lv1Item);

                                            break;
                                        }else {
                                            continue;
                                        }
                                    }else {
                                        adapter.collapse(i);
                                    }
                                }
                            }else {
                                Toasty.warning(OutstockGoodsActivity.this, "条码不存在", Toast.LENGTH_SHORT, true).show();
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutstockGoodsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutstockGoodsActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutstockGoodsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutstockGoodsActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
        adapter = new OutstockGoodsAdapter(new ArrayList<MultiItemEntity>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
                OutstockLv1Item delItem = (OutstockLv1Item) adapter1.getItem(position);
                cacheLot.remove(delItem.getLd_lot());
                lv1List.remove(delItem);
                for (int i = 0; i < lv0List.size(); i++){
                    adapter.collapse(i);//先关闭
                    if (delItem.getLd_part().equalsIgnoreCase(lv0List.get(i).getLad_part())){
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
                            List<OutstockLv1Item> tmpItem = lv0List.get(i).getSubItems();
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
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP12AA&pcmd=QueryDetail")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrs)
                .params("emp", emp)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        OutstockLv0Data data = SceoUtil.parseJson(s, OutstockLv0Data.class);
                        if (data.getStatus() == 0){
                            if (data.getData().size() == 0){
                                adapter.setEmptyView(emptyView);
                            }
                            lv0List = data.getData();
                            for (int i = 0; i < lv0List.size(); i++){
                                lv0List.get(i).setNum(String.valueOf(i + 1));
                            }
                            ArrayList<MultiItemEntity> res = new ArrayList<>();
                            for (OutstockLv0Item item : lv0List){
                                res.add(item);
                            }
                            adapter.setNewData(res);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutstockGoodsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutstockGoodsActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutstockGoodsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutstockGoodsActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
        for (OutstockLv0Item item : lv0List){
            if (!"1".equals(item.getIsover())){
                if (item.getQty_pick().equals(item.getPointNum())){
                    isTrue = isTrue && true;
                }else {
                    isTrue = isTrue && false;
                }
            }
        }
        if (!isTrue){
            Toasty.warning(OutstockGoodsActivity.this, "需求量和点数量相等才可以提交", Toast.LENGTH_SHORT, true).show();
            return;
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP12AA&pcmd=Submit")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrs)
                .params("emp", emp)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(OutstockGoodsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            AppManager.getInstance().finishSingleActivityByClass(InputPickingNumActivity.class);
                            AppManager.getInstance().finishSingleActivityByClass(ChooseStorekeeperActivity.class);
                            Intent intent = new Intent(OutstockGoodsActivity.this, InputPickingNumActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(OutstockGoodsActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutstockGoodsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutstockGoodsActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutstockGoodsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutstockGoodsActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.outstockg_fab)
    void submit(){
        doSubmit();
    }
}
