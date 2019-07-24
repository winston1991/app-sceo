package com.huntkey.software.sceo.ui.activity.home.instorage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.huntkey.software.sceo.entity.WarehosLv0Data;
import com.huntkey.software.sceo.entity.WarehosLv0Item;
import com.huntkey.software.sceo.entity.WarehosLv1Item;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.WarehousingAdapter;
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
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/5/17.
 */

public class DirectWarehousingActivity extends KnifeBaseActivity {

    @BindView(R.id.dirwarhos_toolbar)
    Toolbar toolbar;
    @BindView(R.id.dirwarhos_current)
    TextView currentTv;
    @BindView(R.id.dirwarhos_job)
    TextView jobTv;
    @BindView(R.id.dirwarhos_num)
    TextView numTv;
    @BindView(R.id.dirwarhos_cet)
    ControlEditText dwhCet;
    @BindView(R.id.dirwarhos_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.dirwarhos_fab)
    FloatingActionButton floatingActionButton;

    private View emptyView;
    private View errorView;
    private LoadingUncancelable loadingDialog;

    private String nbr;
    private String refNbr;
    private String currentStorage;
    private WarehousingAdapter adapter;

    private List<WarehosLv0Item> lv0List = new ArrayList<>();
    private List<WarehosLv1Item> lv1List = new ArrayList<>();
    private List<String> cacheLot = new ArrayList<>();//记录扫描的条码

