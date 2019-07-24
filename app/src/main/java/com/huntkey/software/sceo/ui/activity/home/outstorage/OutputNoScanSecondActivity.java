package com.huntkey.software.sceo.ui.activity.home.outstorage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.OutputNoScanData;
import com.huntkey.software.sceo.entity.OutputNoScanItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.OutputNoScanSecondAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/12/19.
 */

public class OutputNoScanSecondActivity extends KnifeBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.opnss_toolbar)
    Toolbar toolbar;
    @BindView(R.id.opnss_nbr)
    TextView nbrTv;
    @BindView(R.id.opnss_refnbr)
    TextView refnbrTv;
    @BindView(R.id.opnss_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.opnss_recyclerView)
    RecyclerView recyclerView;

    private String pid;
    private String nbr;
    private String refNbr;
    private int position;

    private View emptyView;
    private View errorView;

    private LoadingUncancelable loadingDialog;
    private OutputNoScanSecondAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_outputnoscan_sec;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "出库作业");

        pid = getIntent().getStringExtra("pid");
        nbr = getIntent().getStringExtra("nbr");
        refNbr = getIntent().getStringExtra("refnbr");
        position = getIntent().getIntExtra("position", -1);
        nbrTv.setText("任务：" + nbr);
        refnbrTv.setText(refNbr);

        loadingDialog = new LoadingUncancelable(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initEmptyAndErrorView();
        initAdapter();

        onRefresh();
    }

    private void initAdapter() {
        adapter = new OutputNoScanSecondAdapter(R.layout.item_outputnoscan_second, new ArrayList<OutputNoScanItem>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
                OutputNoScanItem item = (OutputNoScanItem) adapter1.getItem(position);
                delItem(item, position);
                return false;
            }
        });
    }

    private void delItem(OutputNoScanItem item, final int position) {
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=DelN")
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
                            Toasty.success(OutputNoScanSecondActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            adapter.remove(position);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputNoScanSecondActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputNoScanSecondActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputNoScanSecondActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputNoScanSecondActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private void doNetwork() {
        if (!hasNetWork()) {
            adapter.setEmptyView(errorView);
            return;
        }
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=QueryDetailN")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("pid", pid)
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
                            Toasty.error(OutputNoScanSecondActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputNoScanSecondActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputNoScanSecondActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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

    @Override
    public void onBackPressed() {
        boolean isTrue = true;
        for (OutputNoScanItem item : adapter.getData()) {
            if (!item.isFlag()) {
                isTrue = false;
            }
        }
        Intent intent = new Intent();
        intent.putExtra("flag", isTrue);
        intent.putExtra("position", position);
        setResult(2, intent);
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
