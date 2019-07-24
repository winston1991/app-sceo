package com.huntkey.software.sceo.ui.fragment;

import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.BaseFragment;
import com.huntkey.software.sceo.bean.InvoiceData;
import com.huntkey.software.sceo.bean.InvoiceDetails;
import com.huntkey.software.sceo.ui.activity.invoicedetails.InvoiceDetailsActivity;
import com.huntkey.software.sceo.ui.activity.search.InvoiceSearchActivity;
import com.huntkey.software.sceo.ui.adapter.InvoiceAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.TimeUtil;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
import com.huntkey.software.sceo.widget.MainTitle;
import com.huntkey.software.sceo.widget.dropmenu.DropDownMenu;
import com.huntkey.software.sceo.widget.dropmenu.OnMenuSelectedListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * 
 * 待审单据
 * @author chenliang3
 *
 */
public class InvoiceFragment extends BaseFragment {

	@ViewInject(R.id.invoice_title)
	MainTitle title;
	@ViewInject(R.id.invoice_menu)
	DropDownMenu menu;
	@ViewInject(R.id.invoice_xlistView)
	PullToRefreshListView ptrListView;
	@ViewInject(R.id.invoice_error)
	ErrorView errorView;
	@ViewInject(R.id.invoice_load)
	RelativeLayout loadView;
	
	private int typeIndex = 1;
	private String timeIndex = "";
	private String orderIndex = "";
	private int currentPage = 1;
	private String pageSize = "20";
	private int totalRow;
	private List<InvoiceDetails> invoiceList;
	private InvoiceAdapter adapter;
	private Handler mHandler;
	private int REQUEST_CODE = 0x23;//跳转到invoiceDetailsActivity请求码
	private int RESULT_CODE = 0x24;//当“待知会”情况下从invoiceDetailsActivity获得的返回码
	private int tmpPosition;
	
