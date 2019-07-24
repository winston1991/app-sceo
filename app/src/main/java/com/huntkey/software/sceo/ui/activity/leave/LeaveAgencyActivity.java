package com.huntkey.software.sceo.ui.activity.leave;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.LeaveAgencyData;
import com.huntkey.software.sceo.bean.LeaveAgencyDetails;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.LeaveAgencyAdapter;
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

/**
 * 请假代理人
 * @author chenliang3
 *
 */
public class LeaveAgencyActivity extends BaseActivity {

	@ViewInject(R.id.leave_agency_title)
	BackTitle title;
	@ViewInject(R.id.leave_agency_todo)
	TextView searchBtn;
	@ViewInject(R.id.leave_agency_clear_edittext)
	ClearEditText clearEditText;
	@ViewInject(R.id.leave_agency_listView)
	ListView listView;
	
	private LoadingDialog loadingDialog;
	private List<LeaveAgencyDetails> agencyDetails = new ArrayList<>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave_agent);
		ViewUtils.inject(this);
		
		initTitle();
		initView();
	}

	private void initView() {
		loadingDialog = new LoadingDialog(LeaveAgencyActivity.this);
		
		searchBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String input = clearEditText.getText().toString().trim();
				if (!TextUtils.isEmpty(input)) {
					doSearchNetwork(input);
				}
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("agencyCode", agencyDetails.get(position).getEmp_id());
				intent.putExtra("agencyName", agencyDetails.get(position).getEmp_name());
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}
	
	private void doSearchNetwork(String input){
		RequestParams params = new RequestParams();
		params.addBodyParameter("keyword", input);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA09AB&pcmd=GetProxyEmpList&charset=utf8",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						LeaveAgencyData data = SceoUtil.parseJson(responseInfo.result, LeaveAgencyData.class);
						if (data.getStatus() == 0) {
							agencyDetails = data.getData();
							LeaveAgencyAdapter adapter = new LeaveAgencyAdapter(LeaveAgencyActivity.this, agencyDetails);
							listView.setAdapter(adapter);
						}else if (data.getStatus() == 88) {
							Toasty.error(LeaveAgencyActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(LeaveAgencyActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(LeaveAgencyActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	private void initTitle() {
		title.setBackTitle("选择代理人");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
