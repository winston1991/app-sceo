//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//                              _oo0oo_
//                             o8888888o
//                             88" . "88
//                             (| -_- |)
//                             0\  =  /0
//                           ___/`---‘\___
//                        .' \\\|     |// '.
//                       / \\\|||  :  |||// \\
//                      / _ ||||| -:- |||||- \\
//                      | |  \\\\  -  /// |   |
//                      | \_|  ''\---/''  |_/ |
//                      \  .-\__  '-'  __/-.  /
//                    ___'. .'  /--.--\  '. .'___
//                 ."" '<  '.___\_<|>_/___.' >'  "".
//                | | : '-  \'.;'\ _ /';.'/ - ' : | |
//                \  \ '_.   \_ __\ /__ _/   .-' /  /
//            ====='-.____'.___ \_____/___.-'____.-'=====
//                              '=---='
//
//
//          ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
//
//                         佛祖保佑                 永无BUG
package com.huntkey.software.sceo.base;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.others.ScreenObserver;
import com.huntkey.software.sceo.others.ScreenObserver.ScreenStateListener;
import com.huntkey.software.sceo.others.SystemBarTintManager;
import com.huntkey.software.sceo.service.TimeoutService;
import com.huntkey.software.sceo.utils.NetWorkUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ErrorView;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.view.WindowManager;

import hei.permission.PermissionActivity;

/**
 * 
 * @author chenliang3
 *
 */
public abstract class BaseActivity extends PermissionActivity {

	private SystemBarTintManager mTintManager;//沉浸效果管理
	private boolean mSystemBarTint = true;
	private ScreenObserver mScreenObserver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		SceoApplication.getInstance().addActivity(this);

		mScreenObserver = new ScreenObserver(this);
		mScreenObserver.requestScreenStateUpdate(new ScreenStateListener() {

			@Override
			public void onScreenOn() {
				if (!ScreenObserver.isApplicationBroughtToBackground(BaseActivity.this)) {
					cancelAlarmManager();
				}
			}

			@Override
			public void onScreenOff() {
				if (!ScreenObserver.isApplicationBroughtToBackground(BaseActivity.this)) {
					cancelAlarmManager();
					setAlarmManager();
				}
			}
		});
	}
	
	public boolean hasNetWork(){
		return NetWorkUtil.networkCanUse(BaseActivity.this);
	}
	
	/**
	 * 显示errorView(错误视图、空视图、没有网络视图)
	 * @param view
	 * @param drawable
	 * @param title
	 * @param content
	 */
	public void showErrorView(ErrorView view, Drawable drawable, String title, String content){
		view.setVisibility(View.VISIBLE);
		view.setImage(drawable);
		view.setTitle(title);
		view.setSubtitle(content);
	}
	
	/**
	 * 隐藏errorView
	 * @param view
	 */
	public void hideErrorView(ErrorView view){
		view.setVisibility(View.GONE);
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
	public void setContentView(View view, LayoutParams params) {
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
	
	/**
	 * 隐藏输入法
	 * @param view
	 */
	public void hideSoftInput(View view){
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		cancelAlarmManager();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (ScreenObserver.isApplicationBroughtToBackground(this)) {
			cancelAlarmManager();
			setAlarmManager();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mScreenObserver.stopScreenStateUpdate();
	}
	
	/**
	 * 设置定时器管理器
	 */
	private void setAlarmManager(){
		long numTimeout = SceoUtil.getOutTimeExit(BaseActivity.this);//超时时间
		Intent alarmIntent = new Intent(BaseActivity.this, TimeoutService.class);
		alarmIntent.putExtra("action", "timeout");//自定义参数
		PendingIntent pi = PendingIntent.getService(BaseActivity.this, 1024, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long triggerAtTime = (System.currentTimeMillis() + numTimeout);
		am.set(AlarmManager.RTC_WAKEUP, triggerAtTime, pi);//设定的一次性闹钟，这里决定是否使用绝对时间
	}
	
	/**
	 * 取消定时管理器
	 */
	private void cancelAlarmManager(){
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(BaseActivity.this, TimeoutService.class);
		PendingIntent pi = PendingIntent.getService(BaseActivity.this, 1024, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//与上面的intent匹配(filterEquals(intent))的闹钟会被取消
		alarmManager.cancel(pi);
	}
	
}
