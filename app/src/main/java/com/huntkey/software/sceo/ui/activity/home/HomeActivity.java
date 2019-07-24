package com.huntkey.software.sceo.ui.activity.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.KnifeBaseActivity;
import com.huntkey.software.sceo.bean.VersionData;
import com.huntkey.software.sceo.entity.HomeMenuItem;
import com.huntkey.software.sceo.others.ClickLogTask;
import com.huntkey.software.sceo.service.AppUpdateService;
import com.huntkey.software.sceo.ui.activity.MainActivity;
import com.huntkey.software.sceo.ui.fragment.home.PersonalFragment;
import com.huntkey.software.sceo.ui.fragment.home.PurchaseFragment;
import com.huntkey.software.sceo.ui.fragment.home.StorageFragment;
import com.huntkey.software.sceo.utils.HProgressDialogUtils;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.Settings;
import com.huntkey.software.sceo.utils.UpdateAppHttpUtil;
import com.huntkey.software.sceo.widget.bottomtab.BottomTabLayout;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.service.DownloadService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by chenl on 2017/3/28.
 */

public class HomeActivity extends KnifeBaseActivity {

    @BindView(R.id.sh_bootom_tablayout)
    BottomTabLayout bottomTabLayout;
    @BindView(R.id.sh_viewpager)
    ViewPager viewPager;

    private List<Fragment> fragmentList = new ArrayList<>();
    private boolean isAppFlag;//是否绑定
    private Intent updateIntent;