	final String[] typeArr = new String[]{"待办","发起","已审","退回","其他"};
	final String[] timeArr = new String[]{"提交时间","一周内","一月内","三月内","今日"};
	final String[] orderArr = new String[]{"默认排序","时间升序","时间降序","名称排序"};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_invoice, null);
		ViewUtils.inject(this, view);//注入view
		
		initTitleAndErrorView();
		initMenu();
		initView();
		
		return view;
	}

	private void initView() {
		mHandler = new Handler();
		
		ptrListView.setMode(Mode.BOTH);
		ptrListView.getLoadingLayoutProxy(false, true).setPullLabel("加载更多");
		ptrListView.getLoadingLayoutProxy(false, true).setReleaseLabel("松开开始加载更多");
		ptrListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
		
		ptrListView.getLoadingLayoutProxy(true, false).setPullLabel("下拉刷新");
		ptrListView.getLoadingLayoutProxy(true, false).setReleaseLabel("松开开始刷新");
		ptrListView.getLoadingLayoutProxy(true, false).setRefreshingLabel("正在刷新...");
		
		ptrListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				if (ptrListView.isHeaderShown()) {
					currentPage = 1;
					getData(typeIndex, timeIndex, orderIndex, true, false);
				}else if (ptrListView.isFooterShown()) {
					++currentPage;
					getData(typeIndex, timeIndex, orderIndex, false, false);
				}
			}
		});
		
		getData(typeIndex, timeIndex, orderIndex, true, true);
	}
	
	private void getData(int typeIndex2, String timeIndex2, String orderIndex2,
			final boolean isRefresh, final boolean isLoadingShow){
		hideErrorView(errorView);
		
		if (!hasNetWork()) {
			showErrorView(errorView, 
					getResources().getDrawable(R.drawable.ic_content_manager_wifi), 
					"网络请求失败", 
					"请打开您的数据连接并重试");
		}else {
			doNetWork(typeIndex2, timeIndex2, orderIndex2, isRefresh, isLoadingShow);
		}
	}

	/**
	 * 后台获取数据
	 * @param typeIndex2  查询类型
	 * @param timeIndex2  查询时间
	 * @param orderIndex2  查询排序
	 * @param isRefresh  true为刷新,false为加载更多
	 */
	private void doNetWork(int typeIndex2, String timeIndex2, String orderIndex2,
			final boolean isRefresh, final boolean isLoadingShow) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getActivity()));
		params.addBodyParameter("tasktype", typeIndex2+"");
		params.addBodyParameter("begdate", timeIndex2+"");
		params.addBodyParameter("sort", orderIndex2+"");
		params.addBodyParameter("nbr", "");//输入单号查询(暂不用)
		params.addBodyParameter("pageno", currentPage+"");
		params.addBodyParameter("pagesize", pageSize+"");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA01AA&pcmd=Query",
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
				ptrListView.onRefreshComplete();
				loadView.setVisibility(View.GONE);
				
				InvoiceData data = SceoUtil.parseJson(responseInfo.result, InvoiceData.class);
				if (data.getStatus() == 0) {
					totalRow = data.getData().getPage().getRowcount();
					
					if (isRefresh) {
						if (invoiceList != null) {
							invoiceList.clear();
						}
						invoiceList = data.getData().getList();
					}else {
						invoiceList.addAll(data.getData().getList());
					}
					if (adapter == null) {
						adapter = new InvoiceAdapter(getActivity(), invoiceList);
						ptrListView.setAdapter(adapter);
					}else {
						adapter.updateList(invoiceList);
						adapter.notifyDataSetChanged();
					}
					
					if (adapter.getCount() < totalRow) {
						ptrListView.setMode(Mode.BOTH);
					}else {
						ptrListView.setMode(Mode.PULL_FROM_START);
					}
					
					ptrListView.setOnItemClickListener(new OnItemClickListener() {
						
						@Override
						public void onItemClick(AdapterView<?> parent, View view, 
								int position, long id) {
							tmpPosition = position-1;
							
							Intent intent = new Intent(getActivity(), InvoiceDetailsActivity.class);
							intent.putExtra("details", (Serializable)invoiceList.get(position-1));
							startActivityForResult(intent, REQUEST_CODE);
						}
					});
					
					if (invoiceList.size() == 0) {
						showErrorView(errorView, 
								getResources().getDrawable(R.drawable.ic_content_manager_visible), 
								"这里是空的", 
								"请更改条件或点击再试一次");
					}
				}else if (data.getStatus() == 88) {
					Toasty.error(getActivity(), data.getMessage(), Toast.LENGTH_SHORT, true).show();
					SceoUtil.gotoLogin(getActivity());
					SceoApplication.getInstance().exit();
				}else {
					Toasty.error(getActivity(), data.getMessage(), Toast.LENGTH_SHORT, true).show();
				}
			}
			
			@Override
			public void onStart() {
				super.onStart();
				if (isLoadingShow) {
					loadView.setVisibility(View.VISIBLE);
				}else {
					ptrListView.setRefreshing();
				}
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				if (data.getBooleanExtra("isChange", false)) {	
					int stepId = data.getIntExtra("stepId", SceoUtil.INT_NEGATIVE)+1;
					String name = data.getStringExtra("name");
					String color = data.getStringExtra("color");
					
					switch (stepId) {
					case 1:
						invoiceList.get(tmpPosition).setColor_n1(color);
						invoiceList.get(tmpPosition).setShowname_n1(name);
						break;
					case 2:
						invoiceList.get(tmpPosition).setColor_n2(color);
						invoiceList.get(tmpPosition).setShowname_n2(name);
						break;
					case 3:
						invoiceList.get(tmpPosition).setColor_n3(color);
						invoiceList.get(tmpPosition).setShowname_n3(name);
						break;
					case 4:
						invoiceList.get(tmpPosition).setColor_n4(color);
						invoiceList.get(tmpPosition).setShowname_n4(name);
						break;

					default:
						break;
					}
					
					invoiceList.get(tmpPosition).setColor_nbr("#333333");
					invoiceList.get(tmpPosition).setAid(0);
					invoiceList.get(tmpPosition).setNid(0);
					adapter.updateList(invoiceList);
					adapter.notifyDataSetChanged();
				}
			}else if (resultCode == RESULT_CODE) {
				int BstepId = data.getIntExtra("BstepId", SceoUtil.INT_NEGATIVE);
				String BempName = data.getStringExtra("BempName");
				String Bcolor = data.getStringExtra("Bcolor");
				switch (BstepId) {
				case 1:
					invoiceList.get(tmpPosition).setColor_n1(Bcolor);
					invoiceList.get(tmpPosition).setShowname_n1(BempName);
					break;
				case 2:
					invoiceList.get(tmpPosition).setColor_n2(Bcolor);
					invoiceList.get(tmpPosition).setShowname_n2(BempName);
					break;
				case 3:
					invoiceList.get(tmpPosition).setColor_n3(Bcolor);
					invoiceList.get(tmpPosition).setShowname_n3(BempName);
					break;
				case 4:
					invoiceList.get(tmpPosition).setColor_n4(Bcolor);
					invoiceList.get(tmpPosition).setShowname_n4(BempName);
					break;

				default:
					break;
				}
				
				adapter.updateList(invoiceList);
				adapter.notifyDataSetChanged();
			}
		}
	}

	private void initMenu() {
		menu.setMenuCount(3);
		menu.setShowCount(6);
		menu.setShowCheck(true);
		menu.setMenuTitleTextSize(16);
		menu.setMenuTitleTextColor(getResources().getColor(R.color.text_color_normal));
		menu.setMenuListTextSize(16);
		menu.setMenuListTextColor(getResources().getColor(R.color.text_color_normal));
		menu.setCheckIcon(R.drawable.ico_make);
		menu.setUpArrow(R.drawable.arrow_up);
		menu.setDownArrow(R.drawable.arrow_down);
		
		//记录menu点击状态
		int theType = SceoUtil.getDropmenuType(getActivity());
		int theTime = SceoUtil.getDropmenuTime(getActivity());
		int theOrder = SceoUtil.getDropmenuOrder(getActivity());
		
		menu.setMenuSelection1(0, theType);
		switch (theType) {
		case 0:
			typeIndex = 1;
			break;
		case 1:
			typeIndex = 3;
			break;
		case 2:
			typeIndex = 4;
			break;
		case 3:
			typeIndex = 5;
			break;
		case 4:
			typeIndex = 2;
			break;

		default:
			break;
		}
		
		menu.setMenuSelection2(1, theTime);
		String currentDate = TimeUtil.getCurrentYMD();
		switch (theTime) {
		case 0:
			timeIndex = "";
			break;
		case 1:
			timeIndex = TimeUtil.checkOption("pre", currentDate, 6);
			break;
		case 2:
			try {
				timeIndex = TimeUtil.subMonth(currentDate, -1);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				timeIndex = TimeUtil.subMonth(currentDate, -3);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;
		case 4:
			timeIndex = currentDate;
			break;

		default:
			break;
		}
			
		menu.setMenuSelection3(2, theOrder);
		switch (theOrder) {
		case 0:
			orderIndex = "";//默认排序
			break;
		case 1:
			orderIndex = "wfna_addtime";//时间升序
			break;
		case 2:
			orderIndex = "wfna_addtime desc";//时间降序
			break;
		case 3:
			orderIndex = "wfm_name";//名称
			break;

		default:
			break;
		}
		
		List<String[]> menuItems = new ArrayList<String[]>();
		menuItems.add(typeArr);
		menuItems.add(timeArr);
		menuItems.add(orderArr);
		menu.setMenuItems(menuItems);
		
		menu.setMenuSelectedListener(new OnMenuSelectedListener() {
			
			@Override
			public void onSelected(View listview, int RowIndex, int ColumnIndex) {
				if (ColumnIndex == 0) {
					SceoUtil.setDropmenuType(getActivity(), RowIndex);
					switch (RowIndex) {
					case 0:
						typeIndex = 1;
						break;
					case 1:
						typeIndex = 3;
						break;
					case 2:
						typeIndex = 4;
						break;
					case 3:
						typeIndex = 5;
						break;
					case 4:
						typeIndex = 2;
						break;

					default:
						break;
					}
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							if (invoiceList != null) {
								invoiceList.clear();
							}
							currentPage = 1;
							getData(typeIndex, timeIndex, orderIndex, true, false);
						}
					}, 10);
				}else if (ColumnIndex == 1) {
					SceoUtil.setDropmenuTime(getActivity(), RowIndex);
					String currentDate = TimeUtil.getCurrentYMD();
					switch (RowIndex) {
					case 0:
						timeIndex = "";
						break;
					case 1:
						timeIndex = TimeUtil.checkOption("pre", currentDate, 6);
						break;
					case 2:
						try {
							timeIndex = TimeUtil.subMonth(currentDate, -1);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						break;
					case 3:
						try {
							timeIndex = TimeUtil.subMonth(currentDate, -3);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						break;
					case 4:
						timeIndex = currentDate;
						break;

					default:
						break;
					}
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							if (invoiceList != null) {
								invoiceList.clear();
							}
							currentPage = 1;
							getData(typeIndex, timeIndex, orderIndex, true, false);
						}
					}, 10);
				}else {
					SceoUtil.setDropmenuOrder(getActivity(), RowIndex);
					switch (RowIndex) {
					case 0:
						orderIndex = "";//默认排序
						break;
					case 1:
						orderIndex = "wfna_addtime";//时间升序
						break;
					case 2:
						orderIndex = "wfna_addtime desc";//时间降序
						break;
					case 3:
						orderIndex = "wfm_name";//名称
						break;

					default:
						break;
					}
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							if (invoiceList != null) {
								invoiceList.clear();
							}
							currentPage = 1;
							getData(typeIndex, timeIndex, orderIndex, true, false);
						}
					}, 10);
				}
			}
		});
	}

	private void initTitleAndErrorView() {
		title.setMainTitle("待审单据");
		title.setMainTitleColor(getResources().getColor(R.color.white));
		title.setTitleSearchVisible();
		title.setTitlePublishClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到搜索
				Intent intent = new Intent(getActivity(), InvoiceSearchActivity.class);
				startActivity(intent);
			}
		});
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						hideErrorView(errorView);
						getData(typeIndex, timeIndex, orderIndex, true, true);
					}
				}, 10);
			}
		});
	}
	
}
