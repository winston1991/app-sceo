package com.huntkey.software.sceo.ui.fragment.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseFragment;
import com.huntkey.software.sceo.entity.Domain;
import com.huntkey.software.sceo.entity.HomeMenuData;
import com.huntkey.software.sceo.entity.HomeMenuItem;
import com.huntkey.software.sceo.entity.StorageItem;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.activity.home.DowndataActivity;
import com.huntkey.software.sceo.ui.activity.home.HomeActivity;
import com.huntkey.software.sceo.ui.activity.home.barconversion.ExternalBarConversionAActivity;
import com.huntkey.software.sceo.ui.activity.home.barrepair.BarRepairAActivity;
import com.huntkey.software.sceo.ui.activity.home.barsplit.BarSplitAActivity;
import com.huntkey.software.sceo.ui.activity.home.cloakoperations.ScanBoxNumActivity;
import com.huntkey.software.sceo.ui.activity.home.createqr.CreateQrActivity;
import com.huntkey.software.sceo.ui.activity.home.instorage.InputTaskNumActivity;
import com.huntkey.software.sceo.ui.activity.home.iqccheck.IQCCheckActivity;
import com.huntkey.software.sceo.ui.activity.home.orderinstore.OrderInstoreAActivity;
import com.huntkey.software.sceo.ui.activity.home.outstockgoods.InputPickingNumActivity;
import com.huntkey.software.sceo.ui.activity.home.outstorage.OutputTaskNumActivity;
import com.huntkey.software.sceo.ui.activity.home.personaljob.PersonalJobActivity;
import com.huntkey.software.sceo.ui.activity.home.personalprint.PersonalPrintActivity;
import com.huntkey.software.sceo.ui.activity.home.receiptgoods.ReceiptAActivity;
import com.huntkey.software.sceo.ui.activity.home.storageadjust.AdjustOrderActivity;
import com.huntkey.software.sceo.ui.activity.home.storagequery.StorageQueryAActivity;
import com.huntkey.software.sceo.ui.adapter.home.StorageAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.Settings;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/3/30.
 */

public class StorageFragment extends KnifeBaseFragment {

    @BindView(R.id.f_storage_toolbar)
    Toolbar toolbar;
    @BindView(R.id.f_storage_recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_domain)
    TextView tvDomain;
    @BindView(R.id.change_domain_layout)
    LinearLayout changeDomainLayout;

    private List<StorageItem> dataList = new ArrayList<>();

    private Realm realm;

    List<Domain.Lists.Item> domains;
    private LoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        loadingDialog = new LoadingDialog(getContext());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String name = getArguments().getString("name");

