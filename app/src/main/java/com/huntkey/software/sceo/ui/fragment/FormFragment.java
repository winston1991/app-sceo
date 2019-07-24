package com.huntkey.software.sceo.ui.fragment;

import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseFragment;
import com.huntkey.software.sceo.entity.FormMenuData;
import com.huntkey.software.sceo.entity.FormMenuItem;
import com.huntkey.software.sceo.ui.activity.forms.FormActivity;
import com.huntkey.software.sceo.ui.adapter.FormMenuAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
import com.huntkey.software.sceo.widget.MainTitle;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author chenliang3
 *
 */
public class FormFragment extends BaseFragment {

	@ViewInject(R.id.fform_errorView)
	ErrorView errorView;
	@ViewInject(R.id.fform_title)
	MainTitle title;
	@ViewInject(R.id.fform_ptrListview)
	PullToRefreshListView listView;
	@ViewInject(R.id.fform_load)
	RelativeLayout loadView;
	
	private FormMenuAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_form, null);
		ViewUtils.inject(this, view);
		
		initTitleAndErrorView();
		initView();
		
		return view;
	}

	private void initView() {
		listView.setMode(Mode.DISABLED);
		getData();
	}

	private void getData(){
		if (!hasNetWork()) {
			showErrorView(errorView, 
					getResources().getDrawable(R.drawable.ic_content_manager_wifi), 
					"网络请求失败", 
					"请打开您的数据连接并重试");
		}else {
			doNetWork();
		}
	}
	
	private void doNetWork() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getActivity()));
		HttpUtils http = new HttpUtils();
		http.configSoTimeout(30 * 1000);
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=MgrList",
				params, 
				new RequestCallBack<String>() {
			
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				loadView.setVisibility(View.GONE);
				showErrorView(errorView, 
						getResources().getDrawable(R.drawable.ic_content_manager_sync), 
						"请求失败", 
						"请求数据失败，请重试");
			}
			
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				loadView.setVisibility(View.GONE);
				
				FormMenuData data = SceoUtil.parseJson(responseInfo.result, FormMenuData.class);
				if (data != null && data.getRows() != null && data.getRows().size() > 0) {
					final List<FormMenuItem> formMenuItems = data.getRows();
					if (adapter == null) {
						adapter = new FormMenuAdapter(getActivity(), formMenuItems);
						listView.setAdapter(adapter);
					}else {
						adapter.updateList(formMenuItems);
						adapter.notifyDataSetChanged();
					}
					listView.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
												View view, int position, long id) {
							Intent intent = new Intent(getActivity(), FormActivity.class);
							intent.putExtra("code", formMenuItems.get(position - 1).getKmam_code());
							intent.putExtra("title", formMenuItems.get(position - 1).getKmam_desc());
							intent.putExtra("issys", ""+formMenuItems.get(position - 1).getIssys());
							startActivity(intent);
						}
					});
				}else {
					showErrorView(errorView,
							getResources().getDrawable(R.drawable.ic_content_manager_visible),
							"这里是空的",
							"您还没有数据，请添加数据并重试");
				}
			}
			
			@Override
			public void onStart() {
				super.onStart();
				loadView.setVisibility(View.VISIBLE);
			}
		});
	}
	
	private void initTitleAndErrorView() {
		title.setMainTitle("即时报表");
		title.setMainTitleColor(getResources().getColor(R.color.white));

		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				hideErrorView(errorView);
				getData();
			}
		});
	}

}
