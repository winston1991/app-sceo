package com.huntkey.software.sceo.ui.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.bean.RemindData;
import com.huntkey.software.sceo.bean.VersionData;
import com.huntkey.software.sceo.cache.ACache;
import com.huntkey.software.sceo.others.ClickLogTask;
import com.huntkey.software.sceo.service.HeartbeatService;
import com.huntkey.software.sceo.ui.fragment.AffairsFragment;
import com.huntkey.software.sceo.ui.fragment.InvoiceFragment;
import com.huntkey.software.sceo.ui.fragment.MeFragment;
import com.huntkey.software.sceo.ui.fragment.NavFragment;
import com.huntkey.software.sceo.utils.HProgressDialogUtils;
import com.huntkey.software.sceo.utils.NetWorkUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.Settings;
import com.huntkey.software.sceo.utils.UpdateAppHttpUtil;
import com.huntkey.software.sceo.widget.FootNavigationView;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog.OnSweetClickListener;
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
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

/**
 * 主界面
 * @author chenliang3
 *
 */
public class MainActivity extends BaseActivity {
	private int REQUEST_WIFISET = 0;//请求码：设置wifi
	private PlaceholderFragment holderFragment;
	private Intent heartbeatIntent;
//	private Intent notifyIntent;
	private ACache aCache;
	private Intent updateIntent;
	private BroadcastReceiver broadcastReceiver;
	private int showFlag;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aCache = ACache.get(this);
        
        showFlag = getIntent().getIntExtra("showFlag", -1);//从通知栏获取的显示待审单据/待办事务的flag
        if (savedInstanceState == null) {
        	holderFragment = new PlaceholderFragment();
        	Bundle bundle = new Bundle();
        	bundle.putInt("showFlag", showFlag);
        	holderFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, holderFragment)
                    .commit();
        }
        
        checkWifi();//检查网络
        
//        //自动更新
//        new Handler().postDelayed(new Runnable() {
//			
//			@Override
//			public void run() {
				checkVersionName();
//			}
//		}, 4000);
		registerBroadCastReceiver();
    }

    public void setFormTxt(String txt) {
		holderFragment.setFormTxt(txt);
	}
    
    @Override
    protected void onResume() {
    	super.onResume();
    	updateUnReadCount();
    }
    
    private void registerBroadCastReceiver(){
    	broadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean hasNewVersion = intent.getBooleanExtra("hasNewVersionFlag", false);
				if (hasNewVersion) {					
					holderFragment.setMeRemind("1");
				}else {
					holderFragment.setMeRemind("0");
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SceoUtil.ACTION_HAS_NEW_VERSION);
		registerReceiver(broadcastReceiver, intentFilter);
    }
    
    /**
     * 更新未读消息条数
     */
    private void updateUnReadCount(){
    	RequestParams params = new RequestParams();
    	params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(MainActivity.this));
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
						if (data.getStatus() == 0) {
							String meTask = data.getData().getMetask();
							String wfTask = data.getData().getWftask();
							if (meTask == null || "".equals(meTask)) {
								meTask = "0";
							}
							if (wfTask == null || "".equals(wfTask)) {
								wfTask = "0";
							}
							holderFragment.setAffairsUnReadCount(meTask);
							holderFragment.setInvoiceUnReadCount(wfTask);
//							holderFragment.setMeRemind(data.getData().getWftask());
							aCache.put("affairsUnReadNo", meTask);
							aCache.put("invoiceUnReadNo", wfTask);
							
							//开启服务
					        heartbeatIntent = new Intent(MainActivity.this, HeartbeatService.class);
					        startService(heartbeatIntent);
//					        notifyIntent = new Intent(MainActivity.this, NotifyService.class);
//					        startService(notifyIntent);
						}
					}
				});
    }
    
    /**
     * 检查网络连接
     */
    private void checkWifi(){
		if (!NetWorkUtil.networkCanUse(MainActivity.this)) {
			new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("网络不可用")
				.setContentText("是否设置？")
				.setCancelText("暂不设置")
				.setConfirmText("现在设置")
				.showCancelButton(true)
				.setCancelClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						sweetAlertDialog.dismiss();
					}
				})
				.setConfirmClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
