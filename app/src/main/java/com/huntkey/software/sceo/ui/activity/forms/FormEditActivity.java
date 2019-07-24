package com.huntkey.software.sceo.ui.activity.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.bean.FormEditData;
import com.huntkey.software.sceo.bean.FormEditDetails;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.DragListAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.draglistview.DragListView;
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
 * 即时快报设置界面
 * @author chenliang3
 *
 */
public class FormEditActivity extends BaseActivity {

	@ViewInject(R.id.formedit_title)
	BackTitle title;
	@ViewInject(R.id.formedit_del_confirm)
	RelativeLayout delConfirm;
	@ViewInject(R.id.formedit_draglist)
	DragListView listView;
	
	private List<FormEditDetails> data = new ArrayList<>();
	private LoadingDialog loadingDialog;
	private DragListAdapter adapter;
	private static FormEditActivity mInstence;

	private String code;
	private String name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_formedit);
		ViewUtils.inject(this);
		
		mInstence = this;

		code = getIntent().getStringExtra("code");
		name = getIntent().getStringExtra("name");
		
		initTitle();
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	private void initView() {
		listView.setItemsCanFocus(true);
		loadingDialog = new LoadingDialog(FormEditActivity.this);
		
		getData();
		
		delConfirm.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				SceoUtil.setFormEditChange(FormEditActivity.this, true);
				setEdit(data);
				Map<Integer, Boolean> delmap = adapter.getDelMap();
				for (int i = delmap.size()-1; i > -1; i--) {
					if (delmap.get(i)) {
						data.remove(i);
//						if (adapter != null) {
//							adapter.updateList(data);
//							adapter.notifyDataSetChanged();
//						}
						adapter = new DragListAdapter(FormEditActivity.this, data);
						listView.setAdapter(adapter);
						delConfirm.setVisibility(View.GONE);
					}
				}
			}
		});
	}
	
	private void setEdit(List<FormEditDetails> data){
		if (data != null) {			
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(FormEditActivity.this));
			for (int i = 0; i <data.size() ; i++) {
				params.addBodyParameter("kmas_id", data.get(i).getKmas_id());
				params.addBodyParameter("ppif_order", String.valueOf(i+1));
			}
			params.addBodyParameter("ppif_dels", adapter.getDelMapToString());
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AD&pcmd=SavePPISet",
					params, 
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							BaseData baseData = SceoUtil.parseJson(responseInfo.result, BaseData.class);
							Toasty.success(FormEditActivity.this, baseData.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
						
						@Override
						public void onStart() {
							super.onStart();
						}
					});
		}
	}
	
	private void getData(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(FormEditActivity.this));
		params.addBodyParameter("selflag", "1");
		params.addBodyParameter("ppit_code", "");
		params.addBodyParameter("kmas_code", code);
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
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						FormEditData editData = SceoUtil.parseJson(responseInfo.result, FormEditData.class);
						if (editData.getStatus() == 0) {
							data = editData.getData().getList();
							adapter = new DragListAdapter(FormEditActivity.this, data);
							listView.setAdapter(adapter);
						}else if (editData.getStatus() == 88) {
							Toasty.error(FormEditActivity.this, editData.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(FormEditActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(FormEditActivity.this, editData.getMessage(), Toast.LENGTH_SHORT, true).show();
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
		title.setBackTitle(name + " 设置");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (SceoUtil.getFormEditChange(FormEditActivity.this)) {					
					setEdit(data);
				}
				finish();
			}
		});
		
		title.setConfirmText("添加");
		title.setConfirmBtnVisible();
		title.setConfirmClicklis(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				if (SceoUtil.getFormEditChange(FormEditActivity.this)) {					
					setEdit(data);
				}
				Intent intent = new Intent(FormEditActivity.this, FormAddActivity.class);
				intent.putExtra("editdetails", (Serializable)data);
				intent.putExtra("code", code);
				startActivity(intent);
				finish();
			}
		});
	}
	
	/**
	 * 监听返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (SceoUtil.getFormEditChange(FormEditActivity.this)) {					
				setEdit(data);
			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public static FormEditActivity getInstence(){
		return mInstence;
	}
	
	public RelativeLayout getLayout(){
		return delConfirm;
	}
	
}
