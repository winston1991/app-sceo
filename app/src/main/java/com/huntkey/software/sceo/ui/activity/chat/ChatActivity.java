package com.huntkey.software.sceo.ui.activity.chat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.ChatData;
import com.huntkey.software.sceo.bean.ChatDetails;
import com.huntkey.software.sceo.bean.ChatSettingLinkman;
import com.huntkey.software.sceo.bean.SubTaskIdData;
import com.huntkey.software.sceo.bean.eventbus.EventBusAffairsNumAndId;
import com.huntkey.software.sceo.bean.eventbus.EventBusAffairsNumAndIdAndTask;
import com.huntkey.software.sceo.bean.eventbus.EventBusEmp;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.ChatMsgAdapter;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.TimeUtil;
import com.huntkey.software.sceo.widget.ActionSheet;
import com.huntkey.software.sceo.widget.ActionSheet.ActionSheetListener;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
import com.huntkey.software.sceo.widget.crop.ImageUtil;
import com.huntkey.software.sceo.widget.xlistview.XListView;
import com.huntkey.software.sceo.widget.xlistview.XListView.IXListViewListener;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import es.dmoral.toasty.Toasty;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 待办事务聊天界面
 * @author chenliang3
 *
 */
public class ChatActivity extends BaseActivity implements IXListViewListener{

	@ViewInject(R.id.chat_title)
	BackTitle title;
	@ViewInject(R.id.chat_listView)
	XListView listView;
	@ViewInject(R.id.chat_input_et)
	EditText inputEt;
	@ViewInject(R.id.chat_sent_btn)
	ImageView sendBtn;
	@ViewInject(R.id.chat_photo)
	ImageView photoBtn;
	@ViewInject(R.id.chat_errorView)
	ErrorView errorView;
	
	private int taskId;
	private String taskName;
	//聊天内容
	private List<ChatDetails> dataList = new ArrayList<ChatDetails>();
	private ChatMsgAdapter adapter;
	private DbUtils db;
	private int currentPage = 1;
	private static int perPageSize = 10;
	private static String newFalg = "1";//为1请求新的，为0请求历史
	private int totalRow;
	private int minId;
	private int maxId;
	private BroadcastReceiver broadcastReceiver;
	private List<ChatSettingLinkman> addLinkmans;
	private int REQUEST_CODE = 0x21;//进入事务设置请求
	private int RESULT_CODE = 0x19;//退出事务后该界面也退出
	private boolean isTrue = true;//判断是否可发送
	private static ChatActivity mInstence = null;
	private int peopleNum;
	private boolean isFirst = true;
	
	private File mCaptureFile = null;
    private static final int REQUEST_CAPTURE_IMAGE = 0;
	private static final int REQUEST_PICK_IMAGE = 3;
    private static final int REQUEST_CUT = 1;
    private final int minification = 2;//图片压缩倍数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		ViewUtils.inject(this);
		mInstence = this;
		
		if (!EventBus.getDefault().isRegistered(this)){
			EventBusUtil.getInstence().register(this);
		}
		db = DbUtils.create(this, "chat" + SceoUtil.getAcctid(this) + SceoUtil.getEmpCode(this));
		taskId = getIntent().getIntExtra("taskId", 0);
		taskName = getIntent().getStringExtra("taskName");
		peopleNum = getIntent().getIntExtra("peopleNum", 1);
		
		addLinkmans = (List<ChatSettingLinkman>) getIntent().getSerializableExtra("AddLinkmans");
		
		initTitleAndErrorView();
		initView();
		chatRecords();
		
		registerBroadCastReceiver();
		
		setTheme(R.style.ActionSheetStyleiOS7);

		checkPermission(new CheckPermListener() {
			@Override
			public void superPermission() {

			}
		},R.string.perssion_PICK_PHOTO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}
	
	public static ChatActivity getInstence(){
		return mInstence;
	}
	
