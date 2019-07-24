package com.huntkey.software.sceo.ui.activity.home.storageadjust;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.AdjustOrderItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.AdjustmentAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by chenl on 2017/5/8.
 */

public class AdjustmentActivity extends KnifeBaseActivity {

    @BindView(R.id.adm_toolbar)
    Toolbar toolbar;
    @BindView(R.id.adm_current)
    TextView currentTv;
    @BindView(R.id.adm_cet)
    ControlEditText admCet;
    @BindView(R.id.adm_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.adm_fab)
    FloatingActionButton floatingActionButton;

    private List<AdjustOrderItem> data = new ArrayList<>();
    private AdjustmentAdapter adapter;
    private String currentStorage;//当前储位

    private List<String> lotList = new ArrayList<>();

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_adjustment;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "储位调整");

        admCet.requestFocus();
        admCet.setOnKeyListener(new MyOnKeyListener());

        loadingDialog = new LoadingUncancelable(this);
        data = (List<AdjustOrderItem>) getIntent().getSerializableExtra("data");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();

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
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                String tmpStr = admCet.getText().toString().replaceAll("\\s*", "");

                if (tmpStr.length() < 10){//判断扫描的是储位还是箱号 暂定<10为储位
                    currentStorage = tmpStr;
                    currentTv.setText("当前储位:" + currentStorage);
                }else {
                    if (currentStorage == null){
                        Toasty.warning(AdjustmentActivity.this, "请先扫描储位", Toast.LENGTH_SHORT, true).show();
                    }else {
                        String[] str=tmpStr.split(",");
                        if (str.length > 3) {
                            tmpStr="";
                            tmpStr=str[0]+","+str[1]+","+str[2];
                        }

                        List<AdjustOrderItem> item = scanLot(tmpStr);
                        if (item != null){
                            adapter.notifyDataSetChanged();
                        }else {
                            Toasty.warning(AdjustmentActivity.this, "请扫描正确的箱号条码", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                }
                admCet.setText("");
            }
            return false;
        }
    }

    @Nullable
    private List<AdjustOrderItem> scanLot(String str){
        if (!str.contains(",")){
            return null;
        }
        if (str.length() < 15){
            return null;
        }
        for (AdjustOrderItem item : data){
            if (str.equalsIgnoreCase(item.getLot_sn())){
                item.setNewRef(currentStorage);
            }
        }
        return data;
    }

    private void initAdapter() {
        adapter = new AdjustmentAdapter(R.layout.item_adjustment, new ArrayList<AdjustOrderItem>());
        recyclerView.setAdapter(adapter);

        adapter.addData(data);
    }

    @OnClick(R.id.adm_fab)
    void clickFab(){
        RequestParams params = new RequestParams();
        params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(this));
        for (AdjustOrderItem item : adapter.getData()){
            params.addBodyParameter("sn", item.getLd_lot());
            params.addBodyParameter("part", item.getLd_part());
            params.addBodyParameter("loc", item.getLd_loc());
            params.addBodyParameter("ref", item.getNewRef());
            if (TextUtils.isEmpty(item.getNewRef())){
                Toasty.warning(AdjustmentActivity.this, "全部分配储位才可以提交", Toast.LENGTH_SHORT, true).show();
                return;
            }
            lotList.add(item.getLd_lot());
        }
        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.POST,
                Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP08AA&pcmd=Submit",
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        loadingDialog.dismiss();
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
                        if (data.getStatus() == 0){
//                            StringBuffer lotBuf = new StringBuffer();
//                            for (String s : lotList){
//                                lotBuf.append(s).append(",");
//                            }
//                            Intent intent = new Intent(AdjustmentActivity.this, AdjustPrintActivity.class);
//                            intent.putExtra("lots", lotBuf.toString());
//                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(AdjustOrderActivity.class);
                            Intent intent = new Intent(AdjustmentActivity.this, AdjustOrderActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(AdjustmentActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AdjustmentActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AdjustmentActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AdjustmentActivity.this, responseInfo.result, Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        loadingDialog.dismiss();
                        Toasty.error(AdjustmentActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingDialog.show();
                    }
                });
    }
}
