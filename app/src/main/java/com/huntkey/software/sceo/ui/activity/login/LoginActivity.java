package com.huntkey.software.sceo.ui.activity.login;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.ui.activity.MainActivity;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.EmpData;
import com.huntkey.software.sceo.bean.UrlData;
import com.huntkey.software.sceo.bean.UrlDetails;
import com.huntkey.software.sceo.others.NoDoubleClickListener;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.findpwd.ChangePwdActivity;
import com.huntkey.software.sceo.ui.activity.findpwd.FindPwdOne;
import com.huntkey.software.sceo.ui.activity.home.DowndataActivity;
import com.huntkey.software.sceo.ui.adapter.LoginSpinnerAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.Settings;
import com.huntkey.software.sceo.utils.StringUtil;
import com.huntkey.software.sceo.widget.LoginClearEditText;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog.OnSweetClickListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import es.dmoral.toasty.Toasty;

public class LoginActivity extends BaseActivity {

	@ViewInject(R.id.login_account)
	private LoginClearEditText accountTxt;
	@ViewInject(R.id.login_pwd)
	private LoginClearEditText pwdTxt;
	@ViewInject(R.id.login_remember_account)
	private CheckBox remember_account;
	@ViewInject(R.id.login_forget_account)
	private TextView forget_account;
	@ViewInject(R.id.login_btn)
	private Button login_btn;
	@ViewInject(R.id.login_location)
	Spinner location;
	
	private int showFlag;
	private LoadingDialog loadingDialog;
	private HttpUtils http;
	private boolean isLocationUrlOK = false;//是否获取到正确的url地址
	private boolean canLoginBtnCls = true;//登录按钮是否可点击
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSystemBarTint(false);
		setContentView(R.layout.activity_login);
		ViewUtils.inject(this);

		initView();
		initControl();
		
		showFlag = getIntent().getIntExtra("showFlag", -1);
		SceoUtil.shareSet(this, "isWizard", true);//判断是否是第一次启动应用
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SceoApplication.getInstance().removeActivity(this);
		checkPermission(new CheckPermListener() {
			@Override
			public void superPermission() {
				getLocationUrl();
			}
		}, R.string.perssion_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
	}

	private void initView() {
		//账号
		String accountString = SceoUtil.shareGetString(LoginActivity.this, 
				SceoUtil.getAcctid(LoginActivity.this)+SceoUtil.SHARE_EMP_ACCOUNT);
		if (accountString != null && !"".equals(accountString)) {
			accountTxt.setText(accountString);
		}
		if (accountString != null && !"".equals(accountString)) {
			remember_account.setChecked(true);
		}else {
			remember_account.setChecked(false);
		}
		
		loadingDialog = new LoadingDialog(this);
		//初始化http请求
		http = new HttpUtils();
		http.configResponseTextCharset("GB2312");//设置数据格式为GB2312
		
//		getLocationUrl();
	}
	
	/**
	 * 获取服务器url并赋给Conf.setService_url
	 */
	List<UrlDetails> urlList = new ArrayList<>();
	Gson gson = new Gson();
	private void getLocationUrl() {
		String locUrlStr = SceoUtil.shareGetString(LoginActivity.this, SceoUtil.SHARE_LOCATION_URL);
		if (locUrlStr != null && !"".equals(locUrlStr)) {
			urlList = gson.fromJson(locUrlStr, new TypeToken<List<UrlDetails>>(){}.getType());
			locationUrlHandler(urlList);
		}

		RequestParams params = new RequestParams();
		params.addBodyParameter("uid", SceoUtil.getDeviceId(this));
		params.addBodyParameter("HostName", android.os.Build.MODEL);//手机型号
		params.addBodyParameter("SysVer", android.os.Build.VERSION.RELEASE);//系统型号
		params.addBodyParameter("versionname", Settings.getVersionName(this));
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.g_SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA00AA&pcmd=GetAppSrv",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String error) {
						if (loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
						Toasty.error(LoginActivity.this, "获取信息失败", Toast.LENGTH_SHORT, true).show();
						isLocationUrlOK = false;
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						final UrlData data = SceoUtil.parseJson(responseInfo.result, UrlData.class);
						if (data.getStatus() == 0) {
							isLocationUrlOK = true;
							urlList = data.getData().getList();
							//edit by chenliang 2018/07/16
							if (urlList == null && urlList.size() == 0) {
								Toasty.warning(LoginActivity.this, "数据为空", Toast.LENGTH_SHORT, true).show();
								return;
							}
							String urlListStr = gson.toJson(urlList);
							SceoUtil.shareSet(LoginActivity.this, SceoUtil.SHARE_LOCATION_URL, urlListStr);

							String tmpLocation = SceoUtil.shareGetString(LoginActivity.this, SceoUtil.SHARE_LOCATION_URL);
							if (tmpLocation == null || "".equals(tmpLocation)) {
								locationUrlHandler(urlList);
							}
						}else {
							Toasty.error(LoginActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							isLocationUrlOK = false;
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
					}
				});
	}

