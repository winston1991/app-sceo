package com.huntkey.software.sceo.ui.fragment;

import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.BaseFragment;
import com.huntkey.software.sceo.bean.AffairsData;
import com.huntkey.software.sceo.bean.AffairsDetails;
import com.huntkey.software.sceo.bean.eventbus.EventBusAffairsNumAndIdAndTask;
import com.huntkey.software.sceo.cache.ACache;
import com.huntkey.software.sceo.ui.activity.chat.ChatActivity;
import com.huntkey.software.sceo.ui.activity.chat.CreatChatRoomActivity;
import com.huntkey.software.sceo.ui.adapter.AffairsAdapter;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.QueryPopWindow;
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import es.dmoral.toasty.Toasty;

/**
 * 待办事务
 * @author chenliang3
 *
 */
public class AffairsFragment extends BaseFragment {

	@ViewInject(R.id.affairs_title)
	MainTitle title;
	@ViewInject(R.id.affairs_error)
	ErrorView errorView;
	@ViewInject(R.id.affairs_menuListView)
	PullToRefreshListView ptrListView;
	@ViewInject(R.id.affairs_load)
	RelativeLayout loadView;

	private ACache aCache;
	private String queryType = "1";//0为获取全部,1为获取未关闭条目,2为获取已关闭条目
	private int currentPage = 1;//当前页
	private String pageSize = "20";//一次请求数据的条数
	private AffairsAdapter adapter;
	private List<AffairsDetails> affairsList;
	private int totalRow;
	private Handler mHandler;
	boolean isFirst = true;
	private BroadcastReceiver broadcastReceiver;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		EventBus.getDefault().registerSticky(this);
		if (!EventBus.getDefault().isRegistered(this)){
			EventBusUtil.getInstence().register(this);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_affairs, null);
		ViewUtils.inject(this, view);
		