//						startActivity(intent);
						startActivityForResult(intent, REQUEST_WIFISET);
						sweetAlertDialog.dismiss();
					}
				})
				.show();
		}
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == REQUEST_WIFISET) {
    		//重新加载fragment
    		getSupportFragmentManager().beginTransaction()
            	.add(R.id.container, new PlaceholderFragment())
            	.commit();
		}
    }

	public static class PlaceholderFragment extends Fragment {

    	private InvoiceFragment booksFragment = new InvoiceFragment();
    	private AffairsFragment affairsFragment = new AffairsFragment();
    	private NavFragment navFragment = new NavFragment();
    	private MeFragment meFragment = new MeFragment();
    	
        private FootNavigationView footView;
        private View currentView;
        private boolean isFirst = true;

		public PlaceholderFragment() {
        }

        public void setFormTxt(String txt) {
			if (!TextUtils.isEmpty(txt)) {
				footView.setFormTxt(txt);
			}
		}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            
            initControls(rootView);
            
            return rootView;
        }

		private void initControls(View view) {
			footView = (FootNavigationView) view.findViewById(R.id.main_fragment_foot);
			
			footView.setBooksClickLis(new booksBtnOnclickLis());//待审单据
			footView.setAffairsClickLis(new affairsBtnOnClickLis());//待办事务
			footView.setFormClickLis(new formBtnOnClickLis());//报表查询
			footView.setMeClickLis(new meBtnOnClickLis());//我
			
			int showFlag = getArguments().getInt("showFlag", -1);//从通知栏获取的显示待审单据/待办事务的flag
			if (showFlag == 0) {				
				footView.setPerformClick(0);
			}else if (showFlag == 1) {
				footView.setPerformClick(2);
			}else {
				footView.setPerformClick(0);
			}
		}
		//待办事务的未读消息
		public void setAffairsUnReadCount(String count){
			if ("0".equals(count)) {
				footView.setAffairsRemindTxtGone();
			}else {
				footView.setAffairsRemindTxtVisible();
				footView.setAffairsRemindText(count);
				footView.setAffairsRemindBackground(count);
			}
		}
		//待审单据的未读消息
		public void setInvoiceUnReadCount(String count){
			if ("0".equals(count)) {
				footView.setBookRemindTxtGone();
			}else {
				footView.setBookRemindTxtVisible();
				footView.setBookRemindText(count);
				footView.setBookRemindBackground(count);
			}
		}
		
		public void setMeRemind(String remind){
			if ("0".equals(remind)) {
				footView.setMeRemindImgGone();
			}else {
				footView.setMeRemindImgVisible();
			}
		}
		//待审单据
		private class booksBtnOnclickLis implements OnClickListener{

			@Override
			public void onClick(View v) {
				if (isFirst) {
					ClickLogTask clickLogTask = new ClickLogTask();
					clickLogTask.execute(getActivity(), "待审单据", "EAA01AA", 2, 0);
					isFirst = false;
				}else {					
					if (booksFragment.isHidden()) {					
						ClickLogTask clickLogTask = new ClickLogTask();
						clickLogTask.execute(getActivity(), "待审单据", "EAA01AA", 2, 0);
					}
				}
				
				if (!booksFragment.isAdded()) {
					getFragmentManager().beginTransaction()
						.add(R.id.main_fragment_content, booksFragment)
						.commit();
				}
				getFragmentManager().beginTransaction()
					.show(booksFragment)
					.hide(affairsFragment)
					.hide(navFragment)
					.hide(meFragment)
					.commit();
				setChecked(v);
				setTxtColor(0);
			}
			
		}
		//待办事务
		private class affairsBtnOnClickLis implements OnClickListener{

			@Override
			public void onClick(View v) {
				if (affairsFragment.isHidden()) {					
					ClickLogTask clickLogTask = new ClickLogTask();
					clickLogTask.execute(getActivity(), "待办事务", "EAA05AA", 2, 0);
				}
				
				if (!affairsFragment.isAdded()) {
					getFragmentManager().beginTransaction()
						.add(R.id.main_fragment_content, affairsFragment)
						.commit();
				}
				getFragmentManager().beginTransaction()
					.show(affairsFragment)
					.hide(booksFragment)
					.hide(navFragment)
					.hide(meFragment)
					.commit();
				setChecked(v);
				setTxtColor(1);
			}
			
		}
		//即时快报
		private class formBtnOnClickLis implements OnClickListener{

			@Override
			public void onClick(View v) {
				if (navFragment.isHidden()) {
					ClickLogTask clickLogTask = new ClickLogTask();
					clickLogTask.execute(getActivity(), "即时快报", "EAA08AA", 2, 0);
				}
				
				if (!navFragment.isAdded()) {
					getFragmentManager().beginTransaction()
						.add(R.id.main_fragment_content, navFragment)
						.commit();
				}
				getFragmentManager().beginTransaction()
					.show(navFragment)
					.hide(booksFragment)
					.hide(affairsFragment)
					.hide(meFragment)
					.commit();
				setChecked(v);
				setTxtColor(2);
			}
			
		}
		//我
		private class meBtnOnClickLis implements OnClickListener{

			@Override
			public void onClick(View v) {
				if (!meFragment.isAdded()) {
					getFragmentManager().beginTransaction()
						.add(R.id.main_fragment_content, meFragment)
						.commit();
				}
				getFragmentManager().beginTransaction()
					.show(meFragment)
					.hide(booksFragment)
					.hide(affairsFragment)
					.hide(navFragment)
					.commit();
				setChecked(v);
				setTxtColor(3);
			}
			
		}
		
		private void setChecked(View view){
			if (currentView == null) {
				currentView = view;
			}else if (currentView == view) {
				return;
			}
			((ViewGroup)currentView).getChildAt(0).setEnabled(true);
			((ViewGroup)view).getChildAt(0).setEnabled(false);
			currentView = view;
		}
		
		private void setTxtColor(int id){
			switch (id) {
			case 0:
				footView.setBooksTxtColor(getResources().getColor(R.color.text_color_blue));
				footView.setAffairsTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setFormTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setMeTxtColor(getResources().getColor(R.color.text_color_weak));
				break;
			case 1:
				footView.setBooksTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setAffairsTxtColor(getResources().getColor(R.color.text_color_blue));
				footView.setFormTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setMeTxtColor(getResources().getColor(R.color.text_color_weak));
				break;
			case 2:
				footView.setBooksTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setAffairsTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setFormTxtColor(getResources().getColor(R.color.text_color_blue));
				footView.setMeTxtColor(getResources().getColor(R.color.text_color_weak));
				break;
			case 3:
				footView.setBooksTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setAffairsTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setFormTxtColor(getResources().getColor(R.color.text_color_weak));
				footView.setMeTxtColor(getResources().getColor(R.color.text_color_blue));
				break;

			default:
				break;
			}
		}
		
    }
    
    /**
     * 双击退出
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
//    		exitBy2Click();
    		new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
    			.setTitleText("确认退出？")
    			.setCancelText("取消")
    			.setConfirmText("确定")
    			.showCancelButton(true)
    			.setCancelClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						sweetAlertDialog.dismiss();
					}
				})
				.setConfirmClickListener(new OnSweetClickListener() {
					
					@Override
					public void onClick(SweetAlertDialog sweetAlertDialog) {
						ClickLogTask clickLogTask5 = new ClickLogTask();
						clickLogTask5.execute(MainActivity.this, "退出系统", "", 3, 1);
						
						sweetAlertDialog.dismiss();
						finish();
//						System.exit(0);
//						SceoApplication.getInstance().exit();
					}
				})
				.show();
		}
    	
    	return false;
    }
    
    private static boolean isExit = false;
    private void exitBy2Click(){
    	Timer tExit = null;
    	if (isExit == false) {
			isExit = true;//准备退出
			Toasty.info(this, "再按一次退出程序", Toast.LENGTH_SHORT, true).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				
				@Override
				public void run() {
					isExit = false;//取消退出
				}
			}, 2000);
		}else {
			finish();
			System.exit(0);
//			SceoApplication.getInstance().exit();
		}
    }
    
    /**
     * 版本检测

     */
	private VersionData data;
	//private SweetAlertDialog alertDialog;
    private void checkVersionName(){
    	RequestParams params = new RequestParams();
    	params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(MainActivity.this));
    	params.addBodyParameter("type", "android");
    	params.addBodyParameter("versionno", Settings.getVersionName(MainActivity.this));
    	params.addBodyParameter("veracctid", SceoUtil.getAcctid(MainActivity.this)+"");
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
								holderFragment.setMeRemind("1");
								SceoUtil.setHasNewVersion(MainActivity.this, true);
								SceoUtil.shareSet(MainActivity.this, "downloadVisibleControl", true);
								
								new Handler().postDelayed(new Runnable() {
									
									@Override
									public void run() {
										if (!isFinishing()) {	//判断一下activity是否还存在--这里也可进行抛异常处理								
											new SweetAlertDialog(MainActivity.this, SweetAlertDialog.NORMAL_TYPE)
											.setTitleText("发现新版本，是否更新？")
											.setContentText(data.getData().getDesc())
											.setCancelText("暂不更新")
											.setConfirmText("立即更新")
											.showCancelButton(true)
											.setCancelClickListener(new OnSweetClickListener() {
												
												@Override
												public void onClick(SweetAlertDialog sweetAlertDialog) {
													sweetAlertDialog.dismiss();
												}
											})
											.setConfirmClickListener(new OnSweetClickListener() {
												@Override
												public void onClick(SweetAlertDialog sweetAlertDialog) {
													//alertDialog = sweetAlertDialog;
													if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
														//没有权限则申请权限
														ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
								
							}else {
								SceoUtil.setHasNewVersion(MainActivity.this, false);
								SceoUtil.shareSet(MainActivity.this, "downloadVisibleControl", false);
							}
						}
					}
				});
    }

	private void updateAPK() {
		// updateIntent = new Intent(MainActivity.this,
		// 		AppUpdateService.class);
		// startService(updateIntent);
		SceoUtil.shareSet(MainActivity.this, "targetVersion", data.getData().getVersion());

		UpdateAppBean updateAppBean = new UpdateAppBean();
		//设置 apk 的下载地址
		//updateAppBean.setApkFileUrl(Conf.SERVICE_URL + "sceosrv/csp/" + Conf.APP_PREFIX + Conf.saveFileName);
		updateAppBean.setApkFileUrl("https://app.huntkey.com/static/sceo.apk");

		//设置apk 的保存路径
		updateAppBean.setTargetPath(Environment.getExternalStorageDirectory() + Conf.savePath);
		//实现网络接口，只实现下载就可以
		updateAppBean.setHttpManager(new UpdateAppHttpUtil());

		UpdateAppManager.download(this, updateAppBean, new DownloadService.DownloadCallback() {
			@Override
			public void onStart() {
				HProgressDialogUtils.showHorizontalProgressDialog(MainActivity.this, "下载进度", false);
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
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode){
			case 1:
				if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
					//执行代码,这里是已经申请权限成功了,可以不用做处理
					updateAPK();
				}else{
					Toast.makeText(MainActivity.this, "权限申请失败", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}
    
    @Override
    protected void onDestroy() {
    	if (heartbeatIntent != null) {			
    		stopService(heartbeatIntent);
		}
//    	stopService(updateIntent);
    	super.onDestroy();
    	if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
    }
}