	private void locationUrlHandler(final List<UrlDetails> urlList) {
		//兼容新版本app
		for (UrlDetails details : urlList){
			if (details.getSrvurl().contains("sceosrv/csp/sceosrv.dll?")){
				String tmpUrl = details.getSrvurl().replace("sceosrv/csp/sceosrv.dll?", "");
				details.setSrvurl(tmpUrl);
			}
		}

		location.setAdapter(new LoginSpinnerAdapter(LoginActivity.this, urlList));

		Conf.setSERVICE_URL(urlList.get(0).getSrvurl());
		SceoUtil.setServiceUrl(LoginActivity.this, urlList.get(0).getSrvurl());
		if (SceoUtil.getAcctid(LoginActivity.this) == SceoUtil.INT_NEGATIVE) {
			SceoUtil.setAcctid(LoginActivity.this, urlList.get(0).getAcctid());
		}
		for (int i = 0; i < urlList.size(); i++) {
			if (urlList.get(i).getAcctid() == SceoUtil.getAcctid(LoginActivity.this)) {
				location.setSelection(i, true);
				Conf.setSERVICE_URL(urlList.get(i).getSrvurl());
				SceoUtil.setServiceUrl(LoginActivity.this, urlList.get(i).getSrvurl());
			}
		}
		location.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent,
									   View view, int position, long id) {
				SceoUtil.setLocation(LoginActivity.this, urlList.get(position).getSrvid());
				SceoUtil.setAcctid(LoginActivity.this, urlList.get(position).getAcctid());
				SceoUtil.setServiceUrl(LoginActivity.this, urlList.get(position).getSrvurl());
				Conf.setSERVICE_URL(urlList.get(position).getSrvurl());

				accountTxt.setText(SceoUtil.shareGetString(LoginActivity.this, urlList.get(position).getAcctid()+SceoUtil.SHARE_EMP_ACCOUNT));
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
	}

