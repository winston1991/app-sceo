package com.huntkey.software.sceo.ui.activity.home.personalprint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.OutputPrintData;
import com.huntkey.software.sceo.entity.PersonalPrintDetailData;
import com.huntkey.software.sceo.entity.PersonalPrintDetailItem;
import com.huntkey.software.sceo.entity.PrinterItem;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.activity.home.bluetoothprint.PrintActivity;
import com.huntkey.software.sceo.ui.adapter.home.PersonalPrintDetailAdapter;
import com.huntkey.software.sceo.ui.adapter.home.PersonalPrintSpinnerAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/4/13.
 */

public class PersonalPrintDetailActivity extends KnifeBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.ppd_toolbar)
    Toolbar toolbar;
    @BindView(R.id.ppd_add2print_btn)
    Button addBtn;
    @BindView(R.id.ppd_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.ppd_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ppd_search_view)
    MaterialSearchView searchView;

    @BindView(R.id.ppd_bottomlayout)
    LinearLayout printLayout;
    @BindView(R.id.ppd_spinner)
    Spinner spinner;

    private View emptyView;
    private View errorView;

    private LoadingUncancelable loadingDialog;

    private boolean isErr = false;
    private PersonalPrintDetailAdapter adapter;
    private ItemDragAndSwipeCallback itemDragAndSwipeCallback;
    private ItemTouchHelper itemTouchHelper;

    private String status;
    private String mid;
    private int currentPage = 1;//当前页
    private String pageSize = "20";//一次请求数据的条数

    private List<PrinterItem> printerList = new ArrayList<>();
    private String choosedPrinter;//选择的打印机

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personalprint_detail;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "打印条码明细");

        status = getIntent().getStringExtra("status");
        mid = getIntent().getStringExtra("mid");
        if ("Y".equals(status)){
            addBtn.setVisibility(View.VISIBLE);
            printLayout.setVisibility(View.GONE);
        }else {
            addBtn.setVisibility(View.GONE);
            printLayout.setVisibility(View.VISIBLE);
        }

        loadingDialog = new LoadingUncancelable(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        initEmptyAndErrorView();
        initAdapter();
        initSearch();
        getPrinter();
    }

    private void getPrinter(){
        PrinterItem btItem = new PrinterItem();
        btItem.setPsvr_no("蓝牙打印机");
        printerList.add(btItem);

        choosedPrinter = printerList.get(0).getPsvr_id();
        spinner.setAdapter(new PersonalPrintSpinnerAdapter(PersonalPrintDetailActivity.this, printerList));
    }

    private void initAdapter() {
        adapter = new PersonalPrintDetailAdapter(R.layout.item_personalprint, new ArrayList<PersonalPrintDetailItem>());
        adapter.setOnLoadMoreListener(this, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        //滑动删除+拖动
        itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        itemDragAndSwipeCallback.setSwipeMoveFlags(ItemTouchHelper.START | ItemTouchHelper.END);
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(new MyOnItemSwipeListener());
        adapter.enableDragItem(itemTouchHelper);
        adapter.setOnItemDragListener(new MyOnItemDragListener());

        recyclerView.setAdapter(adapter);

        onRefresh();
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

    private void doNetwork(final boolean isRefresh, String sn){
        if (!hasNetWork()){
            adapter.setEmptyView(errorView);
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=QueryPrintDetail")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("mid", mid)
                .params("sn", sn)
                .params("pageno", currentPage+"")
                .params("pagesize", pageSize)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        PersonalPrintDetailData data = SceoUtil.parseJson(s, PersonalPrintDetailData.class);
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
                            Toasty.error(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalPrintDetailActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        isErr = true;
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
                });
    }

    private void initSearch() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doNetwork(true, query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        doNetwork(true, "");
    }

    @Override
    public void onLoadMoreRequested() {
        ++currentPage;
        doNetwork(false, "");
    }

    @OnClick(R.id.ppd_add2print_btn)
    void addback2printLine(){
        StringBuffer tmpStr = new StringBuffer();
        for (PersonalPrintDetailItem item : adapter.getData()){
            if (item.isChecked()){
                tmpStr.append(item.getBcls_id()).append(",");
            }
        }
        if (tmpStr.length() <= 0){
            Toasty.warning(PersonalPrintDetailActivity.this, "请先选择", Toast.LENGTH_SHORT).show();
            return;
        }
        doPrintNetwork(tmpStr.toString(), "2");
    }

    /**
     * @param ids
     * @param ismain 为1是主表删除，为0非主表删除
     */
    private void delItem(String ids, String ismain){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=Del")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("ids", ids)
                .params("ismain", ismain)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalPrintDetailActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(PersonalPrintDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private void doPrintNetwork(String ids, String reprint){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=Print")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("ids", ids)
                .params("reprint", reprint)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            Intent intent = new Intent();
                            setResult(Activity.RESULT_OK, intent);
                            PersonalPrintDetailActivity.this.finish();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalPrintDetailActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(PersonalPrintDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    /**
     * 滑动删除
     */
    private class MyOnItemSwipeListener implements OnItemSwipeListener {

        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            delItem(adapter.getData().get(pos).getBcls_id(), "0");
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            canvas.drawColor(ContextCompat.getColor(PersonalPrintDetailActivity.this, R.color.colorAccent));
            Paint paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setTextSize(23);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("滑动删除", 70, 55, paint);
        }
    }

    /**
     * 拖动
     */
    private class MyOnItemDragListener implements OnItemDragListener {

        @Override
        public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {

        }

        @Override
        public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ("Y".equals(status)){
            getMenuInflater().inflate(R.menu.menu_personalprintdetail, menu);
            MenuItem item = menu.findItem(R.id.ppd_action_search);
            searchView.setMenuItem(item);
            return true;
        }else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    private void todoPrint(final String ids, final String reprint){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=Print")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("isprint", "0")//打印状态 0-未打印 1-已打印
                .params("ids", ids)
                .params("psvr", choosedPrinter)
                .params("reprint", reprint)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        OutputPrintData data = SceoUtil.parseJson(s, OutputPrintData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            if (TextUtils.isEmpty(choosedPrinter)){
                                Intent intent = new Intent(PersonalPrintDetailActivity.this, PrintActivity.class);
                                intent.putExtra("data", (Serializable) data.getData());
                                intent.putExtra("ids", ids);
                                intent.putExtra("reprint", reprint);
                                startActivity(intent);
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalPrintDetailActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalPrintDetailActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(PersonalPrintDetailActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.ppd_print_btn)
    void doPrint(){
        StringBuilder tmpStr = new StringBuilder();
        for (PersonalPrintDetailItem item : adapter.getData()){
            if (item.isChecked()){
                tmpStr.append(item.getBcls_id()).append(",");
            }
        }

        if (TextUtils.isEmpty(tmpStr)){
            Toasty.warning(PersonalPrintDetailActivity.this, "请先选择", Toast.LENGTH_SHORT, true).show();
            return;
        }

        todoPrint(tmpStr.toString(), "1");
    }
}
