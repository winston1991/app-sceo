package com.huntkey.software.sceo.ui.activity.home.instorage;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.AppManager;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.AssignedStorageData;
import com.huntkey.software.sceo.entity.AssignedStorageItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.AssignedStorageAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ControlEditText;
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
 * Created by chenl on 2017/4/24.
 */

public class AssignedStorageActivity extends KnifeBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.assistor_toolbar)
    Toolbar toolbar;
    @BindView(R.id.assistor_current)
    TextView currentStorageTv;
    @BindView(R.id.assistor_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.assistor_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.assistor_job)
    TextView nbrTv;
    @BindView(R.id.assistor_num)
    TextView refNbrTv;
    @BindView(R.id.assistor_cet)
    ControlEditText assCet;
    @BindView(R.id.assistor_fab)
    FloatingActionButton floatingActionButton;

    private View emptyView;
    private View errorView;

    private boolean isErr = false;
    private AssignedStorageAdapter adapter;
    private int currentPage = 1;//当前页
    private String pageSize = "50";//一次请求数据的条数
    private LoadingUncancelable loadingDialog;

    private String nbr;
    private String refNbr;

    private String currentStorage;//当前储位

    @Override
    protected int getContentViewId() {
        return R.layout.activity_assignedstorage;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "扫码入库");
        assCet.requestFocus();
        assCet.setOnKeyListener(new MyOnKeyListener());

        loadingDialog = new LoadingUncancelable(this);

        nbr = getIntent().getStringExtra("nbr");
        refNbr = getIntent().getStringExtra("refNbr");
        nbrTv.setText("任务：" + nbr);
        refNbrTv.setText("单号：" + refNbr);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
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

        initEmptyAndErrorView();
        initAdapter();
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                String tmpStr = assCet.getText().toString().replaceAll("\\s*", "");
                if (tmpStr.length() < 10){//判断扫描的是储位还是箱号 暂定<10为储位
                    currentStorage = tmpStr;
                    currentStorageTv.setText("当前储位:" + currentStorage);
                }else {
                    if (currentStorage == null){
                        Toasty.warning(AssignedStorageActivity.this, "请先扫描储位", Toast.LENGTH_SHORT, true).show();
                    }else {
                        try {
                            String[] tmpArr = tmpStr.split(",");
                            for (AssignedStorageItem item : adapter.getData()){
                                if (tmpArr[1].equalsIgnoreCase(item.getTskp_sn().replaceAll("\\s*", ""))){
                                    item.setTskp_ref(currentStorage);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }catch (Exception e){
                            Toasty.warning(AssignedStorageActivity.this, "请扫描正确的箱号", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                }
                assCet.setText("");
            }
            return false;
        }
    }

    private void initAdapter() {
        adapter = new AssignedStorageAdapter(R.layout.item_assignedstorage, new ArrayList<AssignedStorageItem>());
        adapter.setOnLoadMoreListener(this, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public boolean onItemChildClick(BaseQuickAdapter adapter1, View view, int position) {
                delPick(adapter.getData().get(position).getTskp_id(), position);
                return false;
            }
        });

        onRefresh();
    }

    private void delPick(String pid, final int position){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP06AA&pcmd=DelPick")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("pid", pid)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            adapter.remove(position);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AssignedStorageActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AssignedStorageActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AssignedStorageActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(AssignedStorageActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private void doNetwork(final boolean isRefresh){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP06AA&pcmd=QueryLot")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbr)
                .params("pageno", currentPage+"")
                .params("pagesize", pageSize)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        AssignedStorageData data = SceoUtil.parseJson(s, AssignedStorageData.class);
                        if (data.getStatus() == 0){
                            if (isRefresh){
                                adapter.setNewData(data.getData().getResults());

                                if (data.getData().getResults().size() == 0){
                                    adapter.setEmptyView(emptyView);
                                }
                            }else {
                                adapter.addData(data.getData().getResults());
                            }

                            int totalRow = data.getData().getPage().getRowcount();
                            if (adapter.getData().size() == totalRow){
                                adapter.loadMoreEnd(true);
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AssignedStorageActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AssignedStorageActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AssignedStorageActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        isErr = true;
                    }

                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        if (isRefresh){
                            refreshLayout.setRefreshing(true);
                            adapter.setEnableLoadMore(false);
                        }else {
                            refreshLayout.setEnabled(false);
                            adapter.setEnableLoadMore(true);
                        }
                    }

                    @Override
                    public void onAfter(String s, Exception e) {
                        super.onAfter(s, e);
                        if (isRefresh){
                            refreshLayout.setRefreshing(false);
                            adapter.setEnableLoadMore(true);
                        }else {
                            if (!isErr){
                                adapter.loadMoreComplete();
                            }else {
                                adapter.loadMoreFail();
                            }
                            refreshLayout.setEnabled(true);
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        doNetwork(true);
        refreshLayout.setRefreshing(false);
        adapter.setEnableLoadMore(true);
    }

    @Override
    public void onLoadMoreRequested() {
        ++currentPage;
        doNetwork(false);
        if (!isErr){
            adapter.loadMoreComplete();
        }else {
            adapter.loadMoreFail();
        }
        refreshLayout.setEnabled(true);
    }

    @OnClick(R.id.assistor_fab)
    void clickFab(){
        for (AssignedStorageItem item : adapter.getData()){
            if (item.getTskp_ref().length() < 1){
                Toasty.warning(AssignedStorageActivity.this, "所有的箱号都分配储位才能提交", Toast.LENGTH_SHORT, true).show();
                return;
            }
        }

        RequestParams params = new RequestParams();
        params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(this));
        params.addBodyParameter("nbr", nbr);
        for (int i = 0; i < adapter.getData().size(); i++) {
            params.addBodyParameter("pid", adapter.getData().get(i).getTskp_id());
            params.addBodyParameter("ref", adapter.getData().get(i).getTskp_ref());
        }
        HttpUtils http = new HttpUtils();
        http.configRequestRetryCount(0);
        http.configTimeout(15 * 1000);
        http.configSoTimeout(60 * 60 * 1000);
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.POST,
                Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP06AA&pcmd=Submit",
                params,
                new RequestCallBack<String>() {
                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        loadingDialog.dismiss();
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
                        if (data.getStatus() == 0){
                            AppManager.getInstance().finishSingleActivityByClass(InputTaskNumActivity.class);
                            AppManager.getInstance().finishSingleActivityByClass(WarehousingActivity.class);
                            Intent intent = new Intent(AssignedStorageActivity.this, InputTaskNumActivity.class);
                            startActivity(intent);
                            AppManager.getInstance().finishSingleActivityByClass(AssignedStorageActivity.class);
                        }else if (data.getStatus() == 88) {
                            Toasty.error(AssignedStorageActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(AssignedStorageActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(AssignedStorageActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {
                        loadingDialog.dismiss();
                        Toasty.error(AssignedStorageActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        loadingDialog.show();
                    }
                });
    }
}
