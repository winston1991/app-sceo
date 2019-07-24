package com.huntkey.software.sceo.service;

import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.others.ClickLogTask;
import com.huntkey.software.sceo.ui.activity.login.LoginActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog.OnSweetClickListener;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

public class TimeoutService extends Service{

	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				SceoApplication.getInstance().exit();
				
				ClickLogTask clickLogTask5 = new ClickLogTask();
				if (SceoUtil.getOutTimeExit(getApplicationContext())/1000 < 60) {					
					clickLogTask5.execute(getApplicationContext(), "超时退出("+SceoUtil.getOutTimeExit(getApplicationContext())/1000+"秒)", "", 3, 0);
				}else {
					clickLogTask5.execute(getApplicationContext(), "超时退出("+SceoUtil.getOutTimeExit(getApplicationContext())/60000+"分)", "", 3, 0);
				}
//				final long showTime = System.currentTimeMillis();
				
//				SweetAlertDialog dialog = new SweetAlertDialog(getApplicationContext(), SweetAlertDialog.WARNING_TYPE);
//				dialog.setTitleText("SCEO提示您：");
//				dialog.setContentText("超过5分钟未操作");
//				dialog.setCancelText("重新登录");
//				dialog.setConfirmText("确定退出");
//				dialog.showCancelButton(true);
//				dialog.setCancelClickListener(new OnSweetClickListener() {
//					
//					@Override
//					public void onClick(SweetAlertDialog sweetAlertDialog) {
//						sweetAlertDialog.dismiss();
//						Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						startActivity(intent);
//						
//						long clickTime = System.currentTimeMillis();
//						ClickLogTask clickLogTask5 = new ClickLogTask();
//						clickLogTask5.execute(getApplicationContext(), "超时退出("+(int)((clickTime-showTime)/60000+5)+"分钟)", "", 3, 0);
//					}
//				});
//				dialog.setConfirmClickListener(new OnSweetClickListener() {
//					
//					@Override
//					public void onClick(SweetAlertDialog sweetAlertDialog) {
//						sweetAlertDialog.dismiss();
//						
//						long clickTime = System.currentTimeMillis();
//						ClickLogTask clickLogTask5 = new ClickLogTask();
//						clickLogTask5.execute(getApplicationContext(), "超时退出("+(int)((clickTime-showTime)/60000+5)+"分钟)", "", 3, 0);
//					}
//				});
//				dialog.setSystemType(true);
//				dialog.show();
			}
		};
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		forceApplicationExit();
		return super.onStartCommand(intent, flags, startId);
	}
	
	private void forceApplicationExit(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
//				System.exit(0);
				Message msg = new Message();
				msg.what = 1;
				mHandler.sendMessage(msg);
				
//				SceoApplication.getInstance().exit();
				stopSelf();
			}
		}).start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	

}
