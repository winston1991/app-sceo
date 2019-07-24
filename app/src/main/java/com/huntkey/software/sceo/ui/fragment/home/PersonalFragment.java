package com.huntkey.software.sceo.ui.fragment.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseFragment;
import com.huntkey.software.sceo.entity.HomeMenuItem;
import com.huntkey.software.sceo.entity.StorageItem;
import com.huntkey.software.sceo.ui.activity.attence.AttenceActivity;
import com.huntkey.software.sceo.ui.activity.attence.AttenceWebActivity;
import com.huntkey.software.sceo.ui.activity.attence.BaiduMapActivity;
import com.huntkey.software.sceo.ui.adapter.home.StorageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by chenl on 2017/9/12.
 */

public class PersonalFragment extends KnifeBaseFragment {

    @BindView(R.id.f_storage_toolbar)
    Toolbar toolbar;
    @BindView(R.id.f_storage_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private List<StorageItem> dataList = new ArrayList<>();

    private Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String name = getArguments().getString("name");
        tvTitle.setText(name);

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
                    case "cwav4.ewa00aa"://移动考勤
//                        Intent intent1 = new Intent(getActivity(), AttenceWebActivity.class);
                        Intent intent1 = new Intent(getActivity(), AttenceActivity.class);
                        startActivity(intent1);
                        break;
                    case "cwav4.ewa00ab"://考勤设置
                        Intent intent2 = new Intent(getActivity(), BaiduMapActivity.class);
                        startActivity(intent2);
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
