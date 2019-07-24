package com.huntkey.software.sceo.ui.activity.home.outstockgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.InputPickingNumItem;
import com.huntkey.software.sceo.ui.adapter.home.ChooseStorekeeperAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by chenl on 2017/5/10.
 */

public class ChooseStorekeeperActivity extends KnifeBaseActivity {

    @BindView(R.id.csk_toolbar)
    Toolbar toolbar;
    @BindView(R.id.csk_recyclerView)
    RecyclerView recyclerView;

    private ChooseStorekeeperAdapter adapter;
    private List<InputPickingNumItem> data = new ArrayList<>();
    private String nbrs;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_choosestorekeeper;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "选择仓管");

        data = (List<InputPickingNumItem>) getIntent().getSerializableExtra("data");
        nbrs = getIntent().getStringExtra("nbrs");

        if (data.size() > 0){
            data.get(0).setChoosed(true);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();
    }

    private void initAdapter() {
        adapter = new ChooseStorekeeperAdapter(R.layout.item_choosestorekeeper, data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                for (InputPickingNumItem item : adapter.getData()){
                    item.setChoosed(false);
                }
                adapter.getData().get(position).setChoosed(true);
                adapter.notifyDataSetChanged();
            }
        });
    }

    String emp;
    @OnClick(R.id.csk_nextstep)
    void nextStep(){
        for (InputPickingNumItem item : adapter.getData()){
            if (item.isChoosed()){
                emp = item.getLad_warehouse_by();
            }
        }
        Intent intent = new Intent(ChooseStorekeeperActivity.this, OutstockGoodsActivity.class);
        intent.putExtra("nbrs", nbrs);
        intent.putExtra("emp", emp);
        startActivity(intent);
    }
}
