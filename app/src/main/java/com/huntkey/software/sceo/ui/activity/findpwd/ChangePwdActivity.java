package com.huntkey.software.sceo.ui.activity.findpwd;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.BaseData;
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

public class ChangePwdActivity extends BaseActivity {

	@ViewInject(R.id.change_pwd_title)
	BackTitle title;
	@ViewInject(R.id.change_pwd_old)
	ClearEditText pwdOld;
	@ViewInject(R.id.change_pwd_new)
	ClearEditText pwdNew;
	@ViewInject(R.id.change_pwd_new_confirm)
	ClearEditText pwdNewConfirm;
	@ViewInject(R.id.change_pwd_btn)
	Button changeBtn;
	
	private int CHANGE_PWD_STATUS1 = 1;//已经修改过初始密码，提示密码将要过期，此时需要传sessionkey
	private int CHANGE_PWD_STATUS2 = 2;//初始密码未修改过，或者密码已经过期需要修改，此时不需要传sessionkey，只传账号
	private int pwdStatus;
	private String userAccount;
	private LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_pwd);
		ViewUtils.inject(this);
		
		pwdStatus = getIntent().getIntExtra("change_pwd_status", -1);
		userAccount = getIntent().getStringExtra("userAccount");
		
		loadingDialog = new LoadingDialog(ChangePwdActivity.this);
		
		initTitle();
		initControl();
	}

	private void initControl() {
		changeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				String oldString = pwdOld.getText().toString().trim();
				String newString = pwdNew.getText().toString().trim();
				String confirmString = pwdNewConfirm.getText().toString().trim();
				
				if (oldString == null || "".equals(oldString)) {
					Toasty.warning(ChangePwdActivity.this, "请输入旧密码", Toast.LENGTH_SHORT, true).show();
					return;
				}
				if (newString == null || "".equals(newString)) {
					Toasty.warning(ChangePwdActivity.this, "请输入新密码", Toast.LENGTH_SHORT, true).show();
					return;
				}
				if (confirmString == null || "".equals(confirmString)) {
					Toasty.warning(ChangePwdActivity.this, "请再次输入新密码", Toast.LENGTH_SHORT, true).show();
					return;
				}		
				if (newString.length() < 6 || newString.length() > 20) {
					Toasty.warning(ChangePwdActivity.this, "密码长度为6-20", Toast.LENGTH_SHORT, true).show();
					return;
				}
				if (oldString.equals(newString)) {
					Toasty.warning(ChangePwdActivity.this, "新密码和旧密码不能相同", Toast.LENGTH_SHORT, true).show();
					return;
				}
				
				doNetWork();
			}
		});
	}
	
	private void doNetWork(){
		if (pwdNew.getText().toString().trim().equals(pwdNewConfirm.getText().toString().trim())) {			
			RequestParams params = new RequestParams();
			if (pwdStatus == CHANGE_PWD_STATUS1) {				
				params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChangePwdActivity.this));
			}
			params.addBodyParameter("loginuser", userAccount);
			params.addBodyParameter("pwd", pwdNew.getText().toString().trim());
			params.addBodyParameter("oldpwd", pwdOld.getText().toString().trim());
			params.addBodyParameter("uid", SceoUtil.getDeviceId(ChangePwdActivity.this));
			params.addBodyParameter("HostName", android.os.Build.MODEL);
			params.addBodyParameter("SysVer", android.os.Build.VERSION.RELEASE);
			params.addBodyParameter("versionname", Settings.getVersionName(ChangePwdActivity.this));
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=ModifyUserPwd",
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
							Toasty.success(ChangePwdActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							if (data.getStatus() == 0) {
								finish();
							}
						}
						
						@Override
						public void onStart() {
							super.onStart();
							loadingDialog.show();
						}
					});
		}else {
			Toasty.warning(ChangePwdActivity.this, "新密码输入不一致", Toast.LENGTH_SHORT, true).show();
		}
	}

	private void initTitle() {
		title.setBackTitle("修改密码");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				finish();
			}
		});
	}
	
}
