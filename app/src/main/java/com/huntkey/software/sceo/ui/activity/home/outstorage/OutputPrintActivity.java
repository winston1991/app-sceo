package com.huntkey.software.sceo.ui.activity.home.outstorage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.OutputPrintData;
import com.huntkey.software.sceo.entity.OutputPrintItem;
import com.huntkey.software.sceo.entity.PrinterData;
import com.huntkey.software.sceo.entity.PrinterItem;
import com.huntkey.software.sceo.entity.ToogleEvent;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.activity.home.bluetoothprint.PrintActivity;
import com.huntkey.software.sceo.ui.adapter.home.OutputPrintAdapter;
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
 * Created by chenl on 2017/4/27.
 */

public class OutputPrintActivity extends KnifeBaseActivity {

    @BindView(R.id.opp_toolbar)
    Toolbar toolbar;
    @BindView(R.id.opp_job)
    TextView nbrTv;
    @BindView(R.id.opp_num)
    TextView refNbrTv;
    @BindView(R.id.opp_spinner)
    Spinner spinner;
    @BindView(R.id.opp_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.opp_checkall)
    TextView checkTv;

    private String nbr;
    private String refNbr;

    private View emptyView;
    private View errorView;

    private List<PrinterItem> printerList = new ArrayList<>();
    private String choosedPrinter;//选择的打印机

    private boolean ischeckAll = false;
    private OutputPrintAdapter adapter;

    private LoadingUncancelable loadingDialog;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_outputprint;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "待打印条码列表");

        nbr = getIntent().getStringExtra("nbr");
        refNbr = getIntent().getStringExtra("refnbr");
        nbrTv.setText("任务：" + nbr);
        refNbrTv.setText(refNbr);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());

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

    private void initAdapter(){
        adapter = new OutputPrintAdapter(R.layout.item_personalprint, new ArrayList<OutputPrintItem>());
        recyclerView.setAdapter(adapter);
        doNetwork();
    }

    private void doNetwork(){
        if (!hasNetWork()) {
            adapter.setEmptyView(errorView);
            return;
        }

        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP07AA&pcmd=GetPrint")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("nbr", nbr)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        OutputPrintData data = SceoUtil.parseJson(s, OutputPrintData.class);
                        if (data.getStatus() == 0){
                            adapter.setNewData(data.getData());

                            if (data.getData().size() == 0){
                                adapter.setEmptyView(emptyView);
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputPrintActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputPrintActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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
                            spinner.setAdapter(new PersonalPrintSpinnerAdapter(OutputPrintActivity.this, printerList));

                            if (!TextUtils.isEmpty(SceoUtil.getChoosedPrinterId(OutputPrintActivity.this))){
                                for (int i = 0; i < printerList.size(); i++){
                                    if (printerList.get(i).getPsvr_id().equals(SceoUtil.getChoosedPrinterId(OutputPrintActivity.this))){
                                        choosedPrinter = printerList.get(i).getPsvr_id();
                                        spinner.setSelection(i);
                                        break;
                                    }
                                }
                            }else {
                                choosedPrinter = printerList.get(0).getPsvr_id();
                            }
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputPrintActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }

    private class MyOnItemSelectedListener implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            choosedPrinter = printerList.get(position).getPsvr_id();
            SceoUtil.setChoosedPrinterId(OutputPrintActivity.this, choosedPrinter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void doPrintNetwork(final String ids, final String reprint){
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
                            Toasty.success(OutputPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            if ("000".equals(choosedPrinter)){
                                Intent intent = new Intent(OutputPrintActivity.this, PrintActivity.class);
                                intent.putExtra("data", (Serializable) data.getData());
                                intent.putExtra("ids", ids);
                                intent.putExtra("reprint", reprint);
                                startActivity(intent);
                            }
                            doNetwork();
                        }else if (data.getStatus() == 88) {
                            Toasty.error(OutputPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(OutputPrintActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(OutputPrintActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(OutputPrintActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
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

    @OnClick(R.id.opp_print_btn)
    void doPrint(){
        StringBuilder tmpStr = new StringBuilder();
        for (OutputPrintItem item : adapter.getData()){
            if (item.isChecked()){
                tmpStr.append(item.getBcls_id()).append(",");
            }
        }

        if (TextUtils.isEmpty(tmpStr)){
            Toasty.warning(OutputPrintActivity.this, "请先选择", Toast.LENGTH_SHORT, true).show();
            return;
        }

        doPrintNetwork(tmpStr.toString(), "1");
    }

    @OnClick(R.id.opp_checkall)
    void doCheack(){
        if (!ischeckAll){
            checkTv.setText("全不选");
            ischeckAll = true;
        }else {
            checkTv.setText("全选");
            ischeckAll = false;
        }

        for (OutputPrintItem item : adapter.getData()){
            item.setChecked(ischeckAll);
        }
        adapter.notifyDataSetChanged();
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
            for (OutputPrintItem item : adapter.getData()){
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
