package com.huntkey.software.sceo.ui.activity.search;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.InvoiceData;
import com.huntkey.software.sceo.bean.InvoiceDetails;
import com.huntkey.software.sceo.db.DB;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.invoicedetails.InvoiceDetailsActivity;
import com.huntkey.software.sceo.ui.adapter.InvoiceAdapter;
import com.huntkey.software.sceo.ui.adapter.SearchMemoryListAdapter;
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
 * 按单号搜索待审单据
 * @author chenliang3
 *
 */
public class InvoiceSearchActivity extends BaseActivity {

	@ViewInject(R.id.invoice_back_title_back)
	RelativeLayout backBtn;
	@ViewInject(R.id.invoice_search_clear_edittext)
	ClearEditText clearEditText;
	@ViewInject(R.id.invoice_search_todo)
	TextView todoSearch;
	@ViewInject(R.id.invoice_search_memory_list)
	ListView memoryListView;
	@ViewInject(R.id.invoice_search_result_list)
	ListView resultListView;
	@ViewInject(R.id.invoice_search_error)
	ErrorView errorView;
	
	private LoadingDialog loadingDialog;
	private List<String> memoryData;
	private SearchMemoryListAdapter memoryAdapter;
	private String keyword;
	private Runnable curRunnable;
	
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			memoryData = (List<String>)msg.obj;
			if (memoryData.size() > 0) {
				memoryAdapter = new SearchMemoryListAdapter(InvoiceSearchActivity.this, memoryData);
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
		setContentView(R.layout.activity_invoice_search);
		ViewUtils.inject(this);
		
		loadingDialog = new LoadingDialog(this);
		
		initControl();
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						hideErrorView(errorView);
						initControl();
					}
				}, 10);
			}
		});
	}



	private void initControl() {
		backBtn.setOnClickListener(new MyOnclickListener());
		todoSearch.setOnClickListener(new MyOnclickListener());
		
		clearEditText.addTextChangedListener(new MyTextWatcher());
		
		memoryListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				memoryListView.setVisibility(View.GONE);
				resultListView.setVisibility(View.VISIBLE);
				getData(memoryData.get(position));
				
				//隐藏键盘
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(clearEditText.getWindowToken(), 0);
			}
		});
	}
	
	private void getData(String keyword){
		hideErrorView(errorView);
		
		if (!hasNetWork()) {
			showErrorView(errorView, 
					getResources().getDrawable(R.drawable.ic_content_manager_wifi), 
					"网络请求失败", 
					"请打开您的数据连接并重试");
		}else {
			doNetWork(keyword);
		}
	}
	
	public void doNetWork(String keyword) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(InvoiceSearchActivity.this));
		params.addBodyParameter("tasktype", "1");
		params.addBodyParameter("begdate", "");
		params.addBodyParameter("sort", "");
		params.addBodyParameter("nbr", keyword);
		params.addBodyParameter("pageno", "1");
		params.addBodyParameter("pagesize", "100");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA01AA&pcmd=Query&charset=utf8",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
						showErrorView(errorView, 
								getResources().getDrawable(R.drawable.ic_content_manager_sync), 
								"请求失败", 
								"请求数据失败，请重试");
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						InvoiceData data = SceoUtil.parseJson(responseInfo.result, InvoiceData.class);
						if (data.getStatus() == 0) {
							final List<InvoiceDetails> invoiceList = data.getData().getList();
							InvoiceAdapter invoiceAdapter = new InvoiceAdapter(InvoiceSearchActivity.this, invoiceList);
							resultListView.setAdapter(invoiceAdapter);
							
							resultListView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, 
										int position, long id) {
									Intent intent = new Intent(InvoiceSearchActivity.this, InvoiceDetailsActivity.class);
									intent.putExtra("details", (Serializable)invoiceList.get(position));
									startActivity(intent);
								}
							});
							
							if (invoiceList.size() == 0) {
								showErrorView(errorView, 
										getResources().getDrawable(R.drawable.ic_content_manager_visible), 
										"这里是空的", 
										"请更改条件或点击再试一次");
							}
						}else if (data.getStatus() == 88) {
							Toasty.error(InvoiceSearchActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(InvoiceSearchActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(InvoiceSearchActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
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
				errorView.setVisibility(View.GONE);
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
	
	private class MyOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.invoice_back_title_back:
				finish();
				break;
			case R.id.invoice_search_todo:
				memoryListView.setVisibility(View.GONE);
				resultListView.setVisibility(View.VISIBLE);
				
				keyword = clearEditText.getText().toString();
				getData(keyword);
				if (!StringUtil.isEmpty(keyword)) {
					saveKeyWord(keyword);
				}
				
				hideSoftInput(clearEditText);
				break;

			default:
				break;
			}
		}
		
	}
	
	private void saveKeyWord(String keyword){
		DB db = new DB(this);
		db.saveInvoiceInputs(keyword);
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
				DB db = new DB(InvoiceSearchActivity.this);
				List<String> list = db.getInvoiceInputs(keyword);
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
