package com.huntkey.software.sceo.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.others.ClickLogTask;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;

/**
 * 自动升级服务(支持断点续传)
 * @author chenliang3
 *
 */
public class AppUpdateService extends Service {

	private String path;
	private HttpHandler handler;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String url = Conf.SERVICE_URL + "sceosrv/csp/" + Conf.APP_PREFIX + Conf.saveFileName;
		path = Environment.getExternalStorageDirectory() + Conf.savePath;
		final int id = (int) (Math.random() * 1024);
		final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		final NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
			.setContentTitle(Conf.saveFileName)
			.setSmallIcon(R.mipmap.ic_launcher);

		final String Targetversion = SceoUtil.shareGetString(getApplicationContext(), "targetVersion");
		if (SceoUtil.isAppInForeground(getApplicationContext())) {
			HttpUtils http = new HttpUtils();
			handler = http.download(url,
					path + Conf.saveFileName,
					false,// (true)如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将重新下载
					true,// 如果从请求返回信息中获取到文件名，下载完成后自动重命名
					new RequestCallBack<File>() {
				private Notification updateNotification/* = new Notification()*/;
				private PendingIntent updatePendingIntent;

				@Override
				public void onStart() {
					super.onStart();
				}

				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					builder.setProgress((int)total, (int)current, false);
					nm.notify(id, builder.build());
				}

				@Override
				public void onSuccess(ResponseInfo<File> responseInfo) {
					nm.cancel(id);

//					Uri uri = Uri.fromFile(responseInfo.result);
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
						installIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						Uri fileUri = FileProvider.getUriForFile(AppUpdateService.this, "com.huntkey.software.sceo.fileprovider", responseInfo.result);
						installIntent.setDataAndType(fileUri, "application/vnd.android.package-archive");
					}else {
						installIntent.setDataAndType(Uri.fromFile(responseInfo.result), "application/vnd.android.package-archive");
					}
					startActivity(installIntent);

					//传日志给服务器
					ClickLogTask clickLogTask = new ClickLogTask();
					clickLogTask.execute(getApplicationContext(), "APP升级-"+Targetversion, "EAA03AA", 5, 0);

					stopService(installIntent);
					stopSelf();
				}

				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					nm.cancel(id);

//					Intent errorIntent = new Intent(AppUpdateService.this, MainActivity.class);
//					updatePendingIntent = PendingIntent.getActivity(AppUpdateService.this, 0, errorIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//					updateNotification.flags |= Notification.FLAG_AUTO_CANCEL;
//					updateNotification.icon = R.drawable.ic_launcher_final;
//					updateNotification.setLatestEventInfo(AppUpdateService.this, 
//							Conf.saveFileName, "下载失败", updatePendingIntent);
					Notification.Builder builder = new Notification.Builder(AppUpdateService.this);
					builder.setContentTitle("下载失败");
					builder.setContentText("请检查您的网络并重试");
					builder.setSmallIcon(R.mipmap.ic_launcher);
					builder.setTicker("下载失败");
					builder.setAutoCancel(true);
					builder.setWhen(System.currentTimeMillis());
					builder.setContentIntent(updatePendingIntent);
					updateNotification = builder.build();

					nm.notify(0, updateNotification);

//					stopService(errorIntent);
					stopSelf();
				}
			});
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.cancel();
		stopSelf();
	}

}
