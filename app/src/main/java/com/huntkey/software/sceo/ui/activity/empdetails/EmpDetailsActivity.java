package com.huntkey.software.sceo.ui.activity.empdetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.ChatSettingLinkman;
import com.huntkey.software.sceo.bean.EmpDetailsData;
import com.huntkey.software.sceo.bean.EmpDetailsInfo;
import com.huntkey.software.sceo.bean.EmpDetailsTelInfo;
import com.huntkey.software.sceo.bean.EmpDetailsWorkInfo;
import com.huntkey.software.sceo.cache.ACache;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.chat.ChatActivity;
import com.huntkey.software.sceo.ui.activity.photoview.SpaceImageDetailActivity;
import com.huntkey.software.sceo.ui.adapter.EmpDetailsAdapter;
import com.huntkey.software.sceo.ui.adapter.EmpDetailsSmsAdapter;
import com.huntkey.software.sceo.ui.adapter.EmpDetailsTelAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
import com.huntkey.software.sceo.widget.sortlist.SortModel;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog.OnSweetClickListener;
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

import es.dmoral.toasty.Toasty;

/**
 * 员工详情页
 * @author chenliang3
 *
 */
public class EmpDetailsActivity extends BaseActivity {

	@ViewInject(R.id.empdetails_title)
	BackTitle title;
	@ViewInject(R.id.empdetails_photo)
	ImageView photo;
	@ViewInject(R.id.empdetails_name)
	TextView name;
	@ViewInject(R.id.empdetails_code)
	TextView code;
	@ViewInject(R.id.empdetails_collect)
	CheckBox collect;
	@ViewInject(R.id.empdetails_error)
	ErrorView errorView;
	
	@ViewInject(R.id.empdetails_work_list)
	ListView workList;
	@ViewInject(R.id.empdetails_tel_list)
	ListView telList;
	
	@ViewInject(R.id.empdetails_launch_affairs)
	Button launch_affairs;
	@ViewInject(R.id.empdetails_launch_sms)
	Button launch_sms;
	
	private String empCode;
	private List<EmpDetailsWorkInfo> workInfos = new ArrayList<EmpDetailsWorkInfo>();
	private List<EmpDetailsTelInfo> telInfos = new ArrayList<EmpDetailsTelInfo>();
	private ACache aCache;
	private View popView;
	private PopupWindow mPopupWindow;
	private ListView smsListView;
	private EmpDetailsData data;
	private List<ChatSettingLinkman> addLinkmans = new ArrayList<ChatSettingLinkman>();//为了传给chatActivity，先前传的是list，保持一致
	
	private boolean isFirst = true;
	private LoadingDialog loadingDialog;
	private DbUtils db;
	private EmpDetailsInfo info;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empdetails);
		ViewUtils.inject(this);//初始化，进行相关内容绑定
		
		empCode = getIntent().getStringExtra("empCode");
		aCache = ACache.get(EmpDetailsActivity.this);
		db = DbUtils.create(this);
		
		if (empCode.equals(SceoUtil.getEmpCode(EmpDetailsActivity.this))) {
			collect.setVisibility(View.GONE);
		}
		
		initView();
		
		loadingDialog = new LoadingDialog(EmpDetailsActivity.this);
	}

	private void initView() {
		title.setBackBtnClickLis(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish( );
			}
		});
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						errorView.setVisibility(View.GONE);
						doNetWork();//加载数据
					}
				}, 10);
			}
		});
		
		doNetWork();//加载数据
		doCollectControl();//收藏/取消
		launch_affairs.setOnClickListener(new MyOnclickListener());
