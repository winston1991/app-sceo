package com.huntkey.software.sceo.ui.activity.findpwd;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.bean.GetPhoneData;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.Settings;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import es.dmoral.toasty.Toasty;

public class FindPwdOne extends BaseActivity {

	@ViewInject(R.id.find_pwd_title)
	BackTitle title;
	@ViewInject(R.id.find_pwd_account)
	TextView account;
	@ViewInject(R.id.find_pwd_phone)
	TextView phone;
	@ViewInject(R.id.find_pwd_check_button)
	Button checkButton;
	@ViewInject(R.id.find_pwd_check)
	ClearEditText check;
	@ViewInject(R.id.pwd_find_btn)
	Button nextButton;
	
	private String acc;
	private int acctid;
	private TimeCount timeCount;
	private String checkString;
	private LoadingDialog loadingDialog;
	private static FindPwdOne instance = null;
	
	public static FindPwdOne getInstence(){
		return instance;
	}
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			phone.setText(msg.obj.toString());
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_pwd);
		ViewUtils.inject(this);
		
		acc = getIntent().getStringExtra("account");
		acctid = SceoUtil.getAcctid(this);
		
		loadingDialog = new LoadingDialog(FindPwdOne.this);
		
		instance = this;
		
		initTitle();
		initView();
	}

	private void initView() {
		account.setText(acc);
		
		getPhoneThread getPhone = new getPhoneThread();
		new Thread(getPhone).start();
		
		getCheck();
		
		gotoNext();
	}
	/**
	 * 下一步按钮
	 */
	private void gotoNext() {
		nextButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getChecked();
			}
		});
	}

	/**
	 * 获取验证码
	 */
	private void getCheck() {
		timeCount = new TimeCount(60000, 1000);
		
		checkButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getCheckCode();
				timeCount.start();
			}
		});
	}

	/**
	 * 获取手机号
	 * @author chenliang3
	 *
	 */
	private class getPhoneThread implements Runnable{

		@Override
		public void run() {
			RequestParams params = new RequestParams();
			params.addBodyParameter("loginuser", acc);
			params.addBodyParameter("acctid", acctid+"");
			params.addBodyParameter("uid", SceoUtil.getDeviceId(FindPwdOne.this));
			params.addBodyParameter("HostName", android.os.Build.MODEL);//手机型号
			params.addBodyParameter("SysVer", android.os.Build.VERSION.RELEASE);//系统型号
			params.addBodyParameter("versionname", Settings.getVersionName(FindPwdOne.this));
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");//后台获取的数据为GB2312格式
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=GetUserPhone",
					params, 
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							GetPhoneData data = SceoUtil.parseJson(responseInfo.result, GetPhoneData.class);
							if (data.getStatus() == 0) {
								Message msg = new Message();
								msg.obj = data.getUser_phone();
								handler.sendMessage(msg);
							}else {
								Toasty.error(FindPwdOne.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							}
						}
					});
		}
		
	}
	
	private class TimeCount extends CountDownTimer{

		/**
		 * @param millisInFuture 总时长
		 * @param countDownInterval 计时间隔
		 */
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			checkButton.setText("重新获取");
			checkButton.setClickable(true);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			checkButton.setClickable(false);
			checkButton.setText(millisUntilFinished / 1000 + "秒");
		}
		
	}
	
	/**
	 * 通知后台请求验证码
	 */
	private void getCheckCode(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("loginuser", acc);
		params.addBodyParameter("acctid", acctid+"");
		params.addBodyParameter("uid", SceoUtil.getDeviceId(FindPwdOne.this));
		params.addBodyParameter("HostName", android.os.Build.MODEL);//手机型号
		params.addBodyParameter("SysVer", android.os.Build.VERSION.RELEASE);//系统型号
		params.addBodyParameter("versionname", Settings.getVersionName(FindPwdOne.this));
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");//后台获取的数据为GB2312格式
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=SendSMS",
				params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						
					}
				});
	}
	
	/**
	 * 点击下一步
	 */
	private void getChecked(){
		checkString = check.getText().toString();
		
		if (checkString != null && !"".equals(checkString)){			
			RequestParams params = new RequestParams();
			params.addBodyParameter("loginuser", acc);
			params.addBodyParameter("acctid", acctid+"");
			params.addBodyParameter("sms_verify", checkString);
			params.addBodyParameter("uid", SceoUtil.getDeviceId(FindPwdOne.this));
			params.addBodyParameter("HostName", android.os.Build.MODEL);//手机型号
			params.addBodyParameter("SysVer", android.os.Build.VERSION.RELEASE);//系统型号
			params.addBodyParameter("versionname", Settings.getVersionName(FindPwdOne.this));
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");//后台获取的数据为GB2312格式
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=CheckSMSVerify",
					params, 
					new RequestCallBack<String>() {
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					loadingDialog.dismiss();
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					loadingDialog.dismiss();
					
					BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
					if (data.getStatus() == 0) {
						Intent intent = new Intent(FindPwdOne.this, FindPwdTwo.class);
						intent.putExtra("account", acc);
						intent.putExtra("acctid", acctid);
						startActivity(intent);
					}else {
						Toasty.error(FindPwdOne.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
					}
				}
				
				@Override
				public void onStart() {
					super.onStart();
					loadingDialog.show();
				}
			});
		}
	}
	
	private void initTitle() {
		title.setBackTitle("找回密码");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