	private void registerBroadCastReceiver(){
		broadcastReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				final List<ChatDetails> details = (List<ChatDetails>) intent.getSerializableExtra("chatDetails");
				if (details != null && details.size() > 0) {
					dataList.addAll(details);
					new Handler().post(new Runnable() {
						
						@Override
						public void run() {							
							try {
								db.saveAll(details);
							} catch (DbException e) {
								e.printStackTrace();
							}
						}
					});
					if (adapter == null) {
						adapter = new ChatMsgAdapter(ChatActivity.this, ChatActivity.this, dataList, db);
						listView.setAdapter(adapter);								
					}else {
						adapter.notifyDataSetChanged();
					}
					listView.setSelection(listView.getCount() - 1);
				}
			}
		};
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SceoUtil.ACTION_CHAT_MESSAGE_RECEIVER);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	private void initTitleAndErrorView() {
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		title.setBackTitle(taskName+"("+peopleNum+")");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		
		title.setLunchBtnVisible();
		title.setLunchBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (taskId > 0) {					
					//待办事务设置
					Intent intent = new Intent(ChatActivity.this, ChatSettingActivity.class);
					intent.putExtra("taskId", taskId);
					startActivityForResult(intent, REQUEST_CODE);
				}
			}
		});
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {						
						hideErrorView(errorView);
						initView();
					}
				}, 10);
			}
		});
		
		photoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ActionSheet.createBuilder(ChatActivity.this, getSupportFragmentManager())
					.setCancelButtonTitle("取消")
					.setOtherButtonTitles("拍照","打开相册")
					.setCancelableOnTouchOutside(true)
					.setListener(new ActionSheetListener() {

						@Override
						public void onOtherButtonClick(ActionSheet actionSheet, int index) {
							switch (index){
	                            case 0://拍照
									mCaptureFile = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM),
											"" + new Date().getTime() + ".jpg");
									Uri imageUri;
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
										imageUri = FileProvider.getUriForFile(ChatActivity.this, "com.huntkey.software.sceo.fileprovider", mCaptureFile);
									}else {
										imageUri = Uri.fromFile(mCaptureFile);
									}
									Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
									if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
										intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
									}
									intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
									startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
	                                break;
	                            case 1://打开相册
									checkPermission(new CheckPermListener() {
										@Override
										public void superPermission() {
											Intent intent2 = new Intent();
											intent2.setAction(Intent.ACTION_GET_CONTENT);//Pick an item from the data
											intent2.setType("image/*");//从所有图片中进行选择
											startActivityForResult(intent2, REQUEST_PICK_IMAGE);
										}
									}, R.string.perssion_PICK_PHOTO,
											Manifest.permission.WRITE_EXTERNAL_STORAGE,
											Manifest.permission.READ_EXTERNAL_STORAGE);
	                                break;
	                            default:
	                                break;
	                        }
						}

						@Override
						public void onDismiss(ActionSheet actionSheet, boolean isCancel) {

						}
					})
					.show();
			}
		});
	}

	private void initView() {
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(false);
		listView.setXListViewListener(this);
		listView.setScrollEnable(false);//设置刷新的时候不可滑动
		
		inputEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence value, int start, int count, int before) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence value, int start, int count,
					int after) {
				
			}
			
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN) @Override
			public void afterTextChanged(Editable value) {
				if (inputEt.getText().toString().length() > 0) {
//					sendBtn.setBackground(getResources().getDrawable(R.drawable.send_button_background_blue));
					sendBtn.setImageResource(R.drawable.ic_send);
				}else {
//					sendBtn.setBackground(getResources().getDrawable(R.drawable.send_button_background));
					sendBtn.setImageResource(R.drawable.ic_send_gray);
				}
			}
		});
		
		//启动activity时不自动弹出软键盘
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String sendMsg = inputEt.getText().toString();
				if (isTrue) {					
					send(sendMsg);
				}
			}
		});
		
		//如果是新发起的，没有taskid，第一次进入该界面不再进行网络请求
		if (taskId > 0) { 
			newFalg = "1";
			receive(newFalg);
		}
	}
	
	private void receive(String isNew){
		hideErrorView(errorView);
		
		if (!hasNetWork()) {
			showErrorView(errorView, 
					getResources().getDrawable(R.drawable.ic_content_manager_wifi), 
					"网络请求失败", 
					"请打开您的数据连接并重试");
		}else {
			doReceiveNetwork(isNew);
		}
	}
	
	/**
	 * 从服务器获取数据
	 */
	private void doReceiveNetwork(final String isNew) {
		final String temp = isNew;
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				try {
					List<ChatDetails> list = db.findAll(Selector.from(ChatDetails.class)
							.where("taskid","=",taskId).orderBy("subtaskid"));
					if (list != null && list.size() > 0) {				
						minId = list.get(0).getSubtaskid();
						maxId = list.get(list.size() -1).getSubtaskid();
					}
				} catch (DbException e) {
					e.printStackTrace();
				}
				
				RequestParams params = new RequestParams();
				params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChatActivity.this));
				params.addBodyParameter("taskid", taskId+"");
				params.addBodyParameter("newflag", isNew);
				if ("1".equals(isNew)) {
					params.addBodyParameter("maxid", maxId+"");
				}else {
					params.addBodyParameter("minid", minId+"");
				}
				params.addBodyParameter("pageno", currentPage+"");
				params.addBodyParameter("pagesize", perPageSize+"");
				HttpUtils http = new HttpUtils();
				http.configResponseTextCharset("GB2312");
				http.send(HttpRequest.HttpMethod.POST, 
						Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA05AB&pcmd=getTaskInfo",
						params, 
						new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						onLoad();
						showErrorView(errorView, 
								getResources().getDrawable(R.drawable.ic_content_manager_sync), 
								"请求失败", 
								"请求数据失败，请重试");
						SceoUtil.setCanServiceLoading(ChatActivity.this, true);
					}
					
					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						onLoad();
						SceoUtil.setCanServiceLoading(ChatActivity.this, true);
						
						ChatData data = SceoUtil.parseJson(responseInfo.result, ChatData.class);
						if (data.getStatus() == 0) {
							List<ChatDetails> details = data.getData().getTaskinfo();
							if (details != null && details.size() > 0) {								
								for (int i = 0; i < details.size(); i++) {
									details.get(i).setMsgresult(1);//获得的数据消息状态全为成功
								}
							}
							Collections.reverse(details);//将details倒序
							totalRow = data.getData().getPage().getRowcount();
							if ("1".equals(temp)) { //有新消息的情况
								dataList.addAll(details);
								if (totalRow > dataList.size()) {
									listView.setPullRefreshEnable(true);
								}else {
									listView.setPullRefreshEnable(false);
								}
								if (details != null && details.size() > 0) {
									try {
										db.saveAll(details);
									} catch (DbException e) {
										e.printStackTrace();
									}
								}
								if (adapter == null) {
									adapter = new ChatMsgAdapter(ChatActivity.this, ChatActivity.this, dataList, db);
									listView.setAdapter(adapter);								
								}else {
									adapter.updateList(dataList);
									adapter.notifyDataSetChanged();
								}
								listView.setSelection(listView.getCount() - 1);
							}else {  //获取历史消息的情况
								dataList.addAll(0, details);
								if (totalRow > dataList.size()) {
									listView.setPullRefreshEnable(true);
								}else {
									listView.setPullRefreshEnable(false);
								}
								if (details != null && details.size() > 0) {
									new Handler().post(new Runnable() {
										
										@Override
										public void run() {											
											try {
//												db.deleteAll(ChatDetails.class);
												List<ChatDetails> list = db.findAll(Selector.from(ChatDetails.class)
														.where("taskid","=",taskId));
												db.deleteAll(list);
												db.saveAll(dataList);
											} catch (DbException e) {
												e.printStackTrace();
											}
										}
									});
								}
								if (adapter == null) {
									adapter = new ChatMsgAdapter(ChatActivity.this, ChatActivity.this, dataList, db);
									listView.setAdapter(adapter);								
								}else {
									adapter.updateList(dataList);
									adapter.notifyDataSetChanged();
								}
								listView.setSelection(0);
							}
						}else if (data.getStatus() == 88) {
							Toasty.error(ChatActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(ChatActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(ChatActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						SceoUtil.setCanServiceLoading(ChatActivity.this, false);//当chatActivity请求数据的时候不让service请求数据
					}
				});
			}
		});
	}

	/**
	 * 点击发送按钮触发事件
	 */
	public void send(final String sendMsg){
		if (sendMsg != null && !"".equals(sendMsg)) {	
			inputEt.setText("");//重置输入框
			isTrue = false;
			final ChatDetails details = new ChatDetails();
			details.setCreattime(TimeUtil.getCurrentData());
			details.setEmp_id(SceoUtil.getEmpCode(ChatActivity.this));
			details.setEmp_name(SceoUtil.getEmpName(ChatActivity.this));
			details.setContent(sendMsg);
			details.setPhoto(SceoUtil.getEmpPhoto(ChatActivity.this));
			details.setMeflag(1);
			details.setFlag(0);
			details.setWeburl("");
			details.setMsgresult(0);
			details.setTaskid(taskId);
			
			dataList.add(details);
			if (adapter == null) {
				adapter = new ChatMsgAdapter(ChatActivity.this, ChatActivity.this, dataList, db);
				listView.setAdapter(adapter);
			}else {
				adapter.updateList(dataList);
				adapter.notifyDataSetChanged();
			}
			listView.setSelection(listView.getCount() - 1);
			
			RequestParams params = new RequestParams();
			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChatActivity.this));
			if (taskId > 0) {				
				params.addBodyParameter("taskid", taskId+"");
			}
			if (taskId <= 0 && addLinkmans.size() > 0) {
				params.addBodyParameter("emplist", getLinkmans(addLinkmans));
			}
			params.addBodyParameter("taskcontent", sendMsg);
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.POST, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA05AB&pcmd=saveTask&charset=utf8",
					params, 
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0,
								String arg1) {
							Toasty.error(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT, true).show();
//							dataList.remove(details);
							details.setIsReSendShow(1);
							adapter.updateList(dataList);
							adapter.notifyDataSetChanged();
							
							try {
								if (taskId > 0 && isFirst) {									
									db.save(details);//保存到数据库
									isFirst = false;
								}
							} catch (DbException e) {
								e.printStackTrace();
							}
//							inputEt.setText(sendMsg);//失败之后将原消息拿回到输入框
							isTrue = true;
						}
						
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {	
							if (taskId <= 0) {								
								SceoUtil.setIsCreatNewAffairs(ChatActivity.this, true);//affairsFragment更新的flag
							}
							
							SubTaskIdData data = SceoUtil.parseJson(responseInfo.result, SubTaskIdData.class);
							if (data.getStatus() == 0) {
								isTrue = true;
								details.setSubtaskid(data.getData().getSubtaskid());
//								details.setTaskid(data.getData().getTaskid());
								details.setMsgresult(1);
								
								try {
									if (taskId > 0) {										
										db.save(details);//保存到数据库
									}
								} catch (DbException e) {
									e.printStackTrace();
								}
								
								if (taskId <= 0) {
									taskId = data.getData().getTaskid();//针对新建事务时，第一次获得的taskid
									dataList.clear();
									receive("0");
									
									//如果没有title，第一次发送的内容设置为title
									if ((title.getBackTitle()).contains("新事务")) {
										if (getLinkmans(addLinkmans).contains("'"+SceoUtil.getEmpCode(ChatActivity.this)+"'")) {											
											title.setBackTitle(sendMsg + "(" + addLinkmans.size() + ")");
										}else {
											title.setBackTitle(sendMsg + "(" + (addLinkmans.size()+1) + ")");
										}
									}
									return;
								}
								
								adapter.updateList(dataList);
								adapter.notifyDataSetChanged();
								listView.setSelection(listView.getCount() - 1);
								
								final int tmpTaskId = data.getData().getTaskid();
								//是否可进入设置处理
								title.setLunchBtnClickLis(new OnClickListener() {
									
									@Override
									public void onClick(View arg0) {
										if (tmpTaskId > 0) {					
											//待办事务设置
											Intent intent = new Intent(ChatActivity.this, ChatSettingActivity.class);
											intent.putExtra("taskId", tmpTaskId);
											startActivityForResult(intent, REQUEST_CODE);
										}else {
											Toasty.warning(ChatActivity.this, "请先发起事务然后才可以进入设置界面", Toast.LENGTH_SHORT, true).show();
										}
									}
								});
							}else {
								Toasty.error(ChatActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							}
						}
					});
		}
	}
	
	/**
	 * 从数据库中读取数据并在listview中显示，作为本地聊天记录
	 */
	private void chatRecords(){
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {				
				try {
					List<ChatDetails> details = db.findAll(Selector.from(ChatDetails.class).where("taskid","=",taskId));
					if (details != null && details.size() > 0) {
						dataList.addAll(details);
						if (adapter == null) {
							adapter = new ChatMsgAdapter(ChatActivity.this, ChatActivity.this, dataList, db);
							listView.setAdapter(adapter);								
						}else {
							adapter.updateList(dataList);
							adapter.notifyDataSetChanged();
						}
						
						int tmpSelection = SceoUtil.getTmpChatSelection(ChatActivity.this);
						if (tmpSelection > 0) {					
							listView.setSelection(tmpSelection);
						}else {					
							listView.setSelection(listView.getCount() - 1);
						}
					}
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		});
		
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		return imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
	}

	private void onLoad(){
		listView.stopLoadMore();
		listView.stopRefresh();
		listView.setRefreshTime(TimeUtil.timeFormat(System.currentTimeMillis()));
	}
	
	@Override
	public void onRefresh() {
		newFalg = "0";
		receive(newFalg);
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				receive(newFalg);
//				onLoad();
//			}
//		}, 1500);
	}

	@Override
	public void onLoadMore() {
		
	}
	
	/**
	 * 事务成员名单处理(上传给服务器)
	 */
	private String getLinkmans(List<ChatSettingLinkman> linkmans){
		String tmpString = null;
		for (int i = 0; i < linkmans.size(); i++) {
			String s = "'" + linkmans.get(i).getEmp_id() + "'" + ",";
			tmpString += s;
		}
		tmpString = tmpString.replace("null", "");
		tmpString = tmpString.substring(0, tmpString.length() - 1);
		return tmpString;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_CODE) {
				String nameString = data.getStringExtra("taskName");
				title.setBackTitle(nameString);
			}
			else if (requestCode == REQUEST_CAPTURE_IMAGE) {
				try {
	                Uri uri = data.getData();
	                if (uri != null) {
	                    Cursor cr = getContentResolver().query(uri,
	                            new String[]{MediaStore.Images.Media.DATA}, null, null, null);
	                    if (cr.moveToFirst()) {
	                        File file = new File(cr.getString(cr.getColumnIndex(MediaStore.Images.Media.DATA)));
	                        Bitmap bitmap = ImageUtil.loadBitmap(file, minification);
	                        setBitmapJumpActivity(bitmap);
	                    }
	                    cr.close();
	                } else {
	                    Bundle bundle = data.getExtras();
	                    Bitmap bitmap = (Bitmap) bundle.get("data");
	                    setBitmapJumpActivity(ImageUtil.changeBitmap(bitmap,
	                            bitmap.getWidth() / minification, bitmap.getHeight() / minification));
	                }

	            } catch (Exception e) {
	                if (mCaptureFile != null && mCaptureFile.exists()) {
	                    Bitmap bitmap = ImageUtil.loadBitmap(mCaptureFile, minification);
	                    setBitmapJumpActivity(bitmap);
	                }
	            }
			}else if (requestCode == REQUEST_PICK_IMAGE){
				Uri imageUri;
				String imagePath;
				Bitmap bitmap;
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
					imagePath = null;
					imageUri = data.getData();
					if (DocumentsContract.isDocumentUri(this, imageUri)) {
						//如果是document类型的uri,则通过document id处理
						String docId = DocumentsContract.getDocumentId(imageUri);
						if ("com.android.providers.media.documents".equals(imageUri.getAuthority())) {
							String id = docId.split(":")[1];//解析出数字格式的id
							String selection = MediaStore.Images.Media._ID + "=" + id;
							imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
						} else if ("com.android.downloads.documents".equals(imageUri.getAuthority())) {
							Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
							imagePath = getImagePath(contentUri, null);
						}
					} else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
						//如果是content类型的Uri，则使用普通方式处理
						imagePath = getImagePath(imageUri, null);
					} else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
						//如果是file类型的Uri,直接获取图片路径即可
						imagePath = imageUri.getPath();
					}
				}else {
					imageUri = data.getData();
					imagePath = getImagePath(imageUri, null);
				}
				bitmap = BitmapFactory.decodeFile(imagePath);
				setBitmapJumpActivity(bitmap);
			}else if (requestCode == REQUEST_CUT) {
				Bitmap cropped = ((SceoApplication)getApplication()).cropped;
				lubanCompress(bitmap2File(cropped));
			}
		}else if (resultCode == RESULT_CODE) {
			ChatActivity.this.finish();
		}
	}

	private void lubanCompress(File file){
		Luban.with(ChatActivity.this)
				.load(file)
				.setCompressListener(new OnCompressListener() {
					@Override
					public void onStart() {

					}

					@Override
					public void onSuccess(File file) {
						sendPhoto(file);
					}

					@Override
					public void onError(Throwable e) {
						Toasty.error(ChatActivity.this, "压缩图片出错", Toast.LENGTH_SHORT).show();
					}
				})
				.launch();
	}

	private String getImagePath(Uri uri, String selection) {
		String path = null;
		//通过Uri和selection老获取真实的图片路径
		Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
			}
			cursor.close();
		}
		return path;
	}
	
	/**
	 * bitmap 转 file
	 * @param bitmap
	 * @return
	 */
	private File bitmap2File(final Bitmap bitmap){
		String path = Environment.getExternalStorageDirectory().getPath()+"/SCEO/.image/";
		File tmpFile = new File(path);
		File file = new File(path + new Date().getTime() + ".jpg");
//		checkPermission(new CheckPermListener() {
//			@Override
//			public void superPermission() {
				if (!tmpFile.exists()){
					tmpFile.mkdirs();
				}
				if (!file.exists()){
					try {
						file.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				try {
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
					bos.flush();
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
//			}
//		}, R.string.perssion_PICK_PHOTO,
//				Manifest.permission.WRITE_EXTERNAL_STORAGE);

		return file;
	}
	
	private void setBitmapJumpActivity(Bitmap bitmap) {
        ((SceoApplication) getApplication()).cropped = bitmap;
        Intent intent = new Intent(ChatActivity.this, CorpActivity.class);
        startActivityForResult(intent, REQUEST_CUT);
    }
	
	private void sendPhoto(File photoFile){
		isTrue = false;
		final ChatDetails details = new ChatDetails();
		details.setCreattime(TimeUtil.getCurrentData());
		details.setEmp_id(SceoUtil.getEmpCode(ChatActivity.this));
		details.setEmp_name(SceoUtil.getEmpName(ChatActivity.this));
		details.setContent("");
		details.setPhoto(SceoUtil.getEmpPhoto(ChatActivity.this));
		details.setMeflag(1);
		details.setFlag(1);
		details.setWeburl("");
		details.setMsgresult(0);
		details.setTaskid(taskId);
		
		dataList.add(details);
		if (adapter == null) {
			adapter = new ChatMsgAdapter(ChatActivity.this, ChatActivity.this, dataList, db);
			listView.setAdapter(adapter);
		}else {
			adapter.updateList(dataList);
			adapter.notifyDataSetChanged();
		}
		listView.setSelection(listView.getCount() - 1);
		
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChatActivity.this));
		if (taskId > 0) {				
			params.addBodyParameter("taskid", taskId+"");
		}
		if (taskId <= 0 && addLinkmans.size() > 0) {
			params.addBodyParameter("emplist", getLinkmans(addLinkmans));
		}
		params.addBodyParameter("isimage", "1");
		params.addBodyParameter("file", photoFile);
		params.addBodyParameter("imgname", photoFile.getName());
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA05AB&pcmd=saveTask&charset=utf8",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						Toasty.error(ChatActivity.this, "发送失败", Toast.LENGTH_SHORT, true).show();
						dataList.remove(details);
						adapter.updateList(dataList);
						adapter.notifyDataSetChanged();
						isTrue = true;
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						if (taskId <= 0) {								
							SceoUtil.setIsCreatNewAffairs(ChatActivity.this, true);//affairsFragment更新的flag
						}
						
						SubTaskIdData data = SceoUtil.parseJson(responseInfo.result, SubTaskIdData.class);
						if (data.getStatus() == 0) {
							isTrue = true;
							details.setSubtaskid(data.getData().getSubtaskid());
							details.setMsgresult(1);
							details.setContent(data.getData().getContent());
							details.setWeburl(data.getData().getWeburl());
							
							try {
								if (taskId > 0) {										
									db.save(details);//保存到数据库
								}
							} catch (DbException e) {
								e.printStackTrace();
							}
							
							if (taskId <= 0) {
								taskId = data.getData().getTaskid();//针对新建事务时，第一次获得的taskid
								dataList.clear();
								receive("0");
								
								//如果没有title，第一次发送的内容设置为title
								if ((title.getBackTitle()).contains("新事务")) {
									if (getLinkmans(addLinkmans).contains("'"+SceoUtil.getEmpCode(ChatActivity.this)+"'")) {											
										title.setBackTitle("未知标题" + "(" + addLinkmans.size() + ")");
									}else {
										title.setBackTitle("未知标题" + "(" + (addLinkmans.size()+1) + ")");
									}
								}
								return;
							}
							
							adapter.updateList(dataList);
							adapter.notifyDataSetChanged();
							listView.setSelection(listView.getCount() - 1);
							
							final int tmpTaskId = data.getData().getTaskid();
							//是否可进入设置处理
							title.setLunchBtnClickLis(new OnClickListener() {
								
								@Override
								public void onClick(View arg0) {
									if (tmpTaskId > 0) {					
										//待办事务设置
										Intent intent = new Intent(ChatActivity.this, ChatSettingActivity.class);
										intent.putExtra("taskId", tmpTaskId);
										startActivityForResult(intent, REQUEST_CODE);
									}else {
										Toasty.warning(ChatActivity.this, "请先发起事务然后才可以进入设置界面", Toast.LENGTH_SHORT, true).show();
									}
								}
							});
						}else {
							Toasty.error(ChatActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
				});
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (broadcastReceiver != null) {
			unregisterReceiver(broadcastReceiver);
		}
		
		//当该界面销毁的时候，使用EventBus将事务id、事务参与人数、事务最后一条消息传给AffairsFragment
		EventBusAffairsNumAndIdAndTask numAndIdAndTask = new EventBusAffairsNumAndIdAndTask();
		numAndIdAndTask.setaEmpCount(peopleNum);
		numAndIdAndTask.setTaskid(taskId);
		if (dataList.size() > 0) {			
			numAndIdAndTask.setLasttask(dataList.get(dataList.size() - 1).getContent());
		}
		EventBusUtil.getInstence().post(numAndIdAndTask);

		if (EventBus.getDefault().isRegistered(this)){
			EventBusUtil.getInstence().unregister(this);
		}
	}
	
	/**
	 * 当在设置界面添加或删除用户的时候，使用EventBus更改对话界面的用户参与数
	 * @param affairsEmpCount
	 */
	@Subscribe
	public void onEventMainThread(EventBusAffairsNumAndId affairsEmpCount){
		peopleNum = affairsEmpCount.getaEmpCount();
		title.setBackTitle(taskName+"("+peopleNum+")");
		receive("1");
	}

	@Subscribe
	public void onEventMainThread(EventBusEmp eventBusEmp){
		String empName = "@" + eventBusEmp.getEmp_name() + " ";
		String tmpContent = inputEt.getText().toString();
		tmpContent += empName;
		inputEt.setText(tmpContent);
		inputEt.setSelection(tmpContent.length());//光标放到最后
	}
	
}