//		launch_sms.setOnClickListener(new MyOnclickListener());
		initPop();
	}
	
	private void initPop() {
		LayoutInflater inflater = LayoutInflater.from(EmpDetailsActivity.this);
		popView = inflater.inflate(R.layout.popupwindow_sms, null);
		
		mPopupWindow = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.setOutsideTouchable(true);
		
		smsListView = (ListView) popView.findViewById(R.id.pop_sms_listview);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
		return super.onTouchEvent(event);
	}

	private class MyOnItemClickListener implements OnItemClickListener{

		private List<EmpDetailsTelInfo> tells;
		
		private MyOnItemClickListener(List<EmpDetailsTelInfo> telInfos){
			this.tells = telInfos;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int posiotion,
				long id) {
			 telDialog(tells.get(posiotion).getValue());
		}
		
	}
	
	private class MySmsOnItemClickListener implements OnItemClickListener{

		private List<EmpDetailsTelInfo> tells;
		
		private MySmsOnItemClickListener(List<EmpDetailsTelInfo> telInfos){
			this.tells = telInfos;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if ("1".equals(tells.get(position).getSmsflag())) {				
				smsDialog(tells.get(position).getValue());
			}else {
				Toasty.warning(EmpDetailsActivity.this, "请选择可接收短信的号码", Toast.LENGTH_SHORT, true).show();
			}
			mPopupWindow.dismiss();
		}
		
	}
	
	private class MyOnclickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.empdetails_launch_affairs://发起待办事务
				if (data != null) {
					ChatSettingLinkman linkman = new ChatSettingLinkman();
					linkman.setEmp_id(data.getData().getEmp_id());
					linkman.setEmp_name(data.getData().getEmp_name());
					linkman.setEmp_photo(data.getData().getPhoto());
					linkman.setRaiseflag(0);
					addLinkmans.add(linkman);
					Intent intent = new Intent(EmpDetailsActivity.this, ChatActivity.class);
					intent.putExtra("AddLinkmans", (Serializable)addLinkmans);
					intent.putExtra("taskName", "新事务");
					intent.putExtra("peopleNum", 2);
					startActivity(intent);
					EmpDetailsActivity.this.finish();
				}
				break;
			case R.id.empdetails_launch_sms://发短信
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N){
					mPopupWindow.showAtLocation(findViewById(R.id.empdetails_layout), Gravity.BOTTOM, 0, 0);
				}else {
					mPopupWindow.showAtLocation(findViewById(R.id.empdetails_layout), Gravity.LEFT, 0,
							EmpDetailsActivity.this.getWindow().getDecorView().getHeight()-350);
				}
				mPopupWindow.update();
				break;
			default:
				break;
			}
		}
		
	}

	/**
	 * 收藏/取消
	 */
	private void doCollectControl() {
		collect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				final RequestParams params = new RequestParams();
				if (collect.isChecked()) {
					params.addBodyParameter("flag", "1");
				}else {
					params.addBodyParameter("flag", "0");
				}
				params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(EmpDetailsActivity.this));
				params.addBodyParameter("emp_id", SceoUtil.getEmpCode(EmpDetailsActivity.this));
				params.addBodyParameter("friendid", empCode);
				HttpUtils http = new HttpUtils();
				http.configResponseTextCharset("GB2312");
				http.send(HttpRequest.HttpMethod.POST, 
						Conf.SERVICE_URL+"sceosrv/csp/sceosrv.dll?page=EAA04AD&pcmd=setMyFriend",
						params, 
						new RequestCallBack<String>() {
					
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						
					}
					
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						loadingDialog.dismiss();
						SceoUtil.setIsCollectChange(EmpDetailsActivity.this, true);//存储bool值，通知linkmanFragment刷新
						
						//操作数据库
						SortModel model = new SortModel();
						try {
							if (collect.isChecked()) {
								if (info != null) {
									List<EmpDetailsTelInfo> telInfos = info.getTelinfo();
									if (telInfos.size() > 0) {
										model.setEmp_cellphone(telInfos.get(0).getValue());
										if ("1".equals(telInfos.get(0).getSmsflag())) {
											model.setSmsflag(1);
										}
									}
									String empId = info.getEmp_id();
									if (empId != null && !"".equals(empId)) {
										model.setEmp_id(empId);
									}
									String empName = info.getEmp_name();
									if (empName != null && !"".equals(empName)) {
										model.setEmp_name(empName);
									}
									String empPhoto = info.getPhoto();
									if (empPhoto != null && !"".equals(empPhoto)) {
										model.setEmp_photo(empPhoto);
									}
									model.setPersonalId(SceoUtil.getEmpCode(EmpDetailsActivity.this));
								}
								
								db.save(model);
							}else {
								model = db.findFirst(Selector.from(SortModel.class)
										.where("personalId", "=", SceoUtil.getEmpCode(EmpDetailsActivity.this))
										.where("emp_id", "=", info.getEmp_id()));
								db.delete(model);
							}
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
			}
		});
		
	}

	/**
	 * 加载UI
	 */
	private void doNetWork() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(EmpDetailsActivity.this));
		params.addBodyParameter("emp_id", empCode);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL+"sceosrv/csp/sceosrv.dll?page=EAA04AD&pcmd=getEmpDetail",
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
						
						data = SceoUtil.parseJson(responseInfo.result, EmpDetailsData.class);
						if (data.getStatus() == 0) {
							info = data.getData();
							
							//已离职员工处理
							if (info.getLeaflag() == 1) {
								collect.setVisibility(View.GONE);
								launch_affairs.setVisibility(View.GONE);
							}
							
							name.setText(info.getEmp_name());
							code.setText(info.getEmp_id());
							if (info.getIsmyfriend() == 0) {
								collect.setChecked(false);
							}else {
								collect.setChecked(true);
							}
							Glide
									.with(EmpDetailsActivity.this)
									.load(info.getPhoto())
									.centerCrop()
									.placeholder(R.drawable.ic_login_photo)
									.crossFade()
									.into(photo);

							browsePhoto(info.getPhoto());
							
							for (int i = 0; i < info.getWorkinfo().size(); i++) {
								if (info.getWorkinfo().get(i).getValue() != null && !"".equals(data.getData().getWorkinfo().get(i).getValue())) {
									workInfos.add(info.getWorkinfo().get(i));
								}
							}
							EmpDetailsAdapter adapter = new EmpDetailsAdapter(EmpDetailsActivity.this, workInfos);
							workList.setAdapter(adapter);
							SceoUtil.setListViewHeightBasedOnChildren(workList);
							
							for (int i = 0; i < info.getTelinfo().size(); i++) {
								if (info.getTelinfo().get(i).getValue() != null && !"".equals(info.getTelinfo().get(i).getValue())) {
									telInfos.add(info.getTelinfo().get(i));
								}
							}
							EmpDetailsTelAdapter telAdapter = new EmpDetailsTelAdapter(EmpDetailsActivity.this, telInfos);
							telList.setAdapter(telAdapter);
							SceoUtil.setListViewHeightBasedOnChildren(telList);
							telList.setOnItemClickListener(new MyOnItemClickListener(telInfos));
							
							if (telInfos.size() > 0) {
								launch_sms.setVisibility(View.VISIBLE);
								launch_sms.setOnClickListener(new MyOnclickListener());
							}else {
								launch_sms.setVisibility(View.INVISIBLE);
							}
							EmpDetailsSmsAdapter smsAdapter = new EmpDetailsSmsAdapter(EmpDetailsActivity.this, telInfos);
							smsListView.setAdapter(smsAdapter);
							smsListView.setOnItemClickListener(new MySmsOnItemClickListener(telInfos));
						}else {
							Toasty.error(EmpDetailsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(EmpDetailsActivity.this);
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
	
	/**
	 * 点击头像看大图
	 */
	private void browsePhoto(final String url){
		if (url != null && !"".equals(url)) {			
			photo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View view) {
					Intent intent = new Intent(EmpDetailsActivity.this, SpaceImageDetailActivity.class);
					intent.putExtra("imageUrl", url);
					int location[] = new int[2];
					photo.getLocationOnScreen(location);
					intent.putExtra("locationX", location[0]);
					intent.putExtra("locationY", location[1]);
					intent.putExtra("width", photo.getWidth());
					intent.putExtra("height", photo.getHeight());
					startActivity(intent);
					overridePendingTransition(0, 0);
				}
			});
		}
	}
	
	/**
	 * 打电话
	 * @param telNum
	 */
	private void telDialog(final String telNum){
		new SweetAlertDialog(EmpDetailsActivity.this, SweetAlertDialog.NORMAL_TYPE)
			.setTitleText("是否拨打电话？")
			.setContentText(telNum)
			.setCancelText("暂不拨打")
			.setConfirmText("立即拨打")
			.showCancelButton(true)
			.setCancelClickListener(new OnSweetClickListener() {
				
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					sweetAlertDialog.dismiss();
				}
			})
			.setConfirmClickListener(new OnSweetClickListener() {
				
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					Uri uri = Uri.parse("tel:"+telNum);
					Intent intent = new Intent(Intent.ACTION_DIAL, uri);
					startActivity(intent);
					sweetAlertDialog.dismiss();
				}
			})
			.show();
	}
	
	/**
	 * 发短信
	 * listView
	 */
	private void smsDialog(final String smsNum){
		new SweetAlertDialog(EmpDetailsActivity.this, SweetAlertDialog.NORMAL_TYPE)
		.setTitleText("是否发送短信？")
		.setContentText(smsNum)
		.setCancelText("暂不发送")
		.setConfirmText("立即发送")
		.showCancelButton(true)
		.setCancelClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				sweetAlertDialog.dismiss();
			}
		})
		.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				Uri uri = Uri.parse("smsto:"+smsNum);
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				startActivity(intent);
				sweetAlertDialog.dismiss();
			}
		})
		.show();
	}
	
	
}