		init();
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						hideErrorView(errorView);
						getData(queryType, true, true);//获取数据
					}
				}, 10);
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (SceoUtil.getIsCreatNewAffairs(getActivity())) {//针对是否有新创建事务的操作
			getData(queryType, true, false);
			SceoUtil.setIsCreatNewAffairs(getActivity(), false);
		}
	}
	
	private void registerBroadCastReceiver(){
		broadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean isTrue = intent.getBooleanExtra("refreshFlag", false);
				if (isTrue) {
					if (affairsList != null) {					
						affairsList.clear();
					}
					currentPage = 1;
					getData(queryType, true, false);
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SceoUtil.ACTION_AFFAIRS_REFRESH_RECEIVER);
		getActivity().registerReceiver(broadcastReceiver, intentFilter);
	}

	private void init() {
		initTitle();
		aCache = ACache.get(getActivity());
		mHandler = new Handler();
		
		registerBroadCastReceiver();
		
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
					getData(queryType, true, false);
				}else if (ptrListView.isFooterShown()) {
					++currentPage;
					getData(queryType, false, false);
				}
			}
		});
		
		getData(queryType, true, true);//获取数据
	}
	
	private void initTitle() {
		title.setMainTitle("待办事务");
		title.setMainTitleColor(getResources().getColor(R.color.white));
		
		title.setTitlePublishVisible();
		title.setTitleQueryVisible();
		title.setTitlePublishClickLis(new OnClickListener() {//发起
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), CreatChatRoomActivity.class);
				intent.putExtra("ClFlag", "1");//提供给creatchatroomactivity以判断行为
				startActivity(intent);
			}
		});
		
		final QueryPopWindow queryPopWindow = new QueryPopWindow(getActivity());
		title.setTitleQueryClickLis(new OnClickListener() {//筛选
			
			@Override
			public void onClick(View v) {
				//弹出popwin，更改queryType
				queryPopWindow.showPopWindow(title.getQueryView());
				if (isFirst) {					
					queryPopWindow.setQUeryPopNotcloseEnable(true);
					isFirst = false;
				}
				queryPopWindow.setQueryPopAllClickLis(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						queryPopWindow.dismissPopWindow();
						queryType = "0";
						if (affairsList != null) {							
							affairsList.clear();
						}
						currentPage = 1;
						getData(queryType, true, false);
						queryPopWindow.setQUeryPopAllEnable(true);
						queryPopWindow.setQUeryPopNotcloseEnable(false);
						queryPopWindow.setQUeryPopCloseEnable(false);
					}
				});
				
				queryPopWindow.setQueryPopNotcloseClickLis(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						queryPopWindow.dismissPopWindow();
						queryType = "1";
						if (affairsList != null) {							
							affairsList.clear();
						}
						currentPage = 1;
						getData(queryType, true, false);
						queryPopWindow.setQUeryPopAllEnable(false);
						queryPopWindow.setQUeryPopNotcloseEnable(true);
						queryPopWindow.setQUeryPopCloseEnable(false);
					}
				});
				
				queryPopWindow.setQueryPopCloseClickLis(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						queryPopWindow.dismissPopWindow();
						queryType = "2";
						if (affairsList != null) {							
							affairsList.clear();
						}
						currentPage = 1;
						getData(queryType, true, false);
						queryPopWindow.setQUeryPopAllEnable(false);
						queryPopWindow.setQUeryPopNotcloseEnable(false);
						queryPopWindow.setQUeryPopCloseEnable(true);
					}
				});
			}
		});
	}
	
	private void getData(String queryTerms, final boolean isRefresh, final boolean isLoadingShow){
		hideErrorView(errorView);
		
		if (!hasNetWork()) {
			showErrorView(errorView, 
					getResources().getDrawable(R.drawable.ic_content_manager_wifi), 
					"网络请求失败", 
					"请打开您的数据连接并重试");
		}else {
			doNetWork(queryTerms, isRefresh, isLoadingShow);
		}
	}
	
	/**
	 * 加载数据
	 */
	private void doNetWork(String queryTerms, final boolean isRefresh, final boolean isLoadingShow){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getActivity()));
		params.addBodyParameter("emp_id", SceoUtil.getEmpCode(getActivity()));
		params.addBodyParameter("queryterms", queryTerms);
		params.addBodyParameter("pageno", currentPage+"");
		params.addBodyParameter("pagesize", pageSize);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA05AA&pcmd=GetTaskList",
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
				
				AffairsData affairsData = SceoUtil.parseJson(responseInfo.result, AffairsData.class);
				if (affairsData.getStatus() == 0) {
					totalRow = affairsData.getData().getPage().getRowcount();
					if (isRefresh) {
						if (affairsList != null) {
							affairsList.clear();
						}
						affairsList = affairsData.getData().getTasklist();
					}else {
						affairsList.addAll(affairsData.getData().getTasklist());
					}
					
					if (adapter == null) {
						adapter = new AffairsAdapter(getActivity(), affairsList);
						ptrListView.setAdapter(adapter);
					}else {
						adapter.updateList(affairsList);
						adapter.notifyDataSetChanged();
					}
					
					if (adapter.getCount() < totalRow) {
						ptrListView.setMode(Mode.BOTH);
					}else {
						ptrListView.setMode(Mode.PULL_FROM_START);
					}
					
					ptrListView.setOnItemClickListener(new OnItemClickListener() {
						
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Intent intent = new Intent(getActivity(), ChatActivity.class);
							intent.putExtra("taskId", affairsList.get(position-1).getTaskid());
							intent.putExtra("taskName", affairsList.get(position-1).getTaskname());
							intent.putExtra("peopleNum", affairsList.get(position-1).getEmpcount());
							startActivity(intent);
							
							aCache.put("tempTaskId", affairsList.get(position-1).getTaskid()+"");//暂存taskid给service使用
							
							//将右上角的图标值改为0
							affairsList.get(position-1).setUnreadno(0);
							adapter.updateList(affairsList);
							adapter.notifyDataSetChanged();
							//发送广播，更改mainActivity中未读消息数
						}
					});
					
					if (affairsList.size() == 0) {
						showErrorView(errorView, 
								getResources().getDrawable(R.drawable.ic_content_manager_visible), 
								"这里是空的", 
								"请更改条件或点击再试一次");
					}
				}else if (affairsData.getStatus() == 88) {
					Toasty.error(getActivity(), affairsData.getMessage(), Toast.LENGTH_SHORT, true).show();
					SceoUtil.gotoLogin(getActivity());
					SceoApplication.getInstance().exit();
				}else {
					Toasty.error(getActivity(), affairsData.getMessage(), Toast.LENGTH_SHORT, true).show();
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
	public void onDestroy() {
		super.onDestroy();
		if (broadcastReceiver != null) {
			getActivity().unregisterReceiver(broadcastReceiver);
		}
		if (EventBus.getDefault().isRegistered(this)){
			EventBusUtil.getInstence().unregister(this);
		}
	}
	
	/**
	 * 当在设置界面更改了参与用户时，在这里接收EventBus传过来的消息以更改当前列表中事务的参与人数
	 * @param numAndIdAndTask
	 */
	@Subscribe
	public void onEventMainThread(EventBusAffairsNumAndIdAndTask numAndIdAndTask){
		for (AffairsDetails details : affairsList) {
			if (details.getTaskid() == numAndIdAndTask.getTaskid()) {
				details.setEmpcount(numAndIdAndTask.getaEmpCount());
				if (numAndIdAndTask.getLasttask() != null && !"".equals(numAndIdAndTask.getLasttask())) {
					details.setLasttask(numAndIdAndTask.getLasttask());
				}
			}
		}
		adapter.notifyDataSetChanged();
	}

}
