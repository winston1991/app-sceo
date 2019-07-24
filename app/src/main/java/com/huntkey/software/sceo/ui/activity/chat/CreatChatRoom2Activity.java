package com.huntkey.software.sceo.ui.activity.chat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.ChatSettingLinkman;
import com.huntkey.software.sceo.bean.TreeData;
import com.huntkey.software.sceo.bean.TreeElement;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.CreatChatRoom2TreeViewAdapter;
import com.huntkey.software.sceo.ui.adapter.CreatChatRoom2TreeViewAdapter.CSHolder;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.CreatChatRoom2TreeView;
import com.huntkey.software.sceo.widget.CreatChatRoom2TreeView.LastLevelItemClickListener;
import com.huntkey.software.sceo.widget.CreatChatRoom2TreeView.ParentLevelItemClickListener;
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
 * 创建群组时转到的添加企业通讯录中联系人界面
 * @author chenliang3
 *
 */
public class CreatChatRoom2Activity extends BaseActivity {

	@ViewInject(R.id.creat_chatroom_sec_title)
	BackTitle title;
	@ViewInject(R.id.creat_chatroom_sec_layoutInHsv)
	LinearLayout layout;
	@ViewInject(R.id.creat_chatroom_sec_tree)
	CreatChatRoom2TreeView treeList;
	@ViewInject(R.id.creat_chatroom_sec_error)
	ErrorView errorView;
	
	private List<TreeElement> treeData = new ArrayList<TreeElement>();
	private int total;//选中用户总数(右上角显示)
	private List<ChatSettingLinkman> addLinkmans = new ArrayList<ChatSettingLinkman>();
	private String clFlag;
	private LoadingDialog loadingDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_chat_room2);
		ViewUtils.inject(this);
		
		clFlag = getIntent().getStringExtra("ClFlag");
		
		initTitle();
		initView();
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				initView();
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
					Intent intent = new Intent(CreatChatRoom2Activity.this, ChatActivity.class);
					intent.putExtra("AddLinkmans", (Serializable)addLinkmans);
					intent.putExtra("taskName", "新事务");
					if (getLinkmans(addLinkmans).contains(SceoUtil.getEmpCode(CreatChatRoom2Activity.this))) {						
						intent.putExtra("peopleNum", addLinkmans.size());
					}else {
						intent.putExtra("peopleNum", addLinkmans.size()+1);
					}
					startActivity(intent);
					CreatChatRoom2Activity.this.finish();
				}else {					
					Intent intent = new Intent();
					intent.putExtra("backAddLinkmans2", (Serializable)addLinkmans);
					setResult(Activity.RESULT_OK, intent);
					finish();
				}
			}
		});
	}
	
	private void initView() {
		loadingDialog = new LoadingDialog(CreatChatRoom2Activity.this);
		
		initMainData();//加载"航嘉机构"
		treeList.setParentLevelItemClickCallBack(pItemClickCallBack);	
		treeList.setLastLevelItemClickCallBack(itemClickCallBack);
	}
	
	//子节点(员工)点击事件监听
	LastLevelItemClickListener itemClickCallBack = new LastLevelItemClickListener() {
		
		@Override
		public void onLastLevelItemClick(int position, CreatChatRoom2TreeViewAdapter adapter, View convertView) {
			TreeElement element = (TreeElement) adapter.getItem(position);
			if (convertView.getTag() instanceof CSHolder) {					
				CSHolder viewHolder = (CSHolder) convertView.getTag();
				
				//自动触发checkbox的checked事件
				viewHolder.checkBox.toggle();
				
				if (viewHolder.checkBox.isChecked()) {
					showCheckImage(element);
				}else {
					deleteImage(element);
				}
			}
		}
	};
	
	//父节点（部门）点击事件监听

	ParentLevelItemClickListener pItemClickCallBack = new ParentLevelItemClickListener() {
		
		@Override
		public void onParentItemClick(int position, CreatChatRoom2TreeViewAdapter adapter) {
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
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(CreatChatRoom2Activity.this));
		params.addBodyParameter("cc_code", "");
		params.addBodyParameter("is_first", "1");
		params.addBodyParameter("cc_level", "");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA04AA&pcmd=getDeptTreeNode",
				params,
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
						errorView.setVisibility(View.VISIBLE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						errorView.setVisibility(View.GONE);
						
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
							treeList.initData(CreatChatRoom2Activity.this, treeData);
							
							if(treeData.get(0) != null){								
								TreeElement element = (TreeElement)treeList.getAdapter().getItem(0);							
								if (!element.isEx()) 
								{//判断当前父节点是否是第一次加载子数据（保证数据只加载一次）
									getData(element.getCc_code(), element.getCc_level(), 0);//获取父节点下的数据

									element.setEx(true);
								}
							}
						}else {
							Toasty.error(CreatChatRoom2Activity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(CreatChatRoom2Activity.this);
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
	 * 加载父节点下的子节点数据
	 */
	private void getData(final String code, final String level, final int position){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(CreatChatRoom2Activity.this));
		params.addBodyParameter("cc_code", code);
		params.addBodyParameter("is_first", "0");
		params.addBodyParameter("cc_level", level);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA04AA&pcmd=getDeptTreeNode",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
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
							treeList.updateData(CreatChatRoom2Activity.this, treeData, position, code);
						}else {
							Toasty.error(CreatChatRoom2Activity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(CreatChatRoom2Activity.this);
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
	
	//即时显示选中用户的头像
	private void showCheckImage(TreeElement model){
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
				.with(CreatChatRoom2Activity.this)
				.load(model.getPhoto())
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(images);
        
        layout.addView(view, menuLinerLayoutParames);
        title.setConfirmText("确定(" + total + ")");
        
        ChatSettingLinkman linkman = new ChatSettingLinkman();
        linkman.setEmp_id(model.getCc_code());
        linkman.setEmp_name(model.getTitle());
        linkman.setEmp_photo(model.getPhoto());
        linkman.setRaiseflag(0);
        addLinkmans.add(linkman);
	}
	
	private void deleteImage(TreeElement model){
		View view = (View) layout.findViewWithTag(model);
		layout.removeView(view);
		total--;
		title.setConfirmText("确定(" + total + ")");

		ChatSettingLinkman linkman = new ChatSettingLinkman();
        linkman.setEmp_id(model.getCc_code());
        linkman.setEmp_name(model.getTitle());
        linkman.setEmp_photo(model.getPhoto());
        linkman.setRaiseflag(0);
        addLinkmans.remove(linkman);
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
