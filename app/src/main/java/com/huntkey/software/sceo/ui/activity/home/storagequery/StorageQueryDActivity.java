package com.huntkey.software.sceo.ui.activity.home.storagequery;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.StorageQueryCData;
import com.huntkey.software.sceo.entity.StorageQueryDData;
import com.huntkey.software.sceo.entity.StorageQueryDItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.StorageQueryDAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.BaseRequest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2018/2/23.
 */

public class StorageQueryDActivity extends KnifeBaseActivity {

    @BindView(R.id.stoq_d_toolbar)
    Toolbar toolbar;
    @BindView(R.id.stoq_d_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.stoq_d_part)
    TextView partTv;
    @BindView(R.id.stoq_d_desc)
    TextView descTv;

    private LoadingUncancelable loadingDialog;
    private String partStr;
    private String siteStr;
    private String locStr;
    private String lotStr;
    private String refStr;
    private StorageQueryDAdapter adapter;
    private List<StorageQueryDItem> data = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_storage_query_d;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "捡料列表");

        loadingDialog = new LoadingUncancelable(this);
        partStr = getIntent().getStringExtra("part");
        siteStr = getIntent().getStringExtra("site");
        locStr = getIntent().getStringExtra("loc");
        lotStr = getIntent().getStringExtra("lot");
        refStr = getIntent().getStringExtra("ref");

        partTv.setText("料号 " + partStr);
        descTv.setText(siteStr);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StorageQueryDAdapter(R.layout.item_storage_query_d, data);
        recyclerView.setAdapter(adapter);

        doNetwork();
    }

    private void doNetwork() {
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP20AA&pcmd=QueryPick")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("part", partStr)
                .params("site", siteStr)
                .params("loc", locStr)
                .params("lot", lotStr)
                .params("ref", refStr)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        StorageQueryDData data = SceoUtil.parseJson(s, StorageQueryDData.class);
                        if (data.getStatus() == 0){
                            if (data.getData().size() > 0) {
                                for (int i = 0; i < data.getData().size(); i++){
                                    data.getData().get(i).setNum(i + 1);
                                }
                            }
                            adapter.setNewData(data.getData());
                        }else if (data.getStatus() == 88) {
                            Toasty.error(StorageQueryDActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(StorageQueryDActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(StorageQueryDActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                        }
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

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        Toasty.error(StorageQueryDActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }
}
