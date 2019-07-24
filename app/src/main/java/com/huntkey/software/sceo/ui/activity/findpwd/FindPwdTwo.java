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

public class FindPwdTwo extends BaseActivity {

	@ViewInject(R.id.find_pwd2_title)
	BackTitle title;
	@ViewInject(R.id.find_pwd2_check)
	ClearEditText newPwd;
	@ViewInject(R.id.find_pwd2_confirm)
	ClearEditText confirmPwd;
	@ViewInject(R.id.pwd_find2_btn)
	Button confirm;
	
	private String newPwdString;
	private String confirmPwdString;
	private String acc;
	private int acctid;
	private LoadingDialog dialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_pwd_two);
		ViewUtils.inject(this);
		
		acc = getIntent().getStringExtra("account");
		acctid = getIntent().getIntExtra("acctid", 1);
		
		dialog = new LoadingDialog(FindPwdTwo.this);
		
		initView();
		initTitle();
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

	private void initView() {
		confirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				newPwdString = newPwd.getText().toString().trim();
				confirmPwdString = confirmPwd.getText().toString().trim();
				
				if (newPwdString.length() < 6 || newPwdString.length() > 20) {
					Toasty.warning(FindPwdTwo.this, "密码长度为6-20", Toast.LENGTH_SHORT, true).show();
					return;
				}
				
				if (newPwdString.equals(confirmPwdString)) {
					getConfirm();
				}else {
					Toasty.warning(FindPwdTwo.this, "两次输入密码不匹配，请重新输入", Toast.LENGTH_SHORT, true).show();
				}
			}
		});
	}
	
	private void getConfirm(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("loginuser", acc);
		params.addBodyParameter("acctid", acctid+"");
		params.addBodyParameter("userpwd", newPwd.getText().toString().trim());
		params.addBodyParameter("uid", SceoUtil.getDeviceId(FindPwdTwo.this));
		params.addBodyParameter("HostName", android.os.Build.MODEL);//手机型号
		params.addBodyParameter("SysVer", android.os.Build.VERSION.RELEASE);//系统型号
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");//后台获取的数据为GB2312格式
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA02AA&pcmd=ModifyPwd",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						dialog.dismiss();
						
						BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
						Toasty.success(FindPwdTwo.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						if (data.getStatus() == 0) {
							FindPwdOne.getInstence().finish();
							finish();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						dialog.show();
					}
				});
	}
	
}