        tvTitle.setText(name + Settings.getVersionName(getActivity()));

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        initDomain();
        initData();
        initAdapter();
    }

    // 查询域信息
    private void initDomain() {
        loadingDialog.show();
        OkGo.get(Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=getDomain&sessionkey=" + SceoUtil.getSessionKey(getContext()))
                .tag(this)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        Domain.Lists data = SceoUtil.parseJson(s, Domain.Lists.class);

                        if (data.getTotal() > 0) {
                            domains = data.getRows();
                            showDomain();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

    private void showDomain() {
        changeDomainLayout.removeViews(1, changeDomainLayout.getChildCount() - 1);
        for (final Domain.Lists.Item domain : domains) {
            if (TextUtils.isEmpty(domain.getGuid())) {
                if (SceoUtil.getDomainAcctid(getContext()) != SceoUtil.INT_NEGATIVE) {
                    if (domain.getAcctid() == SceoUtil.getDomainAcctid(getContext())) {
                        domain.setGuid(SceoUtil.getDomain(getContext()));
                    }
                } else {
                    if (domain.getAcctid() == domain.getCur_acctid()) {
                        SceoUtil.setDomainAcctid(getContext(), domain.getAcctid());
                        domain.setGuid(SceoUtil.getSessionKey(getContext()));
                    }
                }
            }

            if (domain.getAcctid() == SceoUtil.getDomainAcctid(getContext())) {
                tvDomain.setText(domain.getDomm_name());
            } else {
                Button button = new Button(getContext());
                button.setText("切换 " + domain.getDomm_name());
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(domain.getGuid())) {
                            loadingDialog.show();
                            OkGo.get(Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=setDomain&domainid=" + domain.getAcctid() + "&sessionkey=" + SceoUtil.getSessionKey(getContext()))
                                    .tag(this)
                                    .cacheMode(CacheMode.NO_CACHE)
                                    .execute(new StringCallback() {
                                        @Override
                                        public void onSuccess(String s, Call call, Response response) {
                                            Domain.Lists data = SceoUtil.parseJson(s, Domain.Lists.class);

                                            if (data.getTotal() > 0) {
                                                domain.setGuid(data.getRows().get(0).getGuid());
                                                SceoUtil.setDomain(getContext(), data.getRows().get(0).getGuid());
                                                SceoUtil.setDomainAcctid(getContext(), domain.getAcctid());
                                                showDomain();
                                            } else {
                                                Toasty.error(getContext(), "切换域失败");
                                            }
                                            loadingDialog.dismiss();
                                        }
                                    });
                        } else {
                            SceoUtil.setDomain(getContext(), domain.getGuid());
                            SceoUtil.setDomainAcctid(getContext(), domain.getAcctid());
                            showDomain();
                        }
                    }
                });
                changeDomainLayout.addView(button);
            }
        }
    }

    private void initData() {
        int parentId = getArguments().getInt("parentId");
        RealmResults<HomeMenuItem> results = realm.where(HomeMenuItem.class).equalTo("modt_parent_id", parentId).findAll();
        for (HomeMenuItem item : results) {
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
                switch (dataList.get(position).getSysCode()) {
                    case "wm16.ewp01aa"://外部条码转换
                        startActivity(new Intent(getContext(), ExternalBarConversionAActivity.class));
                        break;
                    case "wm16.ewp02aa"://IQC条码抽检
                        startActivity(new Intent(getContext(), IQCCheckActivity.class));
                        break;
                    case "wm16.ewp03aa"://条码复制
                        startActivity(new Intent(getContext(), BarRepairAActivity.class));
                        break;
                    case "wm16.ewp04aa"://个人条码打印
                        startActivity(new Intent(getContext(), PersonalPrintActivity.class));
                        break;
                    case "wm16.ewp05aa"://个人任务平台
                        startActivity(new Intent(getContext(), PersonalJobActivity.class));
                        break;
                    case "wm16.ewp06aa"://入库作业
                        startActivity(new Intent(getContext(), InputTaskNumActivity.class));
                        break;
                    case "wm16.ewp07aa"://出库作业
                        startActivity(new Intent(getContext(), OutputTaskNumActivity.class));
                        break;
                    case "wm16.ewp08aa"://储位调整
                        startActivity(new Intent(getContext(), AdjustOrderActivity.class));
                        break;
                    case "wm16.ewp09aa"://散箱作业
                        startActivity(new Intent(getContext(), ScanBoxNumActivity.class));
                        break;
                    case "wm16.ewp11aa"://部品条码打印
                        Toasty.normal(getActivity(), "敬请期待", Toast.LENGTH_SHORT).show();
                        break;
                    case "wm16.ewp12aa"://出库点货
                        startActivity(new Intent(getContext(), InputPickingNumActivity.class));
                        break;
                    case "wm16.ewp13aa"://条码拆分
                        startActivity(new Intent(getContext(), BarSplitAActivity.class));
                        break;
                    case "wm16.ewp14aa"://条码生成
                        startActivity(new Intent(getContext(), CreateQrActivity.class));
                        break;
                    case "wm16.ewp16aa"://收货点货
                        startActivity(new Intent(getContext(), ReceiptAActivity.class));
                        break;
                    case "wm16.ewp19aa"://制令入库
                        startActivity(new Intent(getContext(), OrderInstoreAActivity.class));
                        break;
                    case "wm16.ewp20aa"://库存查询
                        startActivity(new Intent(getContext(), StorageQueryAActivity.class));
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
