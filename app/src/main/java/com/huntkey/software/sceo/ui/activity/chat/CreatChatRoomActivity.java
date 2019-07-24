package com.huntkey.software.sceo.ui.activity.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.ChatSettingLinkman;
import com.huntkey.software.sceo.bean.ContantData;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.ChooseSortAdapter;
import com.huntkey.software.sceo.ui.adapter.ChooseSortAdapter.ViewHolder;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
import com.huntkey.software.sceo.widget.sortlist.CharacterParser;
import com.huntkey.software.sceo.widget.sortlist.PinyinComparator;
import com.huntkey.software.sceo.widget.sortlist.SideBar;
import com.huntkey.software.sceo.widget.sortlist.SortModel;
import com.huntkey.software.sceo.widget.sortlist.SideBar.OnTouchingLetterChangedListener;
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
/**
 * 创建群组时转到的添加常用联系人界面
 * @author chenliang3
 *
 */
public class CreatChatRoomActivity extends BaseActivity {

	@ViewInject(R.id.creat_chatroom_title)
	BackTitle title;
	@ViewInject(R.id.creat_chatroom_layoutInHsv)
	LinearLayout layout;//可滑动的显示选中用户的view
	@ViewInject(R.id.creat_chatroom_search)
	ImageView iv_search;
	@ViewInject(R.id.creat_chatroom_et)
	ClearEditText et_search;
	@ViewInject(R.id.creat_chatroom_listview)
	ListView listView;
	@ViewInject(R.id.creat_chatroom_dialog)
	TextView lv_dialog;
	@ViewInject(R.id.creat_chatroom_sidebar)
	SideBar sideBar;
	@ViewInject(R.id.creat_chat_room_error)
	ErrorView errorView;
	
	private boolean isCreatingNewGroup;//是否为一个新建的群组
	private List<ChatSettingLinkman> linkmans;//group中一开始就有的成员
	private int total;//选中用户总数(右上角显示)
	private List<SortModel> SourceDateList;//常用联系人
	private List<ChatSettingLinkman> addLinkmans = new ArrayList<ChatSettingLinkman>();
	private List<SortModel> filterDateList = new ArrayList<SortModel>();//搜索结果
	
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;
	private ChooseSortAdapter adapter;
	private int REQUEST_CODE = 0x12;
	private String clFlag;
	private DbUtils db;
	private LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_chat_room);
		ViewUtils.inject(this);
		
		linkmans = (List<ChatSettingLinkman>) getIntent().getSerializableExtra("linkmans");
		clFlag = getIntent().getStringExtra("ClFlag");
		
		db = DbUtils.create(this);
		loadingDialog = new LoadingDialog(CreatChatRoomActivity.this);
		
		initTitle();
		initView();
