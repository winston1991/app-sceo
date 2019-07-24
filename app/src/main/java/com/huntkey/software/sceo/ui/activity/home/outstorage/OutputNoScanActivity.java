package com.huntkey.software.sceo.ui.activity.home.outstorage;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.OutputNoScanData;
import com.huntkey.software.sceo.entity.OutputNoScanItem;
import com.huntkey.software.sceo.entity.OutputScanData;
import com.huntkey.software.sceo.entity.OutputScanItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.OutputNoScanAdapter;
import com.huntkey.software.sceo.ui.adapter.home.OutputScanAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/12/19.
 */

public class OutputNoScanActivity extends KnifeBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.opos_toolbar)
    Toolbar toolbar;
    @BindView(R.id.opos_nbr)
    TextView nbrTv;
    @BindView(R.id.opos_refnbr)
    TextView refnbrTv;
    @BindView(R.id.opos_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.opos_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.opos_float_menu)
    FloatingActionMenu floatingActionMenu;

    private String nbr;
    private String refNbr;
    private String ref;

    private View emptyView;
    private View errorView;
    private OutputNoScanAdapter adapter;

    private LoadingUncancelable loadingDialog;
    private int REQUEST_CODE = 0x11;
    private int position;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_outputnoscan;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "出库作业");

        nbr = getIntent().getStringExtra("nbr");
        refNbr = getIntent().getStringExtra("refnbr");
        ref = getIntent().getStringExtra("ref");
        position = getIntent().getIntExtra("position", -1);
        nbrTv.setText("任务：" + nbr);
        refnbrTv.setText(refNbr);

        loadingDialog = new LoadingUncancelable(this);

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

        initEmptyAndErrorView();
        initAdapter();

        onRefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        onRefresh();
    }

    private void initAdapter() {
        adapter = new OutputNoScanAdapter(R.layout.item_outputnoscan, new ArrayList<OutputNoScanItem>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                Intent intent = new Intent(OutputNoScanActivity.this, OutputNoScanSecondActivity.class);
                intent.putExtra("pid", adapter.getData().get(position).getTskp_id());
                intent.putExtra("nbr", nbr);
                intent.putExtra("refnbr", refNbr);
                intent.putExtra("position", position);
                startActivityForResult(intent, REQUEST_CODE);
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
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=QueryN")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbr)
                .params("ref", ref)
                .params("isall", "0")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        OutputNoScanData data = SceoUtil.parseJson(s, OutputNoScanData.class);
                        if (data.getStatus() == 0){
                            List<OutputNoScanItem> list = data.getData();
                            adapter.setNewData(list);

                            if (list.size() == 0){
                                adapter.setEmptyView(emptyView);
                            }else {
                                for (int i = 0; i < list.size(); i++){
                                    list.get(i).setNum(String.valueOf(i + 1));
                                }
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputNoScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputNoScanActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputNoScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == 2) {
            boolean flag = data.getBooleanExtra("flag", false);
            int position = data.getIntExtra("position", 0);
            adapter.getItem(position).setFlag(flag);
            adapter.notifyItemChanged(position);
        }
    }

    @Override
    public void onRefresh() {
        doNetwork();
    }

    @OnClick(R.id.opos_unusual_fab)
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
                for (OutputNoScanItem item : adapter.getData()){
                    if (editText.getText().toString().trim().equals(item.getNum())){
                        submitUnusualNetwork(item.getTskp_id());
                    }
                }
            }
        });
        builder.show();
    }

    private void submitUnusualNetwork(String pid){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=FrzLotN")
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
                            Toasty.success(OutputNoScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            onRefresh();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputNoScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputNoScanActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputNoScanActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputNoScanActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
    public void onBackPressed() {
        boolean isTrue = true;
        for (OutputNoScanItem item : adapter.getData()){
            if (!item.isFlag()){
                isTrue = false;
            }
        }
        Intent intent = new Intent();
        intent.putExtra("flag", isTrue);
        intent.putExtra("position", position);
        setResult(3, intent);
        super.onBackPressed();
    }
}
