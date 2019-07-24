package com.huntkey.software.sceo.ui.activity.home.outstorage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
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
import com.huntkey.software.sceo.entity.StorageLocationData;
import com.huntkey.software.sceo.entity.StorageLocationItem;
import com.huntkey.software.sceo.others.NoDoubleClickListener;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.StorageLocationAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/4/26.
 */

public class StorageLocationActivity extends KnifeBaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.sl_toolbar)
    Toolbar toolbar;
    @BindView(R.id.sl_job)
    TextView jobTv;
    @BindView(R.id.sl_num)
    TextView numTv;
    @BindView(R.id.sl_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.sl_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.sl_float_menu)
    FloatingActionMenu floatingActionMenu;
    @BindView(R.id.sl_submit_fab)
    FloatingActionButton submitFab;
    @BindView(R.id.sl_rg)
    RadioGroup radioGroup;

    private String nbrStr;
    private String refNbrStr;
    private String isall = "2";//2-查询全部 0-待检 1-已检
    private String isscan;

    private View emptyView;
    private View errorView;
    private StorageLocationAdapter adapter;

    private LoadingUncancelable loadingDialog;
    private int REQUEST_CODE = 0x11;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_storagelocation;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "");

        nbrStr = getIntent().getStringExtra("nbr");
        isscan = getIntent().getStringExtra("isscan");
        jobTv.setText("任务：" + nbrStr);
        if ("0".equals(isscan)){
            radioGroup.setVisibility(View.GONE);
            toolbar.setTitle("出库作业");
        }

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        loadingDialog = new LoadingUncancelable(this);

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

        submitFab.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View view) {
                doSubmit();
            }
        });
        if ("0".equals(isscan)){
            onRefresh();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!"0".equals(isscan)){
            onRefresh();
        }
    }

    private void initAdapter() {
        adapter = new StorageLocationAdapter(R.layout.item_storagelocation, new ArrayList<StorageLocationItem>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                if ("0".equals(isscan)){
                    Intent intent = new Intent(StorageLocationActivity.this, OutputNoScanActivity.class);
                    intent.putExtra("nbr", nbrStr);
                    intent.putExtra("refnbr", refNbrStr);
                    intent.putExtra("ref", adapter.getData().get(position).getTskp_ref());
                    intent.putExtra("position", position);
                    startActivityForResult(intent, REQUEST_CODE);
                } else {
                    Intent intent = new Intent(StorageLocationActivity.this, OutputScanActivity.class);
                    intent.putExtra("nbr", nbrStr);
                    intent.putExtra("refnbr", refNbrStr);
                    intent.putExtra("ref", adapter.getData().get(position).getTskp_ref());
                    startActivity(intent);
                }
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

    @Override
    public void onRefresh() {
        doNetwork();
    }

    private void doNetwork(){
        if (!hasNetWork()) {
            adapter.setEmptyView(errorView);
            return;
        }

        String url;
        if ("0".equals(isscan)){
            url = "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=QueryMainN";
            isall = "0";
        }else {
            url = "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=QueryMain";
        }
        OkGo.post(Conf.SERVICE_URL + url)
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrStr)
                .params("isall", isall)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        StorageLocationData data = SceoUtil.parseJson(s, StorageLocationData.class);
                        if (data.getStatus() == 0){
                            adapter.setNewData(data.getData());

                            if (data.getData().size() == 0){
                                adapter.setEmptyView(emptyView);
                            }else {
                                refNbrStr = data.getData().get(0).getTskm_ref_nbr();
                                numTv.setText(refNbrStr);
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(StorageLocationActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(StorageLocationActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(StorageLocationActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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

    /**
     * 待检
     */
    @OnClick(R.id.sl_rb_todo)
    void getTodos(){
        isall = "0";
        onRefresh();
    }

    /**
     * 已检
     */
    @OnClick(R.id.sl_rb_has)
    void getHass(){
        isall = "1";
        onRefresh();
    }

    /**
     * 全部
     */
    @OnClick(R.id.sl_rb_all)
    void getAlls(){
        isall = "2";
        onRefresh();
    }

    /**
     * 打印
     */
    @OnClick(R.id.sl_print_fab)
    void gotoPrint(){
        floatingActionMenu.close(true);
        Intent intent = new Intent(this, OutputPrintActivity.class);
        intent.putExtra("nbr", nbrStr);
        intent.putExtra("refnbr", refNbrStr);
        startActivity(intent);
    }

    /**
     * 提交
     */
    void doSubmit(){
        floatingActionMenu.close(true);
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=Submit")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbrStr)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(StorageLocationActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            StorageLocationActivity.this.finish();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(StorageLocationActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(StorageLocationActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.info(StorageLocationActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(StorageLocationActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == 3) {
            boolean flag = data.getBooleanExtra("flag", false);
            int position = data.getIntExtra("position", 0);
            if (flag){
                adapter.getItem(position).setIsok("1");
            }
            adapter.notifyItemChanged(position);
        }
    }
}