//		doNetWork();
		loadDataFromDb();
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				loadDataFromDb();
			}
		});
	}

	private void initView() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View headerView = inflater.inflate(R.layout.creat_chatroom_listview_header, null);
		TextView headerTv = (TextView) headerView.findViewById(R.id.creat_chatroom_header_tv);
		headerTv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到企业通讯录中选择
				if (clFlag != null && !"".equals(clFlag)) {
					Intent intent = new Intent(CreatChatRoomActivity.this, CreatChatRoom2Activity.class);
					intent.putExtra("ClFlag", clFlag);
					startActivity(intent);
					CreatChatRoomActivity.this.finish();
				}else {
					Intent intent = new Intent(CreatChatRoomActivity.this, CreatChatRoom2Activity.class);
					startActivityForResult(intent, REQUEST_CODE);
				}	
			}
		});
		listView.addHeaderView(headerView);
		
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar.setTextView(lv_dialog);
		//设置右侧sidebar触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			
			@Override
			public void onTouchingLetterChanged(String s) {
				//该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listView.setSelection(position);
				}
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (view.getTag() instanceof ViewHolder) {					
					ViewHolder viewHolder = (ViewHolder) view.getTag();
					
					//自动触发checkbox的checked事件
					viewHolder.checkBox.toggle();
					
					filterData("");
					SortModel model = filterDateList.get(position-1);
					ChatSettingLinkman linkman = new ChatSettingLinkman();
			        linkman.setEmp_id(model.getEmp_id());
			        linkman.setEmp_name(model.getEmp_name());
			        linkman.setEmp_photo(model.getEmp_photo());
			        linkman.setRaiseflag(0);
			        
					if (viewHolder.checkBox.isChecked()) {
						showCheckImage(model, linkman);
					}else {
						deleteImage(model, linkman);
					}
				}
			}
		});
		
		et_search.addTextChangedListener(new TextWatcher() {
			
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
	}

	private void initTitle() {
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title.setBackTitle("事务成员");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setConfirmBtnVisible();
		title.setConfirmClicklis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//确定
				if (clFlag != null && !"".equals(clFlag)) {
					Intent intent = new Intent(CreatChatRoomActivity.this, ChatActivity.class);
					intent.putExtra("AddLinkmans", (Serializable)addLinkmans);
					intent.putExtra("taskName", "新事务");
					if (getLinkmans(addLinkmans).contains(SceoUtil.getEmpCode(CreatChatRoomActivity.this))) {						
						intent.putExtra("peopleNum", addLinkmans.size());
					}else {
						intent.putExtra("peopleNum", addLinkmans.size()+1);
					}
					startActivity(intent);
					CreatChatRoomActivity.this.finish();
				}else {					
					Intent intent = new Intent();
					intent.putExtra("backAddLinkmans", (Serializable)addLinkmans);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});
	}
	
	private void loadDataFromDb(){
		try {
			List<SortModel> models = db.findAll(Selector.from(SortModel.class).where("personalId", "=", SceoUtil.getEmpCode(CreatChatRoomActivity.this)));
			if (models != null && models.size() > 0) {
				SourceDateList = filledData(models);
				Collections.sort(SourceDateList, pinyinComparator);
				adapter = new ChooseSortAdapter(CreatChatRoomActivity.this, SourceDateList, linkmans);
				listView.setAdapter(adapter);
			}else {
				doNetWork();
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取联系人信息
	 */
	private void doNetWork() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(CreatChatRoomActivity.this));
		params.addBodyParameter("emp_id", SceoUtil.getEmpCode(CreatChatRoomActivity.this));
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA06AA&pcmd=getMyFriends",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						errorView.setVisibility(View.VISIBLE);
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						errorView.setVisibility(View.GONE);
						loadingDialog.dismiss();
						
						ContantData data = SceoUtil.parseJson(responseInfo.result, ContantData.class);
						if (data.getStatus() == 0) {
							List<SortModel> models = data.getData().getMyfriends();
							
							SortModel model = new SortModel();
							model.setEmp_id(SceoUtil.getEmpCode(CreatChatRoomActivity.this));
							model.setEmp_name(SceoUtil.getEmpName(CreatChatRoomActivity.this));
							model.setEmp_photo(SceoUtil.getEmpPhoto(CreatChatRoomActivity.this));
							model.setSmsflag(0);
							models.add(model);
							
							SourceDateList = filledData(models);
							
							//根据a-z进行源数据排序
							Collections.sort(SourceDateList, pinyinComparator);
							adapter = new ChooseSortAdapter(CreatChatRoomActivity.this, SourceDateList, linkmans);
							listView.setAdapter(adapter);
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
			String sortString = pinyin.substring(0, 1).toUpperCase(Locale.CHINA);
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase(Locale.CHINA));
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
	
	//即时显示选中用户的头像
	private void showCheckImage(SortModel model, ChatSettingLinkman linkman){
		total ++;
		
		LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                108, 108, 1);
        View view = LayoutInflater.from(this).inflate(
                R.layout.creat_chatroom_head_item, null);
        ImageView images = (ImageView) view.findViewById(R.id.creat_chatroom_head_avatar);
        menuLinerLayoutParames.setMargins(5, 5, 5, 5);
        
        //设置id，方便后面删除
        view.setTag(model);
        
		Glide
				.with(CreatChatRoomActivity.this)
				.load(model.getEmp_photo())
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(images);
        
        layout.addView(view, menuLinerLayoutParames);
        title.setConfirmText("确定(" + total + ")");
        if (total > 0) {
            if (iv_search.getVisibility() == View.VISIBLE) {
                iv_search.setVisibility(View.GONE);
            }
        }
        
        addLinkmans.add(linkman);
	}
	
	private void deleteImage(SortModel model, ChatSettingLinkman linkman){
		View view = (View) layout.findViewWithTag(model);
		layout.removeView(view);
		total--;
		title.setConfirmText("确定(" + total + ")");
		if (total < 1) {
            if (iv_search.getVisibility() == View.GONE) {
                iv_search.setVisibility(View.VISIBLE);
            }
        }
		
		if (addLinkmans.size() > 0) {			
			for (int i = 0; i < addLinkmans.size(); i++) {
				if (addLinkmans.get(i).getEmp_id().equals(linkman.getEmp_id())) {					
					addLinkmans.remove(addLinkmans.get(i));
				}
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				List<ChatSettingLinkman> backLinkmans = (List<ChatSettingLinkman>) data.getSerializableExtra("backAddLinkmans2");
				Intent intent = new Intent();
				intent.putExtra("backAddLinkmans3", (Serializable)backLinkmans);
				setResult(0x13, intent);
				CreatChatRoomActivity.this.finish();
			}
		}
	}
	
	/**
	 * list转string
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
	
}
