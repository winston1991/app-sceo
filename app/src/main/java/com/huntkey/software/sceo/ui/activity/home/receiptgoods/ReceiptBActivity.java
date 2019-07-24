package com.huntkey.software.sceo.ui.activity.home.receiptgoods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.ReceiptAItem;
import com.huntkey.software.sceo.ui.adapter.home.ReceiptBAdapter;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

/**
 * Created by chenl on 2017/7/10.
 */

public class ReceiptBActivity extends KnifeBaseActivity implements MaterialSearchView.OnQueryTextListener {

    @BindView(R.id.rpt_b_toolbar)
    Toolbar toolbar;
    @BindView(R.id.rpt_b_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rpt_b_search_view)
    MaterialSearchView searchView;
    @BindView(R.id.rpt_b_cet)
    ClearEditText bCet;

    private List<ReceiptAItem> data = new ArrayList<>();
    private ReceiptBAdapter adapter;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_receipt_b;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setToolBar(toolbar, "收货点货");

        data = (List<ReceiptAItem>) getIntent().getSerializableExtra("data");

        bCet.requestFocus();
        bCet.setOnKeyListener(new MyOnKeyListener());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        initAdapter();

        searchView.setOnQueryTextListener(this);
    }

    private class MyOnKeyListener implements View.OnKeyListener{
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP){
                doScan(bCet.getText().toString().trim());
                bCet.setText("");
            }
            return false;
        }
    }

    private void doScan(String nbr) {
        List<String> nbrList = new ArrayList<>();
        for (int i = 0; i < adapter.getItemCount(); i++){
            nbrList.add(adapter.getData().get(i).getLstm_nbr());
            if (nbr.equals(adapter.getData().get(i).getLstm_nbr())){
                if (adapter.getItem(i).isChoosed()){
                    adapter.getItem(i).setChoosed(false);
                }else {
                    adapter.getItem(i).setChoosed(true);
                }
                adapter.notifyItemChanged(i);
            }
        }
        if (!nbrList.contains(nbr)){
            Toasty.error(ReceiptBActivity.this, "该收货单号不属于当前供应商", Toast.LENGTH_SHORT).show();
        }
    }

    private void initAdapter() {
        adapter = new ReceiptBAdapter(R.layout.item_choosestorekeeper, data);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter1, View view, int position) {
                if (adapter.getItem(position).isChoosed()){
                    adapter.getItem(position).setChoosed(false);
                }else {
                    adapter.getItem(position).setChoosed(true);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.rpt_b_nextstep)
    void nextStep(){
        StringBuffer nbrs = new StringBuffer();
        for (ReceiptAItem item : data){
            if (item.isChoosed()){
                nbrs.append(item.getLstm_nbr()).append(",");
            }
        }
        if (!TextUtils.isEmpty(nbrs.toString())){
            Intent intent = new Intent(ReceiptBActivity.this, ReceiptCActivity.class);
            intent.putExtra("nbrs", nbrs.toString());
            startActivity(intent);
        }else {
            Toasty.info(ReceiptBActivity.this, "请选择", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_personalprintdetail, menu);
        MenuItem item = menu.findItem(R.id.ppd_action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        List<ReceiptAItem> results = new ArrayList<>();
        Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
        for (ReceiptAItem item : data){
            Matcher matcher = pattern.matcher(item.getLstm_nbr());
            if (matcher.find()){
                results.add(item);
            }
        }
        adapter.setNewData(results);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