    @Override
    protected int getContentViewId() {
        return R.layout.activity_directwarehousing;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        toolbar.setTitle("直接入库");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(DirectWarehousingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                        .setTitleText("是否退出该页面？")
                        .setCancelText("否")
                        .setConfirmText("是")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {

                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                onBackPressed();
                            }
                        })
                        .show();
            }
        });

        dwhCet.requestFocus();
        dwhCet.setOnKeyListener(new MyOnKeyListener());

        nbr = getIntent().getStringExtra("nbr");
        jobTv.setText("任务：" + nbr);

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            new SweetAlertDialog(DirectWarehousingActivity.this, SweetAlertDialog.NORMAL_TYPE)
                    .setTitleText("是否退出该页面？")
                    .setCancelText("否")
                    .setConfirmText("是")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {

                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                            onBackPressed();
                        }
                    })
                    .show();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                cutStr(dwhCet.getText().toString().replaceAll("\\s*", ""));
                dwhCet.setText("");
            }
            return false;
        }
    }

    private void cutStr(String lot){
        if (lot.length() < 10){//判断扫描的是储位还是箱号 暂定<10为储位
            currentStorage = lot;
            currentTv.setText("当前储位:" + currentStorage);
        }else {
            if (TextUtils.isEmpty(currentStorage)){
                Toasty.warning(DirectWarehousingActivity.this, "请先扫描储位", Toast.LENGTH_SHORT, true).show();
                return;
            }else {
                if (!lot.contains(",")){
                    Toasty.warning(DirectWarehousingActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                if (lot.length() < 15){
                    Toasty.warning(DirectWarehousingActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                for (String cl : cacheLot){
                    if (cl.equalsIgnoreCase(lot)){
                        Toasty.warning(DirectWarehousingActivity.this, "该条码已经录入", Toast.LENGTH_SHORT, true).show();
                        return;
                    }
                }
                try{
                    WarehosLv1Item lv1Item = new WarehosLv1Item();
                    String[] tmp = lot.split(",");
                    lv1Item.setLot(lot);
                    lv1Item.setPart(tmp[0]);
                    lv1Item.setSn(tmp[1]);
                    lv1Item.setCount(tmp[2]);
                    lv1Item.setStorage(currentStorage);
                    for (int i = 0; i < lv0List.size(); i++){
                        adapter.collapse(i);//先关闭再打开
                        if (tmp[0].equalsIgnoreCase(lv0List.get(i).getTskd_part())){
                            if (!lv0List.get(i).getTskd_qty().equals(lv0List.get(i).getPointNum())){
                                cacheLot.add(lot);

                                lv1Item.setId(lv0List.get(i).getTskd_id());
                                lv1Item.setLine(lv0List.get(i).getTskd_line());
                                lv1Item.setLid(lv0List.get(i).getTskl_id());
                                lv1Item.setTskd_qty(lv0List.get(i).getTskd_qty());
                                lv1Item.setTskl_lot_ref(lv0List.get(i).getTskl_lot_ref());

                                //计算no
                                if (lv0List.get(i).hasSubItem()){
                                    lv1Item.setNo(lv0List.get(i).getTskd_line() + "." + (lv0List.get(i).getSubItems().size() + 1));
                                }else {
                                    lv1Item.setNo(lv0List.get(i).getTskd_line() + ".1");
                                }

//                                float f = Float.parseFloat(lv0List.get(i).getPointNum()) + Float.parseFloat(tmp[2]);
//                                if(Math.abs(f-(int)f) < 0.000001){
//                                    lv0List.get(i).setPointNum(String.valueOf((int)Float.parseFloat(lv0List.get(i).getPointNum()) + (int)Float.parseFloat(tmp[2])));
//                                }else {
//                                    lv0List.get(i).setPointNum(String.valueOf(Float.parseFloat(lv0List.get(i).getPointNum()) + Float.parseFloat(tmp[2])));
                                    BigDecimal bd1 = new BigDecimal(lv0List.get(i).getPointNum());
                                    BigDecimal bd2 = new BigDecimal(tmp[2]);
                                    lv0List.get(i).setPointNum(bd1.add(bd2).stripTrailingZeros().toPlainString());
//                                }
                                lv0List.get(i).addSubItem(lv1Item);
                                adapter.notifyItemChanged(i);
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
                }catch (Exception e){
                    Toasty.warning(DirectWarehousingActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
                    return;
                }
            }
        }
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

    private void initAdapter() {
        adapter = new WarehousingAdapter(new ArrayList<MultiItemEntity>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
                WarehosLv1Item delItem = (WarehosLv1Item)adapter1.getItem(position);
                cacheLot.remove(delItem.getLot());
                lv1List.remove(delItem);
                for (int i = 0; i < lv0List.size(); i++){
                    adapter.collapse(i);//先关闭
                    if (delItem.getPart().equalsIgnoreCase(lv0List.get(i).getTskd_part()) && delItem.getLine().equals(lv0List.get(i).getTskd_line())){
                        lv0List.get(i).removeSubItem(delItem);
//                        float f = Float.parseFloat(lv0List.get(i).getPointNum()) - Float.parseFloat(delItem.getCount());
//                        if(Math.abs(f-(int)f) < 0.000001){
//                            lv0List.get(i).setPointNum(String.valueOf((int)Float.parseFloat(lv0List.get(i).getPointNum()) - (int)Float.parseFloat(delItem.getCount())));
//                        }else {
//                            lv0List.get(i).setPointNum(String.valueOf(Float.parseFloat(lv0List.get(i).getPointNum()) - Float.parseFloat(delItem.getCount())));
                            BigDecimal bd1 = new BigDecimal(lv0List.get(i).getPointNum());
                            BigDecimal bd2 = new BigDecimal(delItem.getCount());
                            lv0List.get(i).setPointNum(bd1.subtract(bd2).stripTrailingZeros().toPlainString());
//                        }

                        //计算no
                        if (lv0List.get(i).hasSubItem()){
                            List<WarehosLv1Item> tmpItem = lv0List.get(i).getSubItems();
                            for (int j = 0; j < tmpItem.size(); j++){
                                tmpItem.get(j).setNo(tmpItem.get(j).getLine() + "." + (j + 1));
                            }
                        }

                        adapter.notifyItemChanged(i);
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
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP06AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbr)
                .params("pageno", "1")
                .params("pagesize", String.valueOf(Integer.MAX_VALUE))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        WarehosLv0Data data = SceoUtil.parseJson(s, WarehosLv0Data.class);
                        if (data.getStatus() == 0){
                            if (data.getData().getResults().size() == 0){
                                adapter.setEmptyView(emptyView);
                            }else {
                                refNbr = data.getData().getResults().get(0).getTskm_ref_nbr();
                                numTv.setText("单号：" + refNbr);
                            }
                            lv0List = data.getData().getResults();
                            ArrayList<MultiItemEntity> res = new ArrayList<>();
                            for (WarehosLv0Item item : lv0List){
                                res.add(item);
                            }
                            adapter.setNewData(res);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(DirectWarehousingActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(DirectWarehousingActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(DirectWarehousingActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(DirectWarehousingActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private void doDirect(){
//        boolean isTrue = true;
//        for (WarehosLv0Item item : lv0List){
//            if (item.getTskd_qty().equals(item.getPointNum())){
//                isTrue = isTrue && true;
//            }else {
//                isTrue = isTrue && false;
//            }
//        }
//        if (!isTrue){
//            Toasty.warning(DirectWarehousingActivity.this, "所有货物的任务量和点数量相等才可以提交", Toast.LENGTH_SHORT, true).show();
//            return;
//        }
        try {
            boolean isTrue = true;
            StringBuffer sb = new StringBuffer("");
            for (WarehosLv0Item item : lv0List){
                if (Integer.parseInt(item.getPointNum()) > Integer.parseInt(item.getTskd_qty())){
                    sb.append(item.getTskd_part()).append(" ");
                    isTrue = isTrue && false;
                } else {
                    isTrue = isTrue && true;
                }
            }
            if (!isTrue) {
                Toasty.warning(DirectWarehousingActivity.this, sb.toString() + "点数量不能大于任务量", Toast.LENGTH_SHORT, true).show();
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        RequestParams params = new RequestParams();
        params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(this));
        params.addBodyParameter("nbr", nbr);
        for (int i = 0; i < lv1List.size(); i++) {
            params.addBodyParameter("pid", lv1List.get(i).getId());
            params.addBodyParameter("lot", lv1List.get(i).getLot());
            params.addBodyParameter("no", lv1List.get(i).getNo());
            params.addBodyParameter("ref", lv1List.get(i).getStorage());
            params.addBodyParameter("lid", lv1List.get(i).getLid());
            params.addBodyParameter("lqty", lv1List.get(i).getTskd_qty());
            params.addBodyParameter("lrf", lv1List.get(i).getTskl_lot_ref());
        }
        HttpUtils http = new HttpUtils();
        http.configRequestRetryCount(0);
        http.configTimeout(15 * 1000);
        http.configSoTimeout(60 * 60 * 1000);
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.POST,
                Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP06AA&pcmd=SubmitTask",
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        loadingDialog.dismiss();
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
                        if (data.getStatus() == 0){
                            AppManager.getInstance().finishSingleActivityByClass(InputTaskNumActivity.class);
                            Intent intent = new Intent(DirectWarehousingActivity.this, InputTaskNumActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(DirectWarehousingActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(DirectWarehousingActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(DirectWarehousingActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(DirectWarehousingActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        loadingDialog.dismiss();
                        Toasty.error(DirectWarehousingActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingDialog.show();
                    }
                });
    }

    @OnClick(R.id.dirwarhos_fab)
    void direct(){
        doDirect();
    }
}
