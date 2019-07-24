package com.huntkey.software.sceo.ui.activity.home.personaljob;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.entity.PersonalJobData;
import com.huntkey.software.sceo.entity.PersonalJobItem;
import com.huntkey.software.sceo.entity.ToogleEvent;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.activity.home.instorage.InputTaskNumActivity;
import com.huntkey.software.sceo.ui.activity.home.instorage.WarehousingActivity;
import com.huntkey.software.sceo.ui.activity.home.outstorage.OutputTaskNumActivity;
import com.huntkey.software.sceo.ui.activity.home.outstorage.StorageLocationActivity;
import com.huntkey.software.sceo.ui.adapter.home.PersonalJobAdapter;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/3/29.
 */

public class PersonalJobActivity extends KnifeBaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.pj_toolbar)
    Toolbar toolbar;
    @BindView(R.id.pj_refreshlayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.pj_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.pj_float_menu)
    FloatingActionMenu floatingActionMenu;
    @BindView(R.id.pj_checkall)
    TextView checkTv;
    @BindView(R.id.pj_search_view)
    MaterialSearchView searchView;
    @BindView(R.id.pj_line)
    View line;
    @BindView(R.id.pj_order)
    Button orderBtn;
    @BindView(R.id.pj_other)
    Button otherBtn;
    @BindView(R.id.pj_chooseBtns)
    LinearLayout chooseBtnsLayout;
    @BindView(R.id.pj_line_layout)
    LinearLayout linearLayout;

    private View emptyView;
    private View errorView;
    private LoadingUncancelable loadingDialog;

    private boolean ischeckAll = false;
    private boolean isErr = false;
    private PersonalJobAdapter adapter;
    private int currentPage = 1;//当前页
    private String pageSize = "20";//一次请求数据的条数
    private String group = "1";//0-入库 1-出库
    private String scanFlag = "1";//1-扫码 其他-非扫码
    private int screenWidth;
    private boolean lineFlag = false;
    private boolean isOrder = true;
    private List<PersonalJobItem> allItems = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_personaljob;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "");

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

        loadingDialog = new LoadingUncancelable(this);
        screenWidth = getWindowManager().getDefaultDisplay().getWidth();
        computeIndicateLineWidth();
        initEmptyAndErrorView();
        initAdapter();
        initSearch();

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
        adapter = new PersonalJobAdapter(R.layout.item_personaljob, new ArrayList<PersonalJobItem>());
        adapter.setOnLoadMoreListener(this, recyclerView);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.disableLoadMoreIfNotFullPage(recyclerView);

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                if ("0".equals(group)){//入库
                    Intent intent = new Intent(PersonalJobActivity.this, InputTaskNumActivity.class);
                    intent.putExtra("nbr", adapter.getData().get(position).getTskm_nbr_group());
                    startActivity(intent);
                }else {//出库
                    Intent intent = new Intent(PersonalJobActivity.this, OutputTaskNumActivity.class);
                    intent.putExtra("nbr", adapter.getData().get(position).getTskm_nbr_group());
                    startActivity(intent);
                }
            }
        });

        onRefresh();
    }

    private void doNetwork(final boolean isRefresh, final String cond) {
        if (!hasNetWork()) {
            adapter.setEmptyView(errorView);
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP05AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("group", group)
                .params("scan", scanFlag)
                .params("cond", cond)
                .params("pageno", currentPage+"")
                .params("pagesize", pageSize)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        PersonalJobData data = SceoUtil.parseJson(s, PersonalJobData.class);
                        if (data.getStatus() == 0){
                            List<PersonalJobItem> results = data.getData().getResults();
                            if ("1".equals(group) && results.size() > 0){ // 出库全部可以合并
                                for (PersonalJobItem item : results){
                                    item.setGroup(group);
                                }
                            }
                            if ("1".equals(group)) {
                                if (isRefresh){
                                    allItems.clear();
                                }
                                allItems.addAll(results);
                                if (isOrder) {
                                    List<PersonalJobItem> items = new ArrayList<>();
                                    for (PersonalJobItem item : results) {
                                        if ("23".equals(item.getLstm_type())){
                                            items.add(item);
                                        }
                                    }
                                    if (isRefresh){
                                        adapter.setNewData(items);
                                    }else {
                                        adapter.addData(items);
                                    }
                                } else {
                                    List<PersonalJobItem> items = new ArrayList<>();
                                    for (PersonalJobItem item : results) {
                                        if (!"23".equals(item.getLstm_type())){
                                            items.add(item);
                                        }
                                    }
                                    if (isRefresh){
                                        adapter.setNewData(items);
                                    }else {
                                        adapter.addData(items);
                                    }
                                }

                                int totalRow = data.getData().getPage().getRowcount();
                                if (allItems.size() == totalRow){
                                    adapter.loadMoreEnd(true);
                                }
                            } else {
                                if (isRefresh){
                                    adapter.setNewData(results);

                                    if (results.size() == 0){
                                        adapter.setEmptyView(emptyView);
                                    }
                                }else {
                                    adapter.addData(results);
                                }

                                int totalRow = data.getData().getPage().getRowcount();
                                if (adapter.getData().size() == totalRow){
                                    adapter.loadMoreEnd(true);
                                }
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalJobActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalJobActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalJobActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
        doNetwork(true, "");
    }

    @Override
    public void onLoadMoreRequested() {
        ++currentPage;
        doNetwork(false, "");
    }

    //出库--扫码出
    @OnClick(R.id.pj_rb_out)
    void getOuts(){
        if (adapter.isLoading()){
            adapter.loadMoreComplete();
        }
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        OkGo.getInstance().cancelAll();
        List<PersonalJobItem> tmpList = new ArrayList<>();
        adapter.setNewData(tmpList);
        chooseBtnsLayout.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        group = "1";
        scanFlag = "1";
        onRefresh();
    }

    //非扫码出库--非扫码出
    @OnClick(R.id.pj_rb_out_no)
    void getOutsNo(){
        if (adapter.isLoading()){
            adapter.loadMoreComplete();
        }
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        OkGo.getInstance().cancelAll();
        List<PersonalJobItem> tmpList = new ArrayList<>();
        adapter.setNewData(tmpList);
        chooseBtnsLayout.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.VISIBLE);
        group = "1";
        scanFlag = "0";
        onRefresh();
    }

    @OnClick(R.id.pj_order)
    void orderQuery(){
        isOrder = true;
        List<PersonalJobItem> items = new ArrayList<>();
        for (PersonalJobItem item : allItems) {
            if ("23".equals(item.getLstm_type())){
                items.add(item);
            }
        }
        adapter.setNewData(items);
    }

    @OnClick(R.id.pj_other)
    void otherQuery(){
        isOrder = false;
        List<PersonalJobItem> items = new ArrayList<>();
        for (PersonalJobItem item : allItems) {
            if (!"23".equals(item.getLstm_type())){
                items.add(item);
            }
        }
        adapter.setNewData(items);
    }

    //入库
    @OnClick(R.id.pj_rb_in)
    void getIns(){
        if (adapter.isLoading()){
            adapter.loadMoreComplete();
        }
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        OkGo.getInstance().cancelAll();
        List<PersonalJobItem> tmpList = new ArrayList<>();
        adapter.setNewData(tmpList);
        chooseBtnsLayout.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        group = "0";
        onRefresh();
    }

    //出库-制令发料
    @OnClick(R.id.pj_order)
    void clickOrder(){
        orderBtn.setTextColor(getResources().getColor(R.color.text_color_blue));
        otherBtn.setTextColor(getResources().getColor(R.color.text_color_normal));
        if (!lineFlag) {
            ViewPropertyAnimator.animate(line).translationX(screenWidth / 2).setDuration(200);
            lineFlag = !lineFlag;
        } else {
            ViewPropertyAnimator.animate(line).translationX(0).setDuration(200);
            lineFlag = !lineFlag;
        }
    }

    //出库-其他单据
    @OnClick(R.id.pj_other)
    void clickOther(){
        orderBtn.setTextColor(getResources().getColor(R.color.text_color_normal));
        otherBtn.setTextColor(getResources().getColor(R.color.text_color_blue));
        if (!lineFlag) {
            ViewPropertyAnimator.animate(line).translationX(screenWidth / 2).setDuration(200);
            lineFlag = !lineFlag;
        } else {
            ViewPropertyAnimator.animate(line).translationX(0).setDuration(200);
            lineFlag = !lineFlag;
        }
    }

    //设置底部指示线条宽度
    private void computeIndicateLineWidth(){
        line.getLayoutParams().width = screenWidth / 2;
    }

    //合并
    @OnClick(R.id.pj_combine_fab)
    void combine(){
        floatingActionMenu.close(true);
        StringBuffer tmpStr = new StringBuffer();
        List<String> tmpList = new ArrayList<>();
        Set<String> tmpSet = new HashSet<>();
        Set<String> tmpType = new HashSet<>();
        for (PersonalJobItem item : adapter.getData()){
            if (item.isChecked()){
                tmpStr.append(item.getTskm_id()).append(",");
                tmpList.add(item.getTskm_id());
                tmpSet.add(item.getTskm_dept());
                tmpType.add(item.getLstm_type());
            }
        }
        if (tmpType.size() > 1){
            Toasty.warning(this, "相同类型的单据才允许合并!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (tmpSet.size() > 1){
            Toasty.warning(this, "选择的需求部门不一致!", Toast.LENGTH_SHORT, true).show();
            return;
        }
        if (tmpStr.toString().length() > 0){
            if (tmpList.size() <= 1){
                Toasty.warning(this, "请至少选择两个!", Toast.LENGTH_SHORT, true).show();
            }else {
                doCombine(tmpStr.toString());
            }
        }else {
            Toasty.warning(this, "请先选择", Toast.LENGTH_SHORT, true).show();
        }
    }

    @OnClick(R.id.pj_checkall)
    void doCheack(){
        if (!ischeckAll){
            checkTv.setText("全不选");
            ischeckAll = true;
        }else {
            checkTv.setText("全选");
            ischeckAll = false;
        }

        for (PersonalJobItem item : adapter.getData()){
            if ("23".equals(item.getLstm_type())){
                item.setChecked(ischeckAll);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void doCombine(String ids){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP05AA&pcmd=TaskUnion")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("ids", ids)
                .params("group", group)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(PersonalJobActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            onRefresh();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalJobActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalJobActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalJobActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(PersonalJobActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }

    @OnClick(R.id.pj_split_fab)
    void split(){
        floatingActionMenu.close(true);
        StringBuffer tmpStr = new StringBuffer();
        for (PersonalJobItem item : adapter.getData()){
            if (item.isChecked() && "1".equals(item.getTskm_union())){
                tmpStr.append(item.getTskm_id()).append(",");
            }
        }
        if (tmpStr.toString().length() > 0){
            doSplit(tmpStr.toString());
        }else {
            Toasty.warning(PersonalJobActivity.this, "已合并任务才能拆分", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void doSplit(String ids){
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP05AA&pcmd=TaskSplit")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("ids", ids)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        //增加sessionkey过期处理
                        BaseData data = SceoUtil.parseJson(s, BaseData.class);
                        if (data.getStatus() == 0){
                            Toasty.success(PersonalJobActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            onRefresh();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(PersonalJobActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(PersonalJobActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(PersonalJobActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(PersonalJobActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
    protected void onDestroy() {
        super.onDestroy();
        if (EventBusUtil.getInstence().isRegistered(this)){
            EventBusUtil.getInstence().unregister(this);
        }
    }

    @Subscribe
    public void onEventMainThread(ToogleEvent event){
        if (event.isToogle()){
            boolean istrue = true;
            for (PersonalJobItem item : adapter.getData()){
                if ("23".equals(item.getLstm_type()) || "24".equals(item.getLstm_type())){
                    istrue = istrue && item.isChecked();
                }
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

    private void initSearch() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentPage = 1;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personalprintdetail, menu);
        MenuItem item = menu.findItem(R.id.ppd_action_search);
        searchView.setMenuItem(item);
        return true;
    }
}
