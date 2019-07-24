package com.huntkey.software.sceo.ui.fragment;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.others.ClickLogTask;
import com.huntkey.software.sceo.service.AppUpdateService;
import com.huntkey.software.sceo.ui.activity.MainActivity;
import com.huntkey.software.sceo.ui.activity.WebViewActivity;
import com.huntkey.software.sceo.ui.activity.business.BusinessActivity;
import com.huntkey.software.sceo.ui.activity.empdetails.EmpDetailsActivity;
import com.huntkey.software.sceo.ui.activity.findpwd.ChangePwdActivity;
import com.huntkey.software.sceo.ui.activity.home.DowndataActivity;
import com.huntkey.software.sceo.ui.activity.leave.LeaveActivity;
import com.huntkey.software.sceo.ui.activity.linkman.LinkmanActivity;
import com.huntkey.software.sceo.ui.adapter.MeGridViewAdapter;
import com.huntkey.software.sceo.utils.HProgressDialogUtils;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.UpdateAppHttpUtil;
import com.huntkey.software.sceo.widget.MainTitle;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog.OnSweetClickListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.vector.update_app.UpdateAppBean;
import com.vector.update_app.UpdateAppManager;
import com.vector.update_app.service.DownloadService;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.io.File;

import es.dmoral.toasty.Toasty;

/**
 * 我
 * @author chenliang3
 *
 */
public class MeFragment extends Fragment {

	@ViewInject(R.id.me_title)
	MainTitle title;
	@ViewInject(R.id.me_photo)
	ImageView photo;
	@ViewInject(R.id.me_name)
	TextView name;
	@ViewInject(R.id.me_code)
	TextView empCode;
	@ViewInject(R.id.me_gridview)
	GridView gridView;
	@ViewInject(R.id.me_layout1)
	RelativeLayout tempLayout;
	
