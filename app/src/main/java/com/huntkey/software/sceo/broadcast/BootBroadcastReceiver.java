package com.huntkey.software.sceo.broadcast;

import com.huntkey.software.sceo.service.NotifyService;
import com.huntkey.software.sceo.utils.SceoUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * 监听开机广播
 * @author chenliang3
 *
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//监听手机开机状态
//		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()) && "1".equals(SceoUtil.getAppFlag(context))) {
//			Intent startServiceIntent = new Intent(context, NotifyService.class);
//			startServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			context.startService(startServiceIntent);
//		}
//
//		if (Intent.ACTION_USER_PRESENT.equals(intent.getAction()) && "1".equals(SceoUtil.getAppFlag(context))) {
//			if (!SceoUtil.isServiceRunning(context, "com.huntkey.software.sceo.service.NotifyService")) {
//				Intent startServiceIntent = new Intent(context, NotifyService.class);
//				startServiceIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				context.startService(startServiceIntent);
//			}
//		}
	}

}
