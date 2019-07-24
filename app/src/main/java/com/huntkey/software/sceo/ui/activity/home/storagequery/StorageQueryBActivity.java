package com.huntkey.software.sceo.ui.activity.home.storagequery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.StorageQueryItem;
import com.huntkey.software.sceo.ui.adapter.home.StorageQueryBAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by chenl on 2018/2/8.
 */

public class StorageQueryBActivity extends KnifeBaseActivity {

    @BindView(R.id.stoq_b_toolbar)
    Toolbar toolbar;
    @BindView(R.id.stoq_b_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.stoq_part)
    TextView partTv;
    @BindView(R.id.stoq_desc)
    TextView descTv;

    private List<StorageQueryItem> data = new ArrayList<>();
    private StorageQueryBAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_storage_query_b;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "库存查询");

        data = (List<StorageQueryItem>) getIntent().getSerializableExtra("data");

        if (data.size() > 0) {
            partTv.setText("料号 " + data.get(0).getIn_part());
            descTv.setText(data.get(0).getPt_desc());
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
    }

    private void initAdapter() {
        adapter = new StorageQueryBAdapter(R.layout.item_storage_query, data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                Intent intent = new Intent(StorageQueryBActivity.this, StorageQueryCActivity.class);
                intent.putExtra("part", adapter.getItem(position).getIn_part());
                intent.putExtra("site", adapter.getItem(position).getIn_site());
                startActivity(intent);
            }
        });
    }
}
