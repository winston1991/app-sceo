package com.huntkey.software.sceo.ui.activity.search;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.SearchResultData;
import com.huntkey.software.sceo.bean.SearchResultDetails;
import com.huntkey.software.sceo.db.DB;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.empdetails.EmpDetailsActivity;
import com.huntkey.software.sceo.ui.adapter.SearchMemoryListAdapter;
import com.huntkey.software.sceo.ui.adapter.SearchResultListAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.StringUtil;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
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
 * 搜索员工
 * @author chenliang3
 *
 */
public class SearchActivity extends BaseActivity {

	@ViewInject(R.id.back_title_back)
	RelativeLayout backBtn;
	@ViewInject(R.id.search_clear_edittext)
	ClearEditText clearEditText;
	@ViewInject(R.id.search_todo)
	TextView todoSearch;
	@ViewInject(R.id.search_memory_list)
	ListView memoryListView;
	@ViewInject(R.id.search_result_list)
	ListView resultListView;
	@ViewInject(R.id.search_error)
	ErrorView errorView;
	
	private Runnable curRunnable;
	private List<String> memoryData;
	private SearchMemoryListAdapter memoryAdapter;
	private String keyword;
	private LoadingDialog loadingDialog;
	
	private Handler handler = new Handler(){

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			memoryData = (List<String>)msg.obj;
			if (memoryData.size() > 0) {
				memoryAdapter = new SearchMemoryListAdapter(SearchActivity.this, memoryData);
				memoryListView.setAdapter(memoryAdapter);
				memoryListView.setVisibility(View.VISIBLE);
			}else {
				memoryListView.setVisibility(View.GONE);
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		ViewUtils.inject(this);
		
		loadingDialog = new LoadingDialog(SearchActivity.this);
		
		initControl();
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						errorView.setVisibility(View.GONE);
						initControl();
					}
				}, 10);
			}
		});
	}

	private void initControl() {
		backBtn.setOnClickListener(new MyOnClickListener());
		todoSearch.setOnClickListener(new MyOnClickListener());
		
		clearEditText.addTextChangedListener(new MyTextWatcher());
		
		memoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				memoryListView.setVisibility(View.GONE);
				resultListView.setVisibility(View.VISIBLE);
				getData(memoryData.get(position));
			}
		});
		
	}
	
	private class MyTextWatcher implements TextWatcher{

		@Override
		public void afterTextChanged(Editable value) {
			String newValue = value.toString();
			if (!newValue.equals(keyword)) {
				memoryListView.setVisibility(View.VISIBLE);
				resultListView.setVisibility(View.GONE);
				findKeyWord(newValue);
			}
		}

		@Override
		public void beforeTextChanged(CharSequence value, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence value, int start, int count,
				int before) {
			
		}
		
	}
	
	private class MyOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_title_back:
				finish();
				break;
			case R.id.search_todo:
				memoryListView.setVisibility(View.GONE);
				resultListView.setVisibility(View.VISIBLE);
				
				keyword = clearEditText.getText().toString();
				getData(keyword);//从服务器获取信息
				if (!StringUtil.isEmpty(keyword)) {
					saveKeyWord(keyword);
				}
				break;

			default:
				break;
			}
		}
		
	}

	public void getData(String keyword) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(SearchActivity.this));
		params.addBodyParameter("deptid", "");
		params.addBodyParameter("key", keyword);
		
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA04AC&pcmd=getSearchResult&charset=utf8", //有汉字的情况下，加"&charset=utf8"
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						errorView.setVisibility(View.VISIBLE);
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						errorView.setVisibility(View.GONE);
						
						SearchResultData data = SceoUtil.parseJson(responseInfo.result, SearchResultData.class);
						if (data.getStatus() == 0) {
							final List<SearchResultDetails> resultDetails = data.getData().getColleague();
							SearchResultListAdapter adapter = new SearchResultListAdapter(SearchActivity.this, resultDetails);
							resultListView.setAdapter(adapter);
							
							resultListView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									Intent intent = new Intent(SearchActivity.this, EmpDetailsActivity.class);
									intent.putExtra("empCode", resultDetails.get(position).getEmp_id());
									startActivity(intent);
								}
							});
						}else if (data.getStatus() == 88){
							Toasty.error(SearchActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(SearchActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(SearchActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	private void saveKeyWord(String keyword){
		DB db = new DB(this);
		db.saveInputText(keyword);
		db.Close();
	}
	
	private void findKeyWord(final String keyword){
		if (StringUtil.isEmpty(keyword)) {
			memoryListView.setVisibility(View.GONE);
			return;
		}
		
		//如果有正在处理的则移除掉
		if (null != curRunnable) {
			handler.removeCallbacks(curRunnable);
		}
		
		curRunnable = new Runnable() {
			
			@Override
			public void run() {
				DB db = new DB(SearchActivity.this);
				List<String> list = db.getInputTexts(keyword);
				db.Close();
				Message msg = new Message();
				msg.obj = list;
				handler.sendMessage(msg);
				curRunnable = null;
			}
		};
		
		handler.post(curRunnable);
	}
	
}
