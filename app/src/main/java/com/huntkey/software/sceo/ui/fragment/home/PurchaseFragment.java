package com.huntkey.software.sceo.ui.fragment.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseFragment;
import com.huntkey.software.sceo.entity.HomeMenuItem;
import com.huntkey.software.sceo.entity.StorageItem;
import com.huntkey.software.sceo.ui.adapter.home.StorageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by chenl on 2017/3/30.
 */

public class PurchaseFragment extends KnifeBaseFragment {

    @BindView(R.id.f_purchase_toolbar)
    Toolbar toolbar;
    @BindView(R.id.f_purchase_recyclerView)
    RecyclerView recyclerView;

    private List<StorageItem> dataList = new ArrayList<>();

    private Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        View view = inflater.inflate(R.layout.fragment_purchase, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String name = getArguments().getString("name");
        toolbar.setTitle(name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        initData();
        initAdapter();
    }

    private void initData() {
        int parentId = getArguments().getInt("parentId");
        RealmResults<HomeMenuItem> results = realm.where(HomeMenuItem.class).equalTo("modt_parent_id", parentId).findAll();
        for (HomeMenuItem item : results){
            StorageItem storageItem = new StorageItem();
            storageItem.setTitle(item.getModt_name());
            storageItem.setImgUrl(Conf.SERVICE_URL + item.getModt_icon_path());
            storageItem.setSysCode(item.getModt_syscode());
            dataList.add(storageItem);
        }
    }

    private void initAdapter() {
        BaseQuickAdapter homeAdapter = new StorageAdapter(R.layout.item_storage, dataList);
        homeAdapter.openLoadAnimation();
        homeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                switch (dataList.get(position).getSysCode()){
                    case "dkt.dop":// 数据输出平台
                        Toasty.normal(getActivity(), "敬请期待", Toast.LENGTH_SHORT).show();
                        break;
                    case "dkt.dip":// 数据输入平台
                        Toasty.normal(getActivity(), "敬请期待", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        recyclerView.setAdapter(homeAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
