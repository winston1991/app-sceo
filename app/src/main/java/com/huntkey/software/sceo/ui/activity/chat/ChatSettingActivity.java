package com.huntkey.software.sceo.ui.activity.chat;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.ChatSettingData;
import com.huntkey.software.sceo.bean.ChatSettingDetails;
import com.huntkey.software.sceo.bean.ChatSettingLinkman;
import com.huntkey.software.sceo.bean.eventbus.EventBusAffairsNumAndId;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.ChatGridViewAdapter;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
import com.huntkey.software.sceo.widget.NewGridView;
import com.huntkey.software.sceo.widget.SwitchButton;
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

/**
 * 待办事务设置
 * @author chenliang3
 * c4是字体从后台拿到再传给后台,前端不作处理
 */
public class ChatSettingActivity extends BaseActivity {

	@ViewInject(R.id.chat_setting_title)
	BackTitle title;
	@ViewInject(R.id.chat_setting_gridview)
	NewGridView gridView;
	@ViewInject(R.id.chat_setting_name_rl)
	RelativeLayout nameLayout;
	@ViewInject(R.id.chat_setting_name_tv)
	TextView nameText;
	@ViewInject(R.id.chat_setting_time_rl)
	RelativeLayout timeLayout;//c6
	@ViewInject(R.id.chat_setting_time_tv)
	TextView timeText;
	@ViewInject(R.id.chat_setting_c1)
	SwitchButton c1Btn;//查看加入事务前的消息
	@ViewInject(R.id.chat_setting_c2)
	SwitchButton c2Btn;//查看退出事务后的消息
	@ViewInject(R.id.chat_setting_c3)
	SwitchButton c3Btn;//邀请其他人员加入
	@ViewInject(R.id.chat_setting_c5)
	SwitchButton c5Btn;//事务消息免打扰
	@ViewInject(R.id.chat_setting_c7)
	SwitchButton c7Btn;//显示参与人姓名
	@ViewInject(R.id.chat_setting_line1)
	View line1;
	@ViewInject(R.id.chat_setting_line2)
	View line2;
	@ViewInject(R.id.chat_setting_line3)
	View line3;
	@ViewInject(R.id.chat_setting_btn)
	Button button;
	@ViewInject(R.id.chat_setting_errorView)
	ErrorView errorView;
	
	private int taskId;
	private LoadingDialog loadingDialog;
	
	private String taskName;//名称
	private int setFlag;//是否可设置
	private int raiseFlag;//是否是发起人
	private int btnFlag;//最下面的按钮是否显示
	private String btnName;//按钮名称
	private int c1;//查看加入事务前的消息
	private int c2;//查看退出事务后的消息
	private int c3;//邀请其他人员加入
	private String c4;//字体
	private int c5;//事务消息免打扰
	private int c6;//刷新时间
	private int c7;//显示参与人姓名
	