	private int imageArray[] = {R.drawable.ic_worklog, R.drawable.ic_personal_management, 
			R.drawable.ic_qr, R.drawable.ic_change_pwd/*, R.drawable.ic_settings*/,R.drawable.ic_leave, 
			R.drawable.ic_business, R.drawable.ic_invoices, R.drawable.ic_exit};
	private String textArray[] = {"工作日历","联系人","二维码","修改密码"/*,"设置"*/,"请假申请", "出差申请", "移动应用", "退出登录"};
	private MeGridViewAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_me, null);
		ViewUtils.inject(this, view);
		
		initView();
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	private void initView() {
		title.setMainTitle("我");
		title.setMainTitleColor(getResources().getColor(R.color.white));
		
		name.setText(SceoUtil.getEmpName(getActivity()));
		empCode.setText("工号："+SceoUtil.getEmpCode(getActivity()));
		Glide
				.with(getContext())
				.load(SceoUtil.getEmpPhoto(getActivity()))
				.centerCrop()
				.placeholder(R.drawable.ic_login_photo)
				.crossFade()
				.into(photo);
		
		gridView.setOnItemClickListener(new MyOnItemClickListener());
		
		tempLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), EmpDetailsActivity.class);
				intent.putExtra("empCode", SceoUtil.getEmpCode(getActivity()));
				getActivity().startActivity(intent);
			}
		});
		
		adapter = new MeGridViewAdapter(getActivity(), imageArray, textArray, SceoUtil.getHasNewVersion(getActivity()));
		gridView.setAdapter(adapter);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					//执行代码,这里是已经申请权限成功了,可以不用做处理
					updateAPK();
				} else {
					Toast.makeText(getActivity(), "权限申请失败", Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	private void updateAPK() {
		// Intent intent = new Intent(getActivity(), AppUpdateService.class);
		// getActivity().startService(intent);
		// Toasty.info(getActivity(), "开始下载新版本", Toast.LENGTH_SHORT, true).show();

		UpdateAppBean updateAppBean = new UpdateAppBean();
		//设置 apk 的下载地址
		// updateAppBean.setApkFileUrl(Conf.SERVICE_URL + "sceosrv/csp/" + Conf.APP_PREFIX + Conf.saveFileName);
		updateAppBean.setApkFileUrl("https://app.huntkey.com/static/sceo.apk");

		//设置apk 的保存路径
		updateAppBean.setTargetPath(Environment.getExternalStorageDirectory() + Conf.savePath);
		//实现网络接口，只实现下载就可以
		updateAppBean.setHttpManager(new UpdateAppHttpUtil());

		UpdateAppManager.download(getContext(), updateAppBean, new DownloadService.DownloadCallback() {
			@Override
			public void onStart() {
				HProgressDialogUtils.showHorizontalProgressDialog(getActivity(), "下载进度", false);
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

	private class MyOnItemClickListener implements OnItemClickListener{

		Intent intent = new Intent(getActivity(), WebViewActivity.class);
		String webUrl = Conf.SERVICE_URL;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0://工作日历
				intent.putExtra("webUrl", webUrl + "CWASysV4/csp/CWASysV4.dll?page=EWA99AA&pcmd=INIT&app=1");
				intent.putExtra("titleName", "工作日历");
				startActivity(intent);
				
				ClickLogTask clickLogTask = new ClickLogTask();
				clickLogTask.execute(getActivity(), "工作日历", "EWA99AA", 2, 0);
				break;
//			case 1:
//				intent.putExtra("webUrl", webUrl + "EKPSysV2/csp/EKPSys.dll?page=EKP24AA&pcmd=init&app=1");
//				intent.putExtra("titleName", "我的PPI");
//				startActivity(intent);
//				
//				ClickLogTask clickLogTask2 = new ClickLogTask();
//				clickLogTask2.execute(getActivity(), "我的PPI", "EKP24AA", 2);
//				break;
//			case 2:
//				intent.putExtra("webUrl", webUrl + "EKPSysV2/csp/EKPSys.dll?page=EKP24AA&pcmd=init&iskpi=1&app=1");
//				intent.putExtra("titleName", "我的KPI");
//				startActivity(intent);
//				
//				ClickLogTask clickLogTask3 = new ClickLogTask();
//				clickLogTask3.execute(getActivity(), "我的KPI", "EKP24AA", 2);
//				break;
			case 1://联系人
				Intent intent2 = new Intent(getActivity(), LinkmanActivity.class);
				startActivity(intent2);
				
				ClickLogTask clickLogTask4 = new ClickLogTask();
				clickLogTask4.execute(getActivity(), "联系人", "EAA06AA", 2, 0);
				break;
			case 2://二维码
				LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
				View dialogView = inflater.inflate(R.layout.dialog_qr, null);
				RelativeLayout qr = (RelativeLayout) dialogView.findViewById(R.id.me_fragment_qr);
				TextView download = (TextView) dialogView.findViewById(R.id.dialog_qr_download);
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setView(dialogView);
				final AlertDialog dialog = builder.show();
				
				qr.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				//没有新版本时隐藏
				if (!SceoUtil.shareGetBoolean(getActivity(), "downloadVisibleControl")) {
					download.setVisibility(View.GONE);
				}else {
					download.setVisibility(View.VISIBLE);
				}
				download.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view) {
						if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
							//没有权限则申请权限
							ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
						}
						else{
							updateAPK();
						}

						/*Intent intent = new Intent(getActivity(), AppUpdateService.class);
						getActivity().startService(intent);
						Toasty.info(getActivity(), "开始下载新版本", Toast.LENGTH_SHORT, true).show();*/
						dialog.dismiss();
					}
				});
				
				//小红点
				SceoUtil.setHasNewVersion(getActivity(), false);
				adapter.updateAdapter(SceoUtil.getHasNewVersion(getActivity()));
				adapter.notifyDataSetChanged();
				//"我"上面的小红点
				Intent broadIntent = new Intent();
				broadIntent.putExtra("hasNewVersionFlag", false);
				broadIntent.setAction(SceoUtil.ACTION_HAS_NEW_VERSION);
				getActivity().sendBroadcast(broadIntent);
				break;
			case 3://修改密码
				Intent intent = new Intent(getActivity(), ChangePwdActivity.class);
				intent.putExtra("change_pwd_status", 1);
				intent.putExtra("userAccount", SceoUtil.getEmpCode(getActivity()));
				startActivity(intent);
				
				ClickLogTask clickLogTask6 = new ClickLogTask();
				clickLogTask6.execute(getActivity(), "修改密码", "EAA02AA", 2, 0);
				break;
//			case 4://设置(超时退出和手势设置)
//				Intent intent3 = new Intent(getActivity(), SettingActivity.class);
//				int time = SceoUtil.getOutTimeExit(getActivity())/1000;
//				intent3.putExtra("time", time);
//				intent3.putExtra("flag", "meFragment");
//				startActivity(intent3);
//				break;
			case 4://请假申请单
				Intent intent4 = new Intent(getActivity(), LeaveActivity.class);
				startActivity(intent4);
				break;
			case 5://出差申请
				Intent intent5 = new Intent(getActivity(), BusinessActivity.class);
				startActivity(intent5);
				break;
			case 6://移动应用
				Intent intent6 = new Intent(getActivity(), DowndataActivity.class);
				startActivity(intent6);
				break;
			case 7://退出登录
				new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
						.setTitleText("确定退出？")
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
								sweetAlertDialog.dismiss();
								
								try {
									SceoUtil.setOnceWork(getActivity(), true);//更改只作用一次的bool值
								} catch (Exception e) {
									e.printStackTrace();
								}
								
								ClickLogTask clickLogTask5 = new ClickLogTask();
								clickLogTask5.execute(getActivity(), "退出系统", "", 3, 0);
								
								SceoUtil.gotoLogin(getActivity());
								SceoApplication.getInstance().exit();
							}
						})
						.show();
				break;

			default:
				break;
			}
		}
		
	} 
	
}
