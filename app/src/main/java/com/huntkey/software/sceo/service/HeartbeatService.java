package com.huntkey.software.sceo.service;

import java.io.Serializable;
import java.util.List;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.bean.ChatData;
import com.huntkey.software.sceo.bean.ChatDetails;
import com.huntkey.software.sceo.bean.RemindData;
import com.huntkey.software.sceo.cache.ACache;
import com.huntkey.software.sceo.utils.LogUtil;
import com.huntkey.software.sceo.utils.NetWorkUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 向服务器发送心跳包
 * 保持唤醒状态且不会被kill
 * 
 * @author chenliang3
 *
 */
public class HeartbeatService extends Service {

	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;
	private int interval;//时间间隔
	private ACache aCache;
	private DbUtils db;
	int maxId;
	
	@Override
	public void onCreate() {
		interval = SceoUtil.getInterval(getApplicationContext());//6s(可改变)
		
		Intent intent = new Intent(getApplicationContext(), HeartbeatService.class);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long now = System.currentTimeMillis();
		alarmManager.setInexactRepeating(AlarmManager.RTC, now, interval, pendingIntent);//interval即为间隔时间
		
		aCache = ACache.get(getApplicationContext());
//		db = DbUtils.create(getApplicationContext());
		db = DbUtils.create(getApplicationContext(), "chat" + SceoUtil.getAcctid(this) + SceoUtil.getEmpCode(this));
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//执行请求
		try {
			checkChatInfo();
			checkAffairsInfo();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		LogUtil.i("heartBeatService is running");
		
		return START_STICKY;//当service被系统终止时，START_STICKY会重启该service，START_NOT_STICKY不会重启
	}
	
	/**
	 * 当chatActivity可见时，通知消息更新
	 */
	private void checkChatInfo(){
		if (NetWorkUtil.networkCanUse(getApplicationContext())) {
			if (SceoUtil.isActivityRunning(getApplicationContext(), 
					"com.huntkey.software.sceo.ui.activity.chat.ChatActivity")) {//判断chatActivity是否可见
				if (SceoUtil.getCanServiceLoading(getApplicationContext())) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							String taskId = aCache.getAsString("tempTaskId");
							try {
								List<ChatDetails> list = db.findAll(Selector.from(ChatDetails.class)
										.where("taskid","=",taskId).orderBy("subtaskid"));
								if (list != null && list.size() > 0) {				
									maxId = list.get(list.size() -1).getSubtaskid();
								}
							} catch (DbException e) {
								e.printStackTrace();
							}
							RequestParams params = new RequestParams();
							params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getApplicationContext()));
							params.addBodyParameter("taskid", taskId);
							params.addBodyParameter("newflag", "1");
							params.addBodyParameter("maxid", maxId+"");
							params.addBodyParameter("pageno", "1");
							params.addBodyParameter("pagesize", "20");
							HttpUtils http = new HttpUtils();
							http.configResponseTextCharset("GB2312");
							http.send(HttpRequest.HttpMethod.POST, 
									SceoUtil.getServiceUrl(getApplicationContext()) + "page=EAA05AB&pcmd=getTaskInfo", 
									params, 
									new RequestCallBack<String>() {
								
								@Override
								public void onFailure(HttpException arg0,
										String arg1) {
									
								}
								
								@Override
								public void onSuccess(ResponseInfo<String> responseInfo) {
									ChatData data = SceoUtil.parseJson(responseInfo.result, ChatData.class);
									if (data.getStatus() == 0) {
										List<ChatDetails> details = data.getData().getTaskinfo();
										if (details != null && details.size() > 0) {								
											LogUtil.i("heartBeatService-->"+details.get(details.size()-1).getTaskid());
											for (int i = 0; i < details.size(); i++) {
												details.get(i).setMsgresult(1);//获得的数据消息状态全为成功
											}
										}
										Intent intent = new Intent();
										intent.putExtra("chatDetails", (Serializable)details);
										intent.setAction(SceoUtil.ACTION_CHAT_MESSAGE_RECEIVER);
										sendBroadcast(intent);
									}
								}
							});
						}
					}).start();
				}
			}
		}
	}
	
	/**
	 * 当MainActivity可见时，通知affairs消息更新，给invoice新消息提醒
	 */
	private void checkAffairsInfo(){
		if (NetWorkUtil.networkCanUse(getApplicationContext())) {
			if (SceoUtil.isActivityRunning(getApplicationContext(), 
					"com.huntkey.software.sceo.ui.activity.MainActivity")) { //判断mainActivity是否在当前界面
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						RequestParams params = new RequestParams();
						params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getApplicationContext()));
						HttpUtils http = new HttpUtils();
						http.configResponseTextCharset("GB2312");
						http.send(HttpRequest.HttpMethod.POST, 
								Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA03AA&pcmd=getWarnTips",
								params, 
								new RequestCallBack<String>() {
							
							@Override
							public void onFailure(HttpException arg0, String arg1) {
								
							}
							
							@Override
							public void onSuccess(ResponseInfo<String> responseInfo) {
								RemindData data = SceoUtil.parseJson(responseInfo.result, RemindData.class);
								String tmpAffairsNo = aCache.getAsString("affairsUnReadNo");
								String tmpInvoiceNo = aCache.getAsString("invoiceUnReadNo");
								
								Intent intent = new Intent();
								if (data != null && data.getStatus() == 0) {
									String meTaskNo = data.getData().getMetask();//待办事务
									if (meTaskNo == null || "".equals(meTaskNo)) {
										meTaskNo = "0";
									}
									if (Integer.parseInt(meTaskNo) > Integer.parseInt(tmpAffairsNo)) {
										intent.putExtra("refreshFlag", true);
										intent.setAction(SceoUtil.ACTION_AFFAIRS_REFRESH_RECEIVER);
										sendBroadcast(intent);
									}
									
//							String wfTaskNo = data.getData().getWftask();//待审单据
//							if (Integer.parseInt(wfTaskNo) > Integer.parseInt(tmpInvoiceNo)) {
//								intent.putExtra("gotNews", true);
//								intent.setAction(SceoUtil.ACTION_INVOICE_GOT_NEWS);
//								sendBroadcast(intent);
//							}
								}
							}
						});
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

}
