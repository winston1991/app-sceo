package com.huntkey.software.sceo.ui.activity.linkman;

import java.util.ArrayList;
import java.util.List;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.bean.TreeData;
import com.huntkey.software.sceo.bean.TreeElement;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.activity.empdetails.EmpDetailsActivity;
import com.huntkey.software.sceo.ui.activity.search.SearchActivity;
import com.huntkey.software.sceo.ui.adapter.TreeViewAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.TreeView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
import com.huntkey.software.sceo.widget.TreeView.LastLevelItemClickListener;
import com.huntkey.software.sceo.widget.TreeView.ParentLevelItemClickListener;
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
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * 企业通讯录
 * @author chenliang3
 *
 */
public class BooksActivity extends BaseActivity {

	@ViewInject(R.id.books_title)
	BackTitle title;
	@ViewInject(R.id.books_search_layout)
	RelativeLayout searchLayout;
	@ViewInject(R.id.books_tree)
	TreeView treeList;
	@ViewInject(R.id.books_error)
	ErrorView errorView;
	private LoadingDialog loadingDialog;
	
	private List<TreeElement> treeData = new ArrayList<TreeElement>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_books);
		ViewUtils.inject(this);
		
		initView();
		initControl();
		
		loadingDialog = new LoadingDialog(BooksActivity.this);
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						hideErrorView(errorView);
						treeData.clear();
						initControl();
					}
				}, 10);
			}
		});
	}
	
	private void initControl() {
		initMainData();//加载"航嘉机构"
		
		treeList.setLastLevelItemClickCallBack(itemClickCallBack);
		treeList.setParentLevelItemClickCallBack(pItemClickCallBack);		
	}
	
	//子节点(员工)点击事件监听
	LastLevelItemClickListener itemClickCallBack = new LastLevelItemClickListener() {
		
		@Override
		public void onLastLevelItemClick(int position, TreeViewAdapter adapter) {
			TreeElement element = (TreeElement) adapter.getItem(position);
			Intent intent = new Intent(BooksActivity.this, EmpDetailsActivity.class);
			intent.putExtra("empCode", element.getCc_code());//根据工号拿数据

			startActivity(intent);
		}
	};
	
	//父节点（部门）点击事件监听

	ParentLevelItemClickListener pItemClickCallBack = new ParentLevelItemClickListener() {
		
		@Override
		public void onParentItemClick(int position, TreeViewAdapter adapter) {
			TreeElement element = (TreeElement) adapter.getItem(position);
			if (!element.isEx()) {//判断当前父节点是否是第一次加载子数据（保证数据只加载一次）
				getData(element.getCc_code(), element.getCc_level(), position);//获取父节点下的数据

				element.setEx(true);
			}
		}
	};
	
	/**
	 * 加载"航嘉机构"(最最外层的一级)
	 */
	private void initMainData(){
		hideErrorView(errorView);
		
		if (!hasNetWork()) {
			showErrorView(errorView, 
					getResources().getDrawable(R.drawable.ic_content_manager_wifi), 
					"网络请求失败", 
					"请打开您的数据连接并重试");
		}else {			
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(BooksActivity.this));
			params.addBodyParameter("cc_code", "");
			params.addBodyParameter("is_first", "1");
			params.addBodyParameter("cc_level", "");
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL+"sceosrv/csp/sceosrv.dll?page=EAA04AA&pcmd=getDeptTreeNode",
					params,
					new RequestCallBack<String>() {
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					showErrorView(errorView, 
							getResources().getDrawable(R.drawable.ic_content_manager_sync), 
							"请求失败", 
							"请求数据失败，请重试");
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					TreeData data = SceoUtil.parseJson(responseInfo.result, TreeData.class);
					if (data.getStatus() == 0) 
					{
						for (int i = 0; i < data.getData().getTreelist().size(); i++) {
							data.getData().getTreelist().get(i).setHasParent(false);
							data.getData().getTreelist().get(i).setHasChild(true);
							data.getData().getTreelist().get(i).setLevel(1);
							data.getData().getTreelist().get(i).setFold(true);
							data.getData().getTreelist().get(i).setParentId(null);
							treeData.add(data.getData().getTreelist().get(i));
						}							
						treeList.initData(BooksActivity.this, treeData);
						
						if(treeData.get(0) != null){								
							TreeElement element = (TreeElement)treeList.getAdapter().getItem(0);							
							if (!element.isEx()) 
							{//判断当前父节点是否是第一次加载子数据（保证数据只加载一次）
								getData(element.getCc_code(), element.getCc_level(), 0);//获取父节点下的数据
								
								element.setEx(true);
							}
						}
					}else {
						Toasty.error(BooksActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						SceoUtil.gotoLogin(BooksActivity.this);
						SceoApplication.getInstance().exit();
					}
				}
				
				@Override
				public void onStart() {
					super.onStart();
				}
				
			});
		}
	}

	/**
	 * 加载父节点下的子节点数据
	 */
	private void getData(final String code, final String level, final int position){
		hideErrorView(errorView);
		
		if (!hasNetWork()) {
			showErrorView(errorView, 
					getResources().getDrawable(R.drawable.ic_content_manager_wifi), 
					"网络请求失败", 
					"请打开您的数据连接并重试");
		}else {			
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(BooksActivity.this));
			params.addBodyParameter("cc_code", code);
			params.addBodyParameter("is_first", "0");
			params.addBodyParameter("cc_level", level);
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL+"sceosrv/csp/sceosrv.dll?page=EAA04AA&pcmd=getDeptTreeNode",
					params, 
					new RequestCallBack<String>() {
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					showErrorView(errorView, 
							getResources().getDrawable(R.drawable.ic_content_manager_sync), 
							"请求失败", 
							"请求数据失败，请重试");
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					loadingDialog.dismiss();
					
					TreeData data = SceoUtil.parseJson(responseInfo.result, TreeData.class);
					if (data.getStatus() == 0) {
						for (int i = 0; i < data.getData().getTreelist().size(); i++) {
							int tmpLevel = Integer.parseInt(data.getData().getTreelist().get(i).getCc_level());
							
							if (data.getData().getTreelist().get(i).getFlag() == 1) {
								data.getData().getTreelist().get(i).setHasParent(true);
								data.getData().getTreelist().get(i).setHasChild(false);
								data.getData().getTreelist().get(i).setLevel(tmpLevel);
								data.getData().getTreelist().get(i).setFold(false);
								data.getData().getTreelist().get(i).setParentId(code);
								treeData.add(data.getData().getTreelist().get(i));
							}else {
								data.getData().getTreelist().get(i).setHasParent(true);
								data.getData().getTreelist().get(i).setHasChild(true);
								data.getData().getTreelist().get(i).setLevel(tmpLevel);
								data.getData().getTreelist().get(i).setFold(false);
								data.getData().getTreelist().get(i).setParentId(code);
								treeData.add(data.getData().getTreelist().get(i));
							}
						}
						treeList.updateData(BooksActivity.this, treeData, position, code);
					}else {
						Toasty.error(BooksActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						SceoUtil.gotoLogin(BooksActivity.this);
						SceoApplication.getInstance().exit();
					}
				}
				
				@Override
				public void onStart() {
					super.onStart();
					loadingDialog.show();
				}
			});
		}
	}

	private void initView() {
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				BooksActivity.this.finish();
			}
		});
		title.setBackTitle("企业通讯录");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		
		gotoSearchClick();
	}
	
	private void gotoSearchClick(){
		searchLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(BooksActivity.this, SearchActivity.class));
			}
		});
	}
	
	
}
