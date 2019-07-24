package com.huntkey.software.sceo.base;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.others.SystemBarTintManager;
import com.huntkey.software.sceo.utils.NetWorkUtil;
import com.lzy.okgo.OkGo;

import butterknife.ButterKnife;
import hei.permission.PermissionActivity;

/**
 * Created by chenl on 2017/3/28.
 */

public abstract class KnifeBaseActivity extends PermissionActivity {

    private SystemBarTintManager mTintManager;//沉浸效果管理
    private boolean mSystemBarTint = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        ButterKnife.bind(this);
        initViews(savedInstanceState);
        AppManager.getInstance().addActivity(this);
    }

    protected void setToolBar(Toolbar toolBar, String title){
        toolBar.setTitle(title);
        toolBar.setTitleTextColor(getResources().getColor(R.color.white));
        toolBar.setNavigationIcon(R.drawable.ic_back_arrow);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public boolean hasNetWork(){
        return NetWorkUtil.networkCanUse(KnifeBaseActivity.this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        injectContent();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        injectContent();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        injectContent();
    }

    private void injectContent(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            if (mSystemBarTint) {
                mTintManager = new SystemBarTintManager(this);
                mTintManager.setStatusBarTintEnabled(true);
                mTintManager.setNavigationBarTintEnabled(true);
                mTintManager.setStatusBarTintColor(getResources().getColor(R.color.title_devide_line));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on){
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 是否开启沉浸效果
     * 默认开启--在setContentView之前引用
     */
    public void setSystemBarTint(boolean mSystemBarTint){
        this.mSystemBarTint = mSystemBarTint;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkGo.getInstance().cancelAll();//取消网络请求
    }

    @Override
    public void finish() {
        super.finish();
        AppManager.getInstance().removeActivity(this);
    }

    protected abstract int getContentViewId();
    protected abstract void initViews(Bundle savedInstanceState);
}