	private void initControl() {
		forget_account.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String acc = accountTxt.getText().toString().trim();
				//忘记密码
				if (acc == null || "".equals(acc)) {
					Toasty.warning(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT, true).show();
				}else {
					Intent intent = new Intent(LoginActivity.this, FindPwdOne.class);
					intent.putExtra("account", acc);
					startActivity(intent);
				}
			}
		});
		
		login_btn.setOnClickListener(new NoDoubleClickListener() {
			
			@Override
			public void onNoDoubleClick(View view) {
				doLogin(view);
			}
		});
	}
	
	private void doLogin(View view) {
		//隐藏软键盘
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		
		if (StringUtil.isEmpty(accountTxt.getText().toString().trim())) {
			Toasty.warning(LoginActivity.this, "请输入您的账号", Toast.LENGTH_SHORT, true).show();
		}else {
			if (remember_account.isChecked()) {
				SceoUtil.shareSet(LoginActivity.this, 
						SceoUtil.getAcctid(LoginActivity.this)+SceoUtil.SHARE_EMP_ACCOUNT, 
						accountTxt.getText().toString().trim());
			}else {
				SceoUtil.shareSet(LoginActivity.this, 
						SceoUtil.getAcctid(LoginActivity.this)+SceoUtil.SHARE_EMP_ACCOUNT, "");//重置为空
			}
			
			if (StringUtil.isEmpty(pwdTxt.getText().toString().trim())) {
				Toasty.warning(LoginActivity.this, "请输入您的密码", Toast.LENGTH_SHORT, true).show();
			}else {
				if (hasNetWork()) {
					if (!isLocationUrlOK) {
						checkPermission(new CheckPermListener() {
							@Override
							public void superPermission() {
								getLocationUrl();
							}
						}, R.string.perssion_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
					}else {
						if (canLoginBtnCls) {

							checkPermission(new CheckPermListener() {
								@Override
								public void superPermission() {
									getlogin();
								}
							}, R.string.perssion_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
						}
					}
				}else {
					//网络不可用
					SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE);
					sweetAlertDialog.setTitleText("网络不可用");
					sweetAlertDialog.setContentText("是否去设置？");
					sweetAlertDialog.setCancelText("暂不设置");
					sweetAlertDialog.setConfirmText("现在设置");
					sweetAlertDialog.showCancelButton(true);
					sweetAlertDialog.setCancelClickListener(new OnSweetClickListener() {
						
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							sweetAlertDialog.dismiss();
						}
					});
					sweetAlertDialog.setConfirmClickListener(new OnSweetClickListener() {
						
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
							startActivity(intent);
							sweetAlertDialog.dismiss();
						}
					});
					sweetAlertDialog.show();
				}
			}
		}
	}

	private void getlogin() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("loginuser", accountTxt.getText().toString().trim());
		params.addBodyParameter("pwd", pwdTxt.getText().toString().trim());
		params.addBodyParameter("uid", SceoUtil.getDeviceId(LoginActivity.this));
		params.addBodyParameter("HostName", android.os.Build.MODEL);//手机型号
		params.addBodyParameter("SysVer", android.os.Build.VERSION.RELEASE);//系统型号
		params.addBodyParameter("versionname", Settings.getVersionName(LoginActivity.this));
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=login",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						if (loadingDialog.isShowing()) {
							loadingDialog.dismiss();
						}
						canLoginBtnCls = true;
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						canLoginBtnCls = true;
						final EmpData data = SceoUtil.parseJson(responseInfo.result, EmpData.class);
						if (data.getStatus() == 0) {
							//取消手势和超时设置，超时退出时间现在由服务器提供
							String autoExitTime = data.getData().getApptimeout();
							try {
								SceoUtil.setOutTimeExit(LoginActivity.this, Integer.parseInt(autoExitTime));
							} catch (Exception e) {
								SceoUtil.setOutTimeExit(LoginActivity.this, 0);
								e.printStackTrace();
							}
							//将手势密码设为空，以取消手势
							SceoUtil.setGesturePassword(LoginActivity.this, "");
							
							SceoUtil.saveEmp(LoginActivity.this, data.getData());
							if ("1".equals(data.getData().getAppflag()) || "-1".equals(data.getData().getAppflag())){//判断app是否绑定，绑定进入MainActivity，未绑定进入SrorageHomeActivity
								Intent intent = new Intent(LoginActivity.this, MainActivity.class);
								intent.putExtra("showFlag", showFlag);
								startActivity(intent);
							}else {
                                Intent intent = new Intent(LoginActivity.this, DowndataActivity.class);
								startActivity(intent);
							}
							finish();
						}else if (data.getStatus() == 3) {//提示密码将要过期
							SceoUtil.saveEmp(LoginActivity.this, data.getData());
							new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
								.setTitleText("系统提示")
								.setContentText(data.getMessage())
								.setCancelText("暂不修改")
								.setConfirmText("现在修改")
								.showCancelButton(true)
								.setCancelClickListener(new OnSweetClickListener() {
									
									@Override
									public void onClick(SweetAlertDialog sweetAlertDialog) {
										sweetAlertDialog.dismiss();
										if ("1".equals(data.getData().getAppflag())){
											Intent intent = new Intent(LoginActivity.this, MainActivity.class);
											intent.putExtra("showFlag", showFlag);
											startActivity(intent);
										}else {
                                            Intent intent = new Intent(LoginActivity.this, DowndataActivity.class);
											startActivity(intent);
										}
										finish();
									}
								})
								.setConfirmClickListener(new OnSweetClickListener() {
									
									@Override
									public void onClick(SweetAlertDialog sweetAlertDialog) {
										sweetAlertDialog.dismiss();
										//将密码输入框置空
										pwdTxt.setText("");
										//跳转到修改密码界面
										Intent intent = new Intent(LoginActivity.this, ChangePwdActivity.class);
										intent.putExtra("change_pwd_status", 1);
										intent.putExtra("userAccount", accountTxt.getText().toString().trim());
										startActivity(intent);
									}
								})
								.show();
						}else if (data.getStatus() == 4 || data.getStatus() == 6) {//密码已经过期/初始密码
							new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
								.setTitleText(data.getMessage())
								.setConfirmText("确定")
								.showCancelButton(false)
								.setConfirmClickListener(new OnSweetClickListener() {
									
									@Override
									public void onClick(SweetAlertDialog sweetAlertDialog) {
										sweetAlertDialog.dismiss();
										//将密码输入框置空
										pwdTxt.setText("");
										//跳转到修改密码界面
										Intent intent = new Intent(LoginActivity.this, ChangePwdActivity.class);
										intent.putExtra("change_pwd_status", 2);
										intent.putExtra("userAccount", accountTxt.getText().toString().trim());
										startActivity(intent);
									}
								})
								.show();
						}else {
							final SweetAlertDialog errorDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.NORMAL_TYPE)
							.setTitleText(data.getMessage())
							.setConfirmText("确定")
							.showCancelButton(false)
							.setConfirmClickListener(new OnSweetClickListener() {
								
								@Override
								public void onClick(SweetAlertDialog sweetAlertDialog) {
									//将密码输入框置空
									pwdTxt.setText("");
									pwdTxt.setFocusable(true);
									pwdTxt.setFocusableInTouchMode(true);
									pwdTxt.requestFocus();
									sweetAlertDialog.dismiss();
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);  
									imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
								}
							});
							errorDialog.show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
						canLoginBtnCls = false;
					}
				});
	}

}
