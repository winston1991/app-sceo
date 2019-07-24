package com.huntkey.software.sceo.ui.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.BaseFragment;
import com.huntkey.software.sceo.bean.ContantData;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.ui.activity.linkman.BooksActivity;
import com.huntkey.software.sceo.ui.activity.empdetails.EmpDetailsActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
import com.huntkey.software.sceo.widget.sortlist.CharacterParser;
import com.huntkey.software.sceo.widget.sortlist.PinyinComparator;
import com.huntkey.software.sceo.widget.sortlist.SideBar;
import com.huntkey.software.sceo.widget.sortlist.SideBar.OnTouchingLetterChangedListener;
import com.huntkey.software.sceo.widget.sortlist.SortAdapter;
import com.huntkey.software.sceo.widget.sortlist.SortModel;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * 常用联系人
 * @author chenliang3
 *
 */
public class LinkmanFragment extends BaseFragment {

	@ViewInject(R.id.linkman_title)
	BackTitle title;
	@ViewInject(R.id.linkman_clear_edittext)
	ClearEditText clearEditText;
	@ViewInject(R.id.linkman_listview)
	ListView sortListView;
	@ViewInject(R.id.linkman_dialog)
	TextView dialog;
	@ViewInject(R.id.linkman_sidebar)
	SideBar sideBar;
	@ViewInject(R.id.linkman_goto_books)
	TextView gotoBooks;
	
	@ViewInject(R.id.linkman_error)
	ErrorView errorView;
	
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;
	private SortAdapter adapter;
	private List<SortModel> SourceDateList;
	private LoadingDialog loadingDialog;
	private DbUtils db;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_linkman, null);
		ViewUtils.inject(this, view);
		
		db = DbUtils.create(getActivity());
		
		initView();
		initControl();
		
		loadingDialog = new LoadingDialog(getActivity());
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						hideErrorView(errorView);
						doNetWork();
					}
				}, 10);
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (SceoUtil.getIsCollectChange(getActivity())) {			
//			doNetWork();//获取联系人信息
			loadDataFromDb();
			SceoUtil.setIsCollectChange(getActivity(), false);
		}
	}

	private void initView() {
		title.setBackTitle("联系人");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
		
		gotoBooks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), BooksActivity.class);
				startActivity(intent);
			}
		});
	}

	private void initControl() {
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		
		sideBar.setTextView(dialog);
		//设置右侧sidebar触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}
			}
		});
		
		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象
				Intent intent = new Intent(getActivity(), EmpDetailsActivity.class);
				intent.putExtra("empCode", ((SortModel)adapter.getItem(position)).getEmp_id());
				startActivity(intent);
			}
		});
		
		//根据输入框输入值的改变来过滤搜索
		clearEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
//		//第一次安装应用，从服务端获取数据，之后均从本地服务器获取数据
//		if (SceoUtil.getOnceWork(getActivity())) {
			doNetWork();//获取联系人信息//------------------现在处理逻辑是第一次进入应用均从服务器获取数据
//			SceoUtil.setOnceWork(getActivity(), false);
//		}else {
//			loadDataFromDb();
//		}
	}

	/**
	 * 获取联系人信息
	 */
	private void doNetWork() {
		hideErrorView(errorView);
		
		if (!hasNetWork()) {
			showErrorView(errorView, 
					getResources().getDrawable(R.drawable.ic_content_manager_wifi), 
					"网络请求失败", 
					"请打开您的数据连接并重试");
		}else {
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getActivity()));
			params.addBodyParameter("emp_id", SceoUtil.getEmpCode(getActivity()));
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA06AA&pcmd=getMyFriends",
					params, 
					new RequestCallBack<String>() {
				
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					loadingDialog.dismiss();
					errorView.setVisibility(View.VISIBLE);
					showErrorView(errorView, 
							getResources().getDrawable(R.drawable.ic_content_manager_sync), 
							"请求失败", 
							"请求数据失败，请重试");
				}
				
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					loadingDialog.dismiss();
					
					ContantData data = SceoUtil.parseJson(responseInfo.result, ContantData.class);
					if (data.getStatus() == 0) {
						//进入应用从服务器获取数据，删除之前保存在数据库中的数据
						try {
							List<SortModel> models2db = db.findAll(Selector.from(SortModel.class).where("personalId", "=", SceoUtil.getEmpCode(getActivity())));
							db.deleteAll(models2db);
						} catch (DbException e1) {
							e1.printStackTrace();
						}
						
						List<SortModel> models = data.getData().getMyfriends();
						
						SortModel model = new SortModel();
						model.setEmp_id(SceoUtil.getEmpCode(getActivity()));
						model.setEmp_name(SceoUtil.getEmpName(getActivity()));
						model.setEmp_photo(SceoUtil.getEmpPhoto(getActivity()));
						model.setSmsflag(0);
						
						models.add(model);
						if (models != null && models.size() > 0) {
							for (int i = 0; i < models.size(); i++) {
								models.get(i).setPersonalId(SceoUtil.getEmpCode(getActivity()));
							}
							try {
								db.saveAll(models);
							} catch (DbException e) {
								e.printStackTrace();
							}
						}
						
						SourceDateList = filledData(models);
						
						//根据a-z进行源数据排序
						Collections.sort(SourceDateList, pinyinComparator);
						adapter = new SortAdapter(getActivity(), SourceDateList);
						sortListView.setAdapter(adapter);
						
						if (models.size() == 0) {
							showErrorView(errorView, 
									getResources().getDrawable(R.drawable.ic_content_manager_visible), 
									"这里是空的", 
									"您还没有数据，请添加数据并重试");
						}
					}else {
						Toasty.error(getActivity(), data.getMessage(), Toast.LENGTH_SHORT, true).show();
						SceoUtil.gotoLogin(getActivity());
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
	
	private void loadDataFromDb(){
		try {
			List<SortModel> models = db.findAll(Selector.from(SortModel.class).where("personalId", "=", SceoUtil.getEmpCode(getActivity())));
			if (models != null && models.size() > 0) {
				SourceDateList = filledData(models);
				Collections.sort(SourceDateList, pinyinComparator);
				adapter = new SortAdapter(getActivity(), SourceDateList);
				sortListView.setAdapter(adapter);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 为ListView填充数据
	 * @param list
	 * @return
	 */
	private List<SortModel> filledData(List<SortModel> list){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		
		for(int i=0; i<list.size(); i++){
			SortModel sortModel = new SortModel();
			sortModel.setEmp_id(list.get(i).getEmp_id());
			sortModel.setEmp_cellphone(list.get(i).getEmp_cellphone());
			sortModel.setEmp_photo(list.get(i).getEmp_photo());
			sortModel.setEmp_sex(list.get(i).getEmp_sex());
			sortModel.setSmsflag(list.get(i).getSmsflag());
			
			sortModel.setEmp_name(list.get(i).getEmp_name());
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(list.get(i).getEmp_name());
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
			
			mSortList.add(sortModel);
		}
		return mSortList;
		
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getEmp_name();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		
		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}
	
}
