package com.huntkey.software.sceo.ui.activity.home.storagequery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.StorageQueryCData;
import com.huntkey.software.sceo.entity.StorageQueryCItem;
import com.huntkey.software.sceo.ui.LoadingUncancelable;
import com.huntkey.software.sceo.ui.adapter.home.StorageQueryCAdapter;
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
 * Created by chenl on 2018/2/11.
 */

public class StorageQueryCActivity extends KnifeBaseActivity {

    @BindView(R.id.stoq_c_toolbar)
    Toolbar toolbar;
    @BindView(R.id.stoq_c_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.stoq_c_part)
    TextView partTv;
    @BindView(R.id.stoq_c_desc)
    TextView descTv;

    private String partStr;
    private String siteStr;
    private LoadingUncancelable loadingDialog;
    private StorageQueryCAdapter adapter;
    private List<StorageQueryCItem> data = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_storage_query_c;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "库存查询");

        loadingDialog = new LoadingUncancelable(this);

        partStr = getIntent().getStringExtra("part");
        siteStr = getIntent().getStringExtra("site");
        partTv.setText("料号 " + partStr);
        descTv.setText(siteStr);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StorageQueryCAdapter(R.layout.item_storage_query_c, data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                Intent intent = new Intent(StorageQueryCActivity.this, StorageQueryDActivity.class);
                intent.putExtra("part", adapter.getItem(position).getLd_part());
                intent.putExtra("site", siteStr);
                intent.putExtra("loc", adapter.getItem(position).getLd_loc());
                intent.putExtra("lot", adapter.getItem(position).getLd_lot());
                intent.putExtra("ref", adapter.getItem(position).getLd_ref());
                startActivity(intent);
            }
        });

        doNetwork();
    }

    private void doNetwork() {
        OkGo.post(Conf.SERVICE_URL + "wm16sys/csp/wm16sys.dll?page=EWP20AA&pcmd=Query")
                .tag(this)
                .params("sessionkey", SceoUtil.getSessionKey(this))
                .params("part", partStr)
                .params("site", siteStr)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        StorageQueryCData data = SceoUtil.parseJson(s, StorageQueryCData.class);
                        if (data.getStatus() == 0){
                            if (data.getData().size() > 0) {
                                for (int i = 0; i < data.getData().size(); i++){
                                    data.getData().get(i).setNum(i + 1);
                                }
                            }
                            adapter.setNewData(data.getData());
                        }else if (data.getStatus() == 88) {
                            Toasty.error(StorageQueryCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
                            SceoUtil.gotoLogin(StorageQueryCActivity.this);
                            SceoApplication.getInstance().exit();
                        }else {
                            Toasty.error(StorageQueryCActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
                        Toasty.error(StorageQueryCActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
                    }
                });
    }
}
