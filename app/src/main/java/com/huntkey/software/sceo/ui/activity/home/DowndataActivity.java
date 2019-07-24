package com.huntkey.software.sceo.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.entity.HomeMenuData;
import com.huntkey.software.sceo.entity.HomeMenuItem;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;

import butterknife.BindView;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by chenl on 2017/4/7.
 */

public class DowndataActivity extends KnifeBaseActivity {

    @BindView(R.id.downdata_status)
    TextView statusTv;
    @BindView(R.id.downdata_imageview)
    ImageView imageView;
    @BindView(R.id.downdata_shimmer)
    ShimmerFrameLayout shimmerFrameLayout;

    private Realm realm;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_downdata;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        initNetwork();
        realm = Realm.getDefaultInstance();
    }

    private void initNetwork() {
        OkGo.get(Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=getMenuData" + "&sessionkey=" + SceoUtil.getSessionKey(this))
                .tag(this)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            HomeMenuData data = SceoUtil.parseJson(s, HomeMenuData.class);

                            SceoUtil.setKeyboardPermission(DowndataActivity.this, data.getRights());

                            if (data.getRows().size() > 0){
                                realm.beginTransaction();
                                //先删除
                                realm.where(HomeMenuItem.class).findAll().deleteAllFromRealm();

                                for (HomeMenuItem item : data.getRows()){
                                    realm.copyToRealmOrUpdate(item);
                                }
                                realm.commitTransaction();

                                Intent intent = new Intent(DowndataActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                imageView.setVisibility(View.GONE);
                                statusTv.setText("您还没有菜单权限");
                            }
                        }catch (Exception e){
                            imageView.setVisibility(View.GONE);
                            statusTv.setText("您暂未开通此功能的使用权限");
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        shimmerFrameLayout.useDefaults();
        shimmerFrameLayout.setAngle(ShimmerFrameLayout.MaskAngle.CW_0);
    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmerAnimation();
    }

    @Override
    public void onPause() {
        super.onPause();
        shimmerFrameLayout.stopShimmerAnimation();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
