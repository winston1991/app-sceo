package com.huntkey.software.sceo.others;

import java.lang.reflect.Method;
import java.util.List;

import com.huntkey.software.sceo.utils.LogUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
/**
 * 长时间未操作自动退出app
 * 1.锁屏问题，2.app被切换至后台问题，3.屏幕锁定和解除时app在后台时的问题
 * 
 * 监听屏幕解锁，锁定
 * @author chenliang3
 *
 */
public class ScreenObserver {

	private Context mContext;
	private ScreenBroadcastReceiver mScreenReceiver;
	private ScreenStateListener mScreenStateListener;
	private static Method mReflectScreenState;
	
	public ScreenObserver(Context context){
		mContext = context;
		mScreenReceiver = new ScreenBroadcastReceiver();
		try {
			mReflectScreenState = PowerManager.class.getMethod("isScreenOn", new Class[]{});
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * screen状态广播接收者
	 */
	private class ScreenBroadcastReceiver extends BroadcastReceiver{

		private String action = null;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			action = intent.getAction();
			if (Intent.ACTION_SCREEN_ON.equals(action)) {
				mScreenStateListener.onScreenOn();
			}else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
				mScreenStateListener.onScreenOff();
			}else if (Intent.ACTION_USER_PRESENT.equals(action)) {
				LogUtil.d("屏幕解锁");
			}
		}
		
	}
	
	/**
	 * 请求screen状态更新
	 */
	public void requestScreenStateUpdate(ScreenStateListener listener){
		mScreenStateListener = listener;
		startScreenBroadcastReceiver();
		firstGetScreenState();
	}
	
	/**
	 * 第一次请求screen状态
	 */
	private void firstGetScreenState(){
		PowerManager manager = (PowerManager) mContext.getSystemService(Activity.POWER_SERVICE);
		if (isScreenOn(manager)) {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenOn();
			}
		}else {
			if (mScreenStateListener != null) {
				mScreenStateListener.onScreenOff();
			}
		}
	}
	
	/**
	 * 停止screen状态更新
	 */
	public void stopScreenStateUpdate(){
		mContext.unregisterReceiver(mScreenReceiver);
	}
	
	/**
	 * 启动screen状态广播接收器
	 */
	private void startScreenBroadcastReceiver(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		//当用户解锁屏幕时
		filter.addAction(Intent.ACTION_USER_PRESENT);
		mContext.registerReceiver(mScreenReceiver, filter);
	}
	
	/**
	 * screen是否打开状态
	 */
	private static boolean isScreenOn(PowerManager pm){
		boolean screenState;
		try {
			screenState = (boolean) mReflectScreenState.invoke(pm);
		} catch (Exception e) {
			screenState = false;
		}
		return screenState;
	}
	
	public interface ScreenStateListener{
		public void onScreenOn();
		public void onScreenOff();
	}
	
	/**
	 * 判断屏幕是否已被锁定
	 */
	public final static boolean isScreenLocked(Context c){
		KeyguardManager mKeyguardManager = (KeyguardManager) c.getSystemService(Context.KEYGUARD_SERVICE);
		return mKeyguardManager.inKeyguardRestrictedInputMode();
	}
	
	/**
	 * 判断当前应用是否是本应用
	 */
	public static boolean isApplicationBroughtToBackground(final Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> tasks = am.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			if (!topActivity.getPackageName().equals(context.getPackageName())) {
				return true;
			}
		}
		return false;
	}
	
}
