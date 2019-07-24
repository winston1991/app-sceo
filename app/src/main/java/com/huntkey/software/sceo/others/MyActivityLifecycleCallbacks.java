package com.huntkey.software.sceo.others;

import com.huntkey.software.sceo.ui.activity.setting.GestureVerifyActivity;
import com.huntkey.software.sceo.utils.SceoUtil;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/**
 * 生命周期回调
 * 监听从后台切换到前台
 * @author chenliang3
 *
 */
public class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks{

	private int foregroundActivities;
	private boolean hasSeenFirstActivity;
	private boolean isChangingConfiguration;
	
	private Context context;
	
	public MyActivityLifecycleCallbacks(Context context){
		this.context = context;
	}
	
	@Override
	public void onActivityCreated(Activity activity, Bundle bundle) {
		
	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		
	}

	@Override
	public void onActivityPaused(Activity activity) {
		
	}

	@Override
	public void onActivityResumed(Activity activity) {
		
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
		
	}

	@Override
	public void onActivityStarted(Activity activity) {
		foregroundActivities++;
		if (hasSeenFirstActivity && foregroundActivities == 1 && !isChangingConfiguration
				&& SceoUtil.getGesturePassword(context) != null && !"".equals(SceoUtil.getGesturePassword(context))
				&& !SceoUtil.isActivityRunning(context, "com.huntkey.software.sceo.ui.activity.setting.GestureVerifyActivity")
				&& !SceoUtil.isActivityRunning(context, "com.huntkey.software.sceo.ui.activity.splash.SplashActivity")
				&& !SceoUtil.isActivityRunning(context, "com.huntkey.software.sceo.ui.activity.login.LoginActivity")) {
			Intent intent = new Intent(context, GestureVerifyActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
		hasSeenFirstActivity = true;
		isChangingConfiguration = false;
	}

	@Override
	public void onActivityStopped(Activity activity) {
		foregroundActivities--;
		if (foregroundActivities == 0) {
			
		}
		isChangingConfiguration = activity.isChangingConfigurations();
	}

}
