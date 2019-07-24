package com.huntkey.software.sceo.ui.activity.home.outstorage;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.OutputScanData;
import com.huntkey.software.sceo.entity.OutputScanItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.OutputScanAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
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
 * Created by chenl on 2017/4/28.
 */

public class OutputScanActivity extends KnifeBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.ops_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ops_current)
    TextView currentStorage;
    @BindView(R.id.ops_nbr)
    TextView nbrTv;
    @BindView(R.id.ops_refnbr)
    TextView refnbrTv;
    @BindView(R.id.ops_cet)
    ControlEditText opsCet;
    @BindView(R.id.ops_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.ops_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ops_float_menu)
    FloatingActionMenu floatingActionMenu;
    @BindView(R.id.ops_showall_fab)
    FloatingActionButton showAllFab;
    @BindView(R.id.ops_hide_fab)
    FloatingActionButton hideFab;

    private String isall = "0";//0-不显示全部 1-显示全部

    private String nbr;
    private String refNbr;
    private String ref;

    private View emptyView;
    private View errorView;
    private OutputScanAdapter adapter;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_outputscan;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "出库扫描作业");

        nbr = getIntent().getStringExtra("nbr");
        refNbr = getIntent().getStringExtra("refnbr");
        ref = getIntent().getStringExtra("ref");
        nbrTv.setText("任务：" + nbr);
        refnbrTv.setText(refNbr);
        currentStorage.setText("储位：" + ref);

        loadingDialog = new LoadingUncancelable(this);

        opsCet.requestFocus();
        opsCet.setOnKeyListener(new MyOnKeyListener());

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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

        floatingActionMenu.removeMenuButton(hideFab);

        initEmptyAndErrorView();
        initAdapter();
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                doScanLot(opsCet.getText().toString().replaceAll("\\s*", ""));
                opsCet.setText("");
            }
            return false;
        }
    }

    private void doScanLot(final String lot){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=ScanLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbr)
                .params("ref", ref)
                .params("lot", lot)
                .params("pid", "0")//上级捡料ID（整拿时传0）
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getMessage().contains("会话无效")){
                            Toasty.error(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputScanActivity.this);
                            SceoApplication.getInstance().exit();
                            return;
                        }
                        if (data.getStatus() > 0){
                            Intent intent = new Intent(OutputScanActivity.this, OutputScanSecondActivity.class);
                            intent.putExtra("nbr", nbr);
                            intent.putExtra("refnbr", refNbr);
                            intent.putExtra("ref", ref);
                            intent.putExtra("pid", String.valueOf(data.getStatus()));
                            intent.putExtra("fatherLot", lot);
                            startActivity(intent);
                        }else if(data.getStatus() == 0){
                            Toasty.success(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            onRefresh();
                        } else {
                            Toasty.error(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputScanActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    private void initAdapter() {
        adapter = new OutputScanAdapter(R.layout.item_outputscan, new ArrayList<OutputScanItem>());
        recyclerView.setAdapter(adapter);
        
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
                OutputScanItem item = (OutputScanItem) adapter1.getItem(position);
                delItem(item, position);
                return false;
            }
        });
    }

    private void delItem(OutputScanItem item, final int position) {
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=Del")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbr)
                .params("pid", item.getTskp_id())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            adapter.remove(position);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputScanActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputScanActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private void initEmptyAndErrorView() {
        emptyView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup)recyclerView.getParent(), false);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
        errorView = getLayoutInflater().inflate(R.layout.error_view, (ViewGroup)recyclerView.getParent(), false);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
    }

    private void doNetwork(){
        if (!hasNetWork()) {
            adapter.setEmptyView(errorView);
            return;
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbr)
                .params("ref", ref)
                .params("isall", isall)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        OutputScanData data = SceoUtil.parseJson(s, OutputScanData.class);
                        if (data.getStatus() == 0){
                            List<OutputScanItem> list = data.getData();
                            adapter.setNewData(list);

                            if (list.size() == 0){
                                adapter.setEmptyView(emptyView);
                            }else {
                                for (int i = 0; i < list.size(); i++){
                                    list.get(i).setNum(String.valueOf(i + 1));
                                }
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputScanActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        refreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onRefresh() {
        doNetwork();
    }

    @OnClick(R.id.ops_showall_fab)
    void showAll(){
        floatingActionMenu.close(true);
        isall = "1";
        floatingActionMenu.addMenuButton(hideFab);
        floatingActionMenu.removeMenuButton(showAllFab);
        onRefresh();
    }

    @OnClick(R.id.ops_hide_fab)
    void notShowAll(){
        floatingActionMenu.close(true);
        isall = "0";
        floatingActionMenu.addMenuButton(showAllFab);
        floatingActionMenu.removeMenuButton(hideFab);
        onRefresh();
    }

    @OnClick(R.id.ops_unusual_fab)
    void submitUnusual(){
        floatingActionMenu.close(true);
        final EditText editText = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提交异常");
        builder.setMessage("请输入异常项序号");
        builder.setView(editText);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (OutputScanItem item : adapter.getData()){
                    if (editText.getText().toString().trim().equals(item.getNum())){
                        submitUnusualNetwork(item.getTskp_id());
                    }
                }
            }
        });
        builder.show();
    }

    private void submitUnusualNetwork(String pid){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=FrzLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbr)
                .params("pid", pid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            onRefresh();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputScanActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputScanActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