    private Realm realm;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        realm = Realm.getDefaultInstance();
        initViewPager();
        if ("0".equals(SceoUtil.getAppFlag(this))) {
            isAppFlag = true;
        } else {
            isAppFlag = false;
        }
        checkVersionName();
    }

    private void initViewPager() {
        StorageFragment storageFragment = new StorageFragment();
        PurchaseFragment purchaseFragment = new PurchaseFragment();
        PersonalFragment personalFragment = new PersonalFragment();

        RealmResults<HomeMenuItem> results = realm.where(HomeMenuItem.class).equalTo("level", 2).findAll();
        for (HomeMenuItem item : results) {
            if ("dkt.ckgl".equals(item.getModt_syscode())) {
                fragmentList.add(storageFragment);
                Bundle storBundle = new Bundle();
                storBundle.putString("name", item.getModt_name());
                storBundle.putInt("parentId", item.getModt_id());
                storageFragment.setArguments(storBundle);
            } else if ("dkt.sjgl".equals(item.getModt_syscode())) {
                fragmentList.add(purchaseFragment);
                Bundle purBundle = new Bundle();
                purBundle.putString("name", item.getModt_name());
                purBundle.putInt("parentId", item.getModt_id());
                purchaseFragment.setArguments(purBundle);
            } else if ("dkt.kqgl".equals(item.getModt_syscode())) {
                fragmentList.add(personalFragment);
                Bundle purBundle = new Bundle();
                purBundle.putString("name", item.getModt_name());
                purBundle.putInt("parentId", item.getModt_id());
                personalFragment.setArguments(purBundle);
            }
        }

        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        bottomTabLayout.setViewPager(viewPager);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                bottomTabLayout.setFlagNum(1, "1");
//            }
//        }, 2000);
    }

    private class MyFragmentAdapter extends FragmentPagerAdapter {

        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //将super注释掉，以解决viewpager+fragment出现的重复加载数据的情况
            //super.destroyItem(container, position, object);
        }
    }

    /**
     * 双击退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (isAppFlag && keyCode == KeyEvent.KEYCODE_BACK) {
            new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("确认退出？")
                    .setCancelText("取消")
                    .setConfirmText("确定")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {

                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            ClickLogTask clickLogTask5 = new ClickLogTask();
                            clickLogTask5.execute(HomeActivity.this, "退出系统", "", 3, 1);

                            sweetAlertDialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        } else {
            finish();
        }
        return false;
    }

    /**
     * 版本检测
     */
    private VersionData data;

    private void checkVersionName() {
        RequestParams params = new RequestParams();
        params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(HomeActivity.this));
        params.addBodyParameter("type", "android");
        params.addBodyParameter("versionno", Settings.getVersionName(HomeActivity.this));
        params.addBodyParameter("veracctid", SceoUtil.getAcctid(HomeActivity.this) + "");
        HttpUtils http = new HttpUtils();
        http.configResponseTextCharset("GB2312");
        http.send(HttpRequest.HttpMethod.POST,
                Conf.g_SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA03AA&pcmd=getVersion",
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onFailure(HttpException arg0, String arg1) {

                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
                        data = SceoUtil.parseJson(responseInfo.result, VersionData.class);
                        if (data.getStatus() == 0) {
                            if (data.getData() != null && data.getData().getAppurl() != null) {
                                SceoUtil.setHasNewVersion(HomeActivity.this, true);
                                SceoUtil.shareSet(HomeActivity.this, "downloadVisibleControl", true);

                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (!isFinishing()) {    //判断一下activity是否还存在--这里也可进行抛异常处理
                                            new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.NORMAL_TYPE)
                                                    .setTitleText("发现新版本，是否更新？")
                                                    .setContentText(data.getData().getDesc())
                                                    .setCancelText("暂不更新")
                                                    .setConfirmText("立即更新")
                                                    .showCancelButton(true)
                                                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {

                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismiss();
                                                        }
                                                    })
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {

                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            // updateIntent = new Intent(HomeActivity.this,
                                                            //         AppUpdateService.class);
                                                            // startService(updateIntent);
                                                            // SceoUtil.shareSet(HomeActivity.this, "targetVersion", data.getData().getVersion());
                                                            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                                                //没有权限则申请权限
                                                                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                                            } else {
                                                                updateAPK();
                                                            }
                                                            sweetAlertDialog.dismiss();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                }, 4000);

                            } else {
                                SceoUtil.setHasNewVersion(HomeActivity.this, false);
                                SceoUtil.shareSet(HomeActivity.this, "downloadVisibleControl", false);
                            }
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //执行代码,这里是已经申请权限成功了,可以不用做处理
                    updateAPK();
                } else {
                    Toast.makeText(HomeActivity.this, "权限申请失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void updateAPK() {
        // updateIntent = new Intent(HomeActivity.this,
        //         AppUpdateService.class);
        // startService(updateIntent);
        SceoUtil.shareSet(HomeActivity.this, "targetVersion", data.getData().getVersion());

        UpdateAppBean updateAppBean = new UpdateAppBean();
        //设置 apk 的下载地址
        // updateAppBean.setApkFileUrl(Conf.SERVICE_URL + "sceosrv/csp/" + Conf.APP_PREFIX + Conf.saveFileName);
        updateAppBean.setApkFileUrl("https://app.huntkey.com/static/sceo.apk");

        //设置apk 的保存路径
        updateAppBean.setTargetPath(Environment.getExternalStorageDirectory() + Conf.savePath);
        //实现网络接口，只实现下载就可以
        updateAppBean.setHttpManager(new UpdateAppHttpUtil());

        UpdateAppManager.download(this, updateAppBean, new DownloadService.DownloadCallback() {
            @Override
            public void onStart() {
                HProgressDialogUtils.showHorizontalProgressDialog(HomeActivity.this, "下载进度", false);
            }

            @Override
            public void onProgress(float progress, long totalSize) {
                HProgressDialogUtils.setProgress(Math.round(progress * 100));

            }

            @Override
            public void setMax(long totalSize) {
            }

            @Override
            public boolean onFinish(File file) {
                HProgressDialogUtils.cancel();
                return true;
            }

            @Override
            public void onError(String msg) {
                HProgressDialogUtils.cancel();
            }

            @Override
            public boolean onInstallAppAndAppOnForeground(File file) {
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
