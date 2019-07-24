package com.huntkey.software.sceo.ui.activity.forms;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.bean.FormAddData;
import com.huntkey.software.sceo.bean.FormAddDetails;
import com.huntkey.software.sceo.bean.FormEditData;
import com.huntkey.software.sceo.bean.FormEditDetails;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.FormAddLeftAdapter;
import com.huntkey.software.sceo.ui.adapter.FormAddRightAdapter;
import com.huntkey.software.sceo.ui.adapter.FormAddRightAdapter.ViewHolder;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
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
 * 即时快报添加界面
 * @author chenliang3
 *
 */
public class FormAddActivity extends BaseActivity {

	@ViewInject(R.id.formadd_title)
	BackTitle title;
	@ViewInject(R.id.formadd_list_left)
	ListView leftListView;
	@ViewInject(R.id.formadd_list_right)
	ListView rightListView;
	
	private LoadingDialog loadingDialog;
	private FormAddLeftAdapter leftAdapter;
	private List<FormEditDetails> editDetails;
	private FormAddRightAdapter rightAdapter;
	private List<FormEditDetails> getDetails;
	private List<FormAddDetails> leftDetails;
	private List<FormEditDetails> saveList = new ArrayList<>();

	private String code;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formadd);
		ViewUtils.inject(this);
		
		editDetails = (List<FormEditDetails>) getIntent().getSerializableExtra("editdetails");
		code = getIntent().getStringExtra("code");
		
		initTitle();
		initView();
	}

	private void initView() {
		loadingDialog = new LoadingDialog(FormAddActivity.this);
		getLeftData();
		
		leftListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				leftAdapter.changeSelected(position);
				if (getDetails != null) {
					getDetails.clear();
				}
				getRightData(leftDetails.get(position).getPpit_id());
			}
		});
		leftListView.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				leftAdapter.changeSelected(position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		rightListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (view.getTag() instanceof ViewHolder) {
					ViewHolder viewHolder = (ViewHolder) view.getTag();
					viewHolder.checkBox.toggle();
					
					if (viewHolder.checkBox.isChecked()) {
						saveList.add(getDetails.get(position));
					}else {
						for (int i = 0; i < saveList.size(); i++) {
							if (saveList.get(i).getPpif_id().equals(getDetails.get(position).getPpif_id())) {
								saveList.remove(saveList.get(i));
							}
						}
					}
				}
			}
		});
	}
	
	private void getLeftData(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(FormAddActivity.this));
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AD&pcmd=QueryPPIType",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						loadingDialog.dismiss();
						FormAddData data = SceoUtil.parseJson(response.result, FormAddData.class);
						if (data.getStatus() == 0) {
							leftDetails = data.getData().getList();
							leftAdapter = new FormAddLeftAdapter(FormAddActivity.this, leftDetails);
							leftListView.setAdapter(leftAdapter);
						}else if (data.getStatus() == 88) {
							Toasty.error(FormAddActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(FormAddActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(FormAddActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	private void getRightData(String code){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(FormAddActivity.this));
		params.addBodyParameter("selflag", "0");
		params.addBodyParameter("ppit_code", code);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AD&pcmd=Query",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> response) {
						loadingDialog.dismiss();
						FormEditData rightData = SceoUtil.parseJson(response.result, FormEditData.class);
						if (rightData.getStatus() == 0) {
							getDetails = rightData.getData().getList();
							rightAdapter = new FormAddRightAdapter(FormAddActivity.this, getDetails, editDetails, saveList);
							rightListView.setAdapter(rightAdapter);
						}else if (rightData.getStatus() == 88) {
							Toasty.error(FormAddActivity.this, rightData.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(FormAddActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(FormAddActivity.this, rightData.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	private void saveSets(){
		if (saveList != null && saveList.size() > 0) {
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(FormAddActivity.this));
			for (int i = 0; i < saveList.size(); i++) {
				params.addBodyParameter("ppif_id", saveList.get(i).getPpif_id());
			}
			params.addBodyParameter("kmas_code", code);
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AD&pcmd=Save",
					params, 
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							Toasty.error(FormAddActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
							if (data.getStatus() == 88) {
								SceoUtil.gotoLogin(FormAddActivity.this);
								SceoApplication.getInstance().exit();
							}
							Toasty.success(FormAddActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.setFormEditChange(FormAddActivity.this, true);
							finish();
						}
					});
		}else {
			finish();
		}
	}

	private void initTitle() {
		title.setBackTitle("添加指标");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		
		title.setConfirmBtnVisible();
		title.setConfirmText("确定");
		title.setConfirmClicklis(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				saveSets();
			}
		});
	}
	
}
