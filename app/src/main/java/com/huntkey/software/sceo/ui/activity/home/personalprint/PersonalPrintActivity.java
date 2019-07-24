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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.huntkey.software.sceo.entity.PersonalPrintData;
import com.huntkey.software.sceo.entity.PersonalPrintItem;
import com.huntkey.software.sceo.entity.PersonalPrintToogleEvent;
import com.huntkey.software.sceo.entity.PrinterData;
import com.huntkey.software.sceo.entity.PrinterItem;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.activity.home.bluetoothprint.PrintActivity;
import com.huntkey.software.sceo.ui.adapter.home.PersonalPrintAdapter;
import com.huntkey.software.sceo.ui.adapter.home.PersonalPrintSpinnerAdapter;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/3/29.
 */

public class PersonalPrintActivity extends KnifeBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.pp_toolbar)
    Toolbar toolbar;
    @BindView(R.id.pp_rb_no)
    RadioButton noRadioBtn;
    @BindView(R.id.pp_rb_yes)
    RadioButton yesRadioBtn;
    @BindView(R.id.pp_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.pp_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.pp_spinner)
    Spinner spinner;
    @BindView(R.id.pp_checkall)
    TextView checkTv;
    @BindView(R.id.pp_print_btn)
    Button printBtn;

    private View emptyView;
    private View errorView;
    private LoadingUncancelable loadingDialog;

    private List<PrinterItem> printerList = new ArrayList<>();

    private boolean ischeckAll = false;
    private boolean isErr = false;
    private PersonalPrintAdapter adapter;
    private ItemDragAndSwipeCallback itemDragAndSwipeCallback;
    private ItemTouchHelper itemTouchHelper;

    private int currentPage = 1;//当前页
    private String pageSize = "20";//一次请求数据的条数
    private String isPrinted = "0";//打印状态 0-未打印 1-已打印
    private String choosedPrinter;//选择的打印机

    private String src;//打印列表来源

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personalprint;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "");

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

        src = getIntent().getStringExtra("src");
        if (TextUtils.isEmpty(src)){
            src = "EWP04AA";
        }

        loadingDialog = new LoadingUncancelable(this);

        initEmptyAndErrorView();
        initAdapter();
        getPrinter();

        if (!EventBusUtil.getInstence().isRegistered(this)){
            EventBusUtil.getInstence().register(this);
        }
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

    private void initAdapter() {
        adapter = new PersonalPrintAdapter(R.layout.item_personalprint, new ArrayList<PersonalPrintItem>());
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

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                Intent intent = new Intent(PersonalPrintActivity.this, PersonalPrintDetailActivity.class);
                intent.putExtra("status", adapter.getData().get(position).getBcls_status());
                intent.putExtra("mid", adapter.getData().get(position).getBclm_id());
                startActivityForResult(intent, 0x23);
            }
        });

        onRefresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x23){
            if (resultCode == Activity.RESULT_OK){
                onRefresh();
            }
        }
    }

    private void doNetwork(final boolean isRefresh){
        if (!hasNetWork()){
            adapter.setEmptyView(errorView);
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=QueryPrint")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("isprint", isPrinted)
                .params("ids", "")
                .params("pageno", currentPage+"")
                .params("pagesize", pageSize)
                .params("src", src)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        PersonalPrintData data = SceoUtil.parseJson(s, PersonalPrintData.class);
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
                            Toasty.error(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalPrintActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
    }

    @Override
    public void onLoadMoreRequested() {
        ++currentPage;
        doNetwork(false);
    }

    private void getPrinter(){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=PrinterSgt")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        PrinterData data = SceoUtil.parseJson(s, PrinterData.class);
                        if (data.getStatus() == 0){
                            if (printerList != null){
                                printerList.clear();
                            }
                            printerList = data.getData();

                            //增加蓝牙打印机
                            PrinterItem btItem = new PrinterItem();
                            btItem.setPsvr_id("000");
                            btItem.setPsvr_no("蓝牙打印机");
                            printerList.add(btItem);
                            spinner.setAdapter(new PersonalPrintSpinnerAdapter(PersonalPrintActivity.this, printerList));

                            if (!TextUtils.isEmpty(SceoUtil.getChoosedPrinterId(PersonalPrintActivity.this))){
                                for (int i = 0; i < printerList.size(); i++){
                                    if (printerList.get(i).getPsvr_id().equals(SceoUtil.getChoosedPrinterId(PersonalPrintActivity.this))){
                                        choosedPrinter = printerList.get(i).getPsvr_id();
                                        spinner.setSelection(i);
                                        break;
                                    }
                                }
                            }else {
                                choosedPrinter = printerList.get(0).getPsvr_id();
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalPrintActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }

    private void doPrintNetwork(final String ids, final String reprint){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP01AA&pcmd=Print")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("isprint", isPrinted)
                .params("ids", ids)
                .params("psvr", choosedPrinter)
                .params("reprint", reprint)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        OutputPrintData data = SceoUtil.parseJson(s, OutputPrintData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            if ("000".equals(choosedPrinter)){
                                Intent intent = new Intent(PersonalPrintActivity.this, PrintActivity.class);
                                intent.putExtra("data", (Serializable) data.getData());
                                intent.putExtra("ids", ids);
                                intent.putExtra("reprint", reprint);
                                startActivity(intent);
                            }
                            onRefresh();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalPrintActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(PersonalPrintActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            choosedPrinter = printerList.get(position).getPsvr_id();
            SceoUtil.setChoosedPrinterId(PersonalPrintActivity.this, choosedPrinter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @OnClick(R.id.pp_print_btn)
    void doPrint(){
        StringBuilder tmpStr = new StringBuilder();
        for (PersonalPrintItem item : adapter.getData()){
            if (item.isChecked()){
                tmpStr.append(item.getBclm_id()).append(",");
            }
        }

        if (TextUtils.isEmpty(tmpStr)){
            Toasty.warning(PersonalPrintActivity.this, "请先选择", Toast.LENGTH_SHORT, true).show();
            return;
        }

        doPrintNetwork(tmpStr.toString(), "0");
    }

    /**
     * 未打印
     */
    @OnClick(R.id.pp_rb_no)
    void loadNotPrint(){
        printBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        printBtn.setEnabled(true);
        checkTv.setVisibility(View.VISIBLE);
        isPrinted = "0";
        onRefresh();
    }

    /**
     * 已打印
     */
    @OnClick(R.id.pp_rb_yes)
    void loadYesPrint(){
        printBtn.setBackgroundColor(getResources().getColor(R.color.gray));
        printBtn.setEnabled(false);
        checkTv.setVisibility(View.GONE);
        isPrinted = "1";
        onRefresh();
    }

    @OnClick(R.id.pp_checkall)
    void doCheack(){
        if (!ischeckAll){
            checkTv.setText("全不选");
            ischeckAll = true;
        }else {
            checkTv.setText("全选");
            ischeckAll = false;
        }

        for (PersonalPrintItem item : adapter.getData()){
            item.setChecked(ischeckAll);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 滑动删除
     */
    private class MyOnItemSwipeListener implements OnItemSwipeListener{

        @Override
        public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {

        }

        @Override
        public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
            delItem(adapter.getData().get(pos).getBclm_id(), "1");
        }

        @Override
        public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float dX, float dY, boolean isCurrentlyActive) {
            canvas.drawColor(ContextCompat.getColor(PersonalPrintActivity.this, R.color.colorAccent));
            Paint paint = new Paint();
            paint.setStrokeWidth(1);
            paint.setTextSize(23);
            paint.setColor(Color.WHITE);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("滑动删除", 70, 55, paint);
        }
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
                            Toasty.success(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalPrintActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(PersonalPrintActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
     * 拖动
     */
    private class MyOnItemDragListener implements OnItemDragListener{

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
    protected void onDestroy() {
        super.onDestroy();
        if (EventBusUtil.getInstence().isRegistered(this)){
            EventBusUtil.getInstence().unregister(this);
        }
    }

    @Subscribe
    public void onEventMainThread(PersonalPrintToogleEvent event){
        if (event.isToogle()){
            boolean istrue = true;
            for (PersonalPrintItem item : adapter.getData()){
                istrue = istrue && item.isChecked();
            }
            if (istrue){
                checkTv.setText("全不选");
                ischeckAll = true;
            }else {
                checkTv.setText("全选");
                ischeckAll = false;
            }
        }
    }
}
