package com.huntkey.software.sceo.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.RemindData;
import com.huntkey.software.sceo.cache.ACache;
import com.huntkey.software.sceo.ui.activity.login.LoginActivity;
import com.huntkey.software.sceo.utils.NetWorkUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.RemoteViews;

/**
 * notify提醒
 * @author chenliang3
 *
 */
public class NotifyService extends Service {

	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private int interval;//时间间隔
	private ACache aCache;
	private Notification notification;
	private static int count;
	private NotificationManager notificationManager;
	private boolean isFirst = true;
	
	@Override
	public void onCreate() {
		aCache = ACache.get(getApplicationContext());
		interval = SceoUtil.getInterval2(getApplicationContext());//10min
		
		Intent intent = new Intent(getApplicationContext(), NotifyService.class);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long now = System.currentTimeMillis();
		alarmManager.setInexactRepeating(AlarmManager.RTC, now, interval, pendingIntent);//interval即为间隔时间
		
		super.onCreate();
//		startForeground(1, new Notification());
		setForeground();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
//			checkUnReadNo();
		if (count == 900) {
			count = 0;
			checkUnReadNo();
		}else {
			count++;
			
			if (isFirst) {
				setNotify();
				isFirst = false;
			}
			if (SceoUtil.isAppRunning(getApplicationContext())) {					
				setNotify();
			}
			
		}
		
//		LogUtil.i("notifyService is running");
		
		return START_STICKY;//当service被系统终止时，START_STICKY会重启该service，START_NOT_STICKY不会重启
	}
	
	private void setNotify(){
		String tmpAffairsNo = aCache.getAsString("affairsUnReadNo");
		String tmpInvoiceNo = aCache.getAsString("invoiceUnReadNo");
		notification.contentView.setTextViewText(R.id.fore_notify_invoice_tv, tmpInvoiceNo);
		notification.contentView.setTextViewText(R.id.fore_notif_affair_tv, tmpAffairsNo);
		
		Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent1, 0);
		notification.contentView.setOnClickPendingIntent(R.id.fore_notif_invoice_layout, pendingIntent);
		
		Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
		intent2.putExtra("showFlag", 1);
		PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);
		notification.contentView.setOnClickPendingIntent(R.id.fore_notif_affair_layout, pendingIntent2);
		
		notificationManager.notify(1, notification);
	}
	
	/**
	 * 未读消息个数
	 */
	private void checkUnReadNo(){
		if (NetWorkUtil.networkCanUse(getApplicationContext())) {
			if (!SceoUtil.isAppRunning(getApplicationContext())) { //判断app是否在运行
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						if (!TextUtils.isEmpty(SceoUtil.getEmpCode(getApplicationContext()))) {
							RequestParams params = new RequestParams();
//							params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getApplicationContext()));
							params.addBodyParameter("emp_id", SceoUtil.getEmpCode(getApplicationContext()));
							params.addBodyParameter("loginflag", "-1");
							params.addBodyParameter("uid", SceoUtil.getDeviceId(getApplicationContext()));
							params.addBodyParameter("HostName", android.os.Build.MODEL);
							params.addBodyParameter("SysVer", android.os.Build.VERSION.RELEASE);
							HttpUtils http = new HttpUtils();
							http.configResponseTextCharset("GB2312");
							http.send(HttpRequest.HttpMethod.POST, 
									SceoUtil.getServiceUrl(getApplicationContext()) + "page=EAA03AA&pcmd=getWarnTips", 
									params, 
									new RequestCallBack<String>() {
								
								@Override
								public void onFailure(HttpException arg0, String arg1) {
									
								}
								
								@Override
								public void onSuccess(ResponseInfo<String> responseInfo) {
									try {
										RemindData data = SceoUtil.parseJson(responseInfo.result, RemindData.class);
//									String tmpAffairsNo = aCache.getAsString("affairsUnReadNo");
//									String tmpInvoiceNo = aCache.getAsString("invoiceUnReadNo");
										
//									//应用关闭后，发送通知到通知栏，点击通知栏能跳转到相应页面
//									Intent niftyIntent = new Intent();
//									niftyIntent.addCategory(Intent.CATEGORY_DEFAULT);
//									niftyIntent.setComponent(new ComponentName(getPackageName(), "com.huntkey.software.sceo.ui.activity.login.LoginActivity"));
//									niftyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
										if (data.getStatus() == 0) {	
											String meTaskNo = data.getData().getMetask();//待办事务
											String wfTaskNo = data.getData().getWftask();
//										if (meTaskNo == null || "".equals(meTaskNo)) {
//											meTaskNo = "0";
//										}
//										if (wfTaskNo == null || "".equals(wfTaskNo)) {
//											wfTaskNo = "0";
//										}
//										
//										if (Integer.parseInt(meTaskNo) > Integer.parseInt(tmpAffairsNo)) {
//											niftyIntent.putExtra("showFlag", 1);//通过通知栏传递信息给MianActivity，显示待审单据/待办事务的flag
//											SceoUtil.sendLocatNotification(getApplicationContext(), "您有"+meTaskNo+"条新的待办事务", "点击查看", niftyIntent, 0x111);
//											aCache.put("affairsUnReadNo", meTaskNo);
//										}
//										if (Integer.parseInt(wfTaskNo) > Integer.parseInt(tmpInvoiceNo)) {
//											SceoUtil.sendLocatNotification(getApplicationContext(), "您有"+wfTaskNo+"条新的待审单据", "点击查看", niftyIntent, 0x112);
//											aCache.put("invoiceUnReadNo", wfTaskNo);
//										}
											notification.contentView.setTextViewText(R.id.fore_notify_invoice_tv, wfTaskNo);
											notification.contentView.setTextViewText(R.id.fore_notif_affair_tv, meTaskNo);
											notification.contentView.setTextViewText(R.id.fore_notif_time, new SimpleDateFormat("HH:mm").format(new Date()));
											
											Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
											PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
											notification.contentView.setOnClickPendingIntent(R.id.fore_notif_invoice_layout, pendingIntent);
											
											Intent intent2 = new Intent(getApplicationContext(), LoginActivity.class);
											intent2.putExtra("showFlag", 1);
											PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent2, 0);
											notification.contentView.setOnClickPendingIntent(R.id.fore_notif_affair_layout, pendingIntent2);
											notificationManager.notify(1, notification);
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
									
								}
							});
						}
					}
				}).start();
			}
		}
	}	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private void setForeground(){
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		notification = new Notification(R.mipmap.ic_launcher, "", System.currentTimeMillis());
		notification.contentView = new RemoteViews(getPackageName(), R.layout.foreground_notif);
		
		startForeground(1, notification);
	}
	
}
