package com.huntkey.software.sceo.ui.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.huntkey.software.sceo.widget.CommonTitle;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import es.dmoral.toasty.Toasty;

/**
 * 待办事务名称设置
 * @author chenliang3
 *
 */
public class ChatNameSettingActivity extends BaseActivity {

	@ViewInject(R.id.chat_name_setting_title)
	CommonTitle title;
	@ViewInject(R.id.chat_name_setting_cEditText)
	ClearEditText editText;
	
	private String taskName;
	private int taskId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_name_setting);
		ViewUtils.inject(this);
		
		init();
	}

	private void init() {
		taskName = getIntent().getStringExtra("taskName");
		taskId = getIntent().getIntExtra("taskId", 0);
		
		title.setMiddleTitle("待办事务名称");
		title.setTitleLeftClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title.setTitleRightClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doNetWork();
			}
		});
//		editText.setHint(taskName);
		editText.setText(taskName);
		editText.setSelection(taskName.length());//光标放到最后
	}

	protected void doNetWork() {
		final String changeName = editText.getText().toString().trim();
		if (changeName != null && !"".equals(changeName) && !taskName.equals(changeName)) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChatNameSettingActivity.this));
			params.addBodyParameter("taskid", taskId+"");
			params.addBodyParameter("taskname", changeName);
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA05AD&pcmd=setTaskName&charset=utf8",
					params, 
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
//							ChatNameSettingData data = SceoUtil.parseJson(responseInfo.result, ChatNameSettingData.class);
//							if (data.getStatus() == 88) {
//								Toast.makeText(ChatNameSettingActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();
//								SceoUtil.gotoLogin(ChatNameSettingActivity.this);
//								SceoApplication.getInstance().exit();
//							}else {
//								Toast.makeText(ChatNameSettingActivity.this, data.getMessage(), Toast.LENGTH_SHORT).show();								
//							}
							Toasty.success(ChatNameSettingActivity.this, "修改成功", Toast.LENGTH_SHORT, true).show();
							Intent intent = new Intent();
							intent.putExtra("taskName", changeName);
							setResult(0x17, intent);
							ChatNameSettingActivity.this.finish();
						}
					});
		}
	}
	
}