	private List<ChatSettingLinkman> linkmans;
	private ChatGridViewAdapter adapter;
	private int REQUEST_CODE = 0x11;//常用联系人添加人请求
	private int RESULT_CODE2 = 0x13;//企业通讯录添加人返回
	private int REQUEST_CODE2 = 0x16;//事务名称请求
	private int RESULT_CODE = 0x17;//事务名称返回
	private int RESULT_CODE3 = 0x19;//退出事务时让chatActivity也退出
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_setting);
		ViewUtils.inject(this);
		
		taskId = getIntent().getIntExtra("taskId", 0);
		loadingDialog = new LoadingDialog(this);
		
		initTitleAndErrorView();
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		timeText.setText(SceoUtil.getInterval(ChatSettingActivity.this)/1000 + "秒");
	}

	private void initTitleAndErrorView() {
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title.setBackTitle("事务设置");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setConfirmBtnVisible();
		title.setConfirmClicklis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setInfo2Server();
			}
		});
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						errorView.setVisibility(View.GONE);
						init();
					}
				}, 10);
			}
		});
	}

	private void init() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChatSettingActivity.this));
		params.addBodyParameter("taskid", taskId+"");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA05AC&pcmd=getTaskSet",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
						errorView.setVisibility(View.VISIBLE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						errorView.setVisibility(View.GONE);
						
						ChatSettingData data = SceoUtil.parseJson(responseInfo.result, ChatSettingData.class);
						if (data.getStatus() == 0) {
							ChatSettingDetails details = data.getData().getTasksetinfo();
							taskName = details.getTaskname();
							setFlag = details.getSetflag();
							raiseFlag = details.getRaiseflag();
							btnFlag = details.getBtnflag();
							btnName = details.getBtnname();
							c1 = details.getC1();
							c2 = details.getC2();
							c3 = details.getC3();
							c4 = details.getC4();
							c5 = details.getC5();
							c6 = details.getC6();
							c7 = details.getC7();
							linkmans = data.getData().getEmplist();
							
							initControl();
						}else if (data.getStatus() == 88) {
							Toasty.error(ChatSettingActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(ChatSettingActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(ChatSettingActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	private void initControl(){
		MyOnclickLis myOnclickLis = new MyOnclickLis();
		
		nameText.setText(taskName);
//		timeText.setText(c6+"");
		if (raiseFlag == 0) { //如果不是发起人，将c1-c3隐藏
			c1Btn.setVisibility(View.GONE);
			c2Btn.setVisibility(View.GONE);
			c3Btn.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
			line3.setVisibility(View.GONE);
		}
		
		if (c1 == 1) {
			c1Btn.setChecked(true);
		}else {
			c1Btn.setChecked(false);
		}
		
		if (c2 == 1) {
			c2Btn.setChecked(true);
		}else {
			c2Btn.setChecked(false);
		}
		
		if (c3 == 1) {
			c3Btn.setChecked(true);
		}else {
			c3Btn.setChecked(false);
		}
		
		if (c5 == 1) {
			c5Btn.setChecked(true);
		}else {
			c5Btn.setChecked(false);
		}
		
		if (c7 == 1) {
			c7Btn.setChecked(true);
			SceoUtil.setIsNameShow(ChatSettingActivity.this, true);
		}else {
			c7Btn.setChecked(false);
			SceoUtil.setIsNameShow(ChatSettingActivity.this, false);
		}
		
		if (setFlag == 1) { //是否可设置
			nameLayout.setOnClickListener(myOnclickLis);
			timeLayout.setOnClickListener(myOnclickLis);
			
			c1Btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						c1 = 1;
					}else {
						c1 = 0;
					}
				}
			});
			
			c2Btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						c2 = 1;
					}else {
						c2 = 0;
					}
				}
			});
			
			c3Btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						c3 = 1;
					}else {
						c3 = 0;
					}
				}
			});
			
			c5Btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						c5 = 1;
					}else {
						c5 = 0;
					}
				}
			});
			
			c7Btn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						c7 = 1;
						SceoUtil.setIsNameShow(ChatSettingActivity.this, true);
					}else {
						c7 = 0;
						SceoUtil.setIsNameShow(ChatSettingActivity.this, false);
					}
				}
			});
		}else {
			nameLayout.setEnabled(false);
			timeLayout.setEnabled(false);
			
			c1Btn.setEnabled(false);
			c2Btn.setEnabled(false);
			c3Btn.setEnabled(false);
			c5Btn.setEnabled(false);
			c7Btn.setEnabled(false);
		}
		
		if (btnName != null && !"".equals(btnName)) {
			button.setVisibility(View.VISIBLE);
		}else {
			button.setVisibility(View.GONE);
		}
		button.setText(btnName);
		button.setOnClickListener(myOnclickLis);
		
		adapter = new ChatGridViewAdapter(ChatSettingActivity.this, ChatSettingActivity.this, 
				linkmans, raiseFlag, REQUEST_CODE, setFlag);
		gridView.setAdapter(adapter);
	}
	
	private class MyOnclickLis implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.chat_setting_name_rl:
				if (btnFlag == 4) {//btnFlag=4,指标关联，不可修改名称
					Toasty.warning(ChatSettingActivity.this, "该事务与指标关联，不可修改名称", Toast.LENGTH_SHORT, true).show();
				}else {					
					//跳转到修改名称的activity
					Intent intent = new Intent(ChatSettingActivity.this, ChatNameSettingActivity.class);
					intent.putExtra("taskName", taskName);
					intent.putExtra("taskId", taskId);
//					startActivity(intent);
					startActivityForResult(intent, REQUEST_CODE2);
				}
				break;
			case R.id.chat_setting_time_rl:
				//跳转到修改刷新时间的activity
				Intent intent2 = new Intent(ChatSettingActivity.this, ChatTimeSettingActivity.class);
				startActivity(intent2);
				break;
			case R.id.chat_setting_btn:
				if (btnFlag == 4) {//btnFlag=4,指标关联，不可删除
					Toasty.warning(ChatSettingActivity.this, "该事务与指标关联，不能结束", Toast.LENGTH_SHORT, true).show();
				}else {					
					new SweetAlertDialog(ChatSettingActivity.this, SweetAlertDialog.WARNING_TYPE)
					.setContentText("确定" + btnName + "?")
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
							btnWorks();
							sweetAlertDialog.dismiss();
						}
					})
					.show();
				}
				break;

			default:
				break;
			}
		}
		
	}
	
	private String empString;
	private void setInfo2Server(){
//		List<ChatSettingLinkman> list = adapter.getData();
		for (int i = 0; i < linkmans.size(); i++) {
			if(empString != null && !"".equals(empString)){
				empString += ",";
			}
			empString += "'" + linkmans.get(i).getEmp_id() + "'";
		}
		empString = empString.replace("null", "");
		//empString = empString.substring(0, empString.length() - 1);
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChatSettingActivity.this));
		params.addBodyParameter("taskid", taskId+"");
		params.addBodyParameter("emplist", empString);
		params.addBodyParameter("c1", c1+"");
		params.addBodyParameter("c2", c2+"");
		params.addBodyParameter("c3", c3+"");
		params.addBodyParameter("c4", c4+"");
		params.addBodyParameter("c5", c5+"");
		params.addBodyParameter("c6", timeText.getText().toString().trim());
		params.addBodyParameter("c7", c7+"");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA05AC&pcmd=setTask",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						Toasty.success(ChatSettingActivity.this, "保存成功", Toast.LENGTH_SHORT, true).show();
						finish();
					}
				});
		
	}
	
	private void btnWorks(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChatSettingActivity.this));
		params.addBodyParameter("taskid", taskId+"");
		params.addBodyParameter("emp_id", SceoUtil.getEmpCode(ChatSettingActivity.this));
		params.addBodyParameter("btnflag", btnFlag+"");
		params.addBodyParameter("taskscore", "0");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA05AC&pcmd=setExitTask",
				params, 
				new RequestCallBack<String>(){

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						Intent intent = new Intent();
						intent.putExtra("exitFlag", 1);
						setResult(RESULT_CODE3, intent);
						ChatSettingActivity.this.finish();
					}
			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				List<ChatSettingLinkman> backLinkmans = (List<ChatSettingLinkman>) data.getSerializableExtra("backAddLinkmans");
				for (int i = 0; i < backLinkmans.size(); i++) {
					linkmans.add(backLinkmans.get(i));
				}
				adapter = new ChatGridViewAdapter(ChatSettingActivity.this, ChatSettingActivity.this, 
						linkmans, raiseFlag, REQUEST_CODE, setFlag);
				adapter.notifyDataSetChanged();
				gridView.setAdapter(adapter);
			}
		}else if (resultCode == RESULT_CODE2) {
			List<ChatSettingLinkman> backLinkmans = (List<ChatSettingLinkman>) data.getSerializableExtra("backAddLinkmans3");
			for (int i = 0; i < backLinkmans.size(); i++) {
				linkmans.add(backLinkmans.get(i));
			}
			linkmans = removeDuplicate(linkmans);
			adapter = new ChatGridViewAdapter(ChatSettingActivity.this, ChatSettingActivity.this, 
					linkmans, raiseFlag, REQUEST_CODE, setFlag);
//			adapter.notifyDataSetChanged();
			gridView.setAdapter(adapter);
		}else if (resultCode == RESULT_CODE) {
			if (requestCode == REQUEST_CODE2) {
				String nameString = data.getStringExtra("taskName");
				nameText.setText(nameString);
				Intent intent = new Intent();
				intent.putExtra("taskName", nameString);
				setResult(Activity.RESULT_OK, intent);
			}
		}
	}
	
	/**
	 * 去除重复数据
	 * @param list
	 * @return
	 */
	private List<ChatSettingLinkman> removeDuplicate(List<ChatSettingLinkman> list){
		for (int i = 0; i < list.size()-1; i++) {
			for (int j = list.size()-1; j > i; j--) {
				if (list.get(j).getEmp_id().equals(list.get(i).getEmp_id())) {
					list.remove(j);
				}
			}
		}
		return list;
	}
	
	/**
	 * 当该设置界面销毁的时候，使用EventBus将该事务的参与人数和id传给ChartActivity
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBusAffairsNumAndId affairsEmpCount = new EventBusAffairsNumAndId();
		affairsEmpCount.setaEmpCount(linkmans.size());
		affairsEmpCount.setTaskid(taskId);
		EventBusUtil.getInstence().post(affairsEmpCount);
	}
	
	
}
