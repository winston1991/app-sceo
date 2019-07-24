package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.ChatDetails;
import com.huntkey.software.sceo.bean.eventbus.EventBusEmp;
import com.huntkey.software.sceo.ui.activity.WebViewActivity;
import com.huntkey.software.sceo.ui.activity.chat.ChatActivity;
import com.huntkey.software.sceo.ui.activity.empdetails.EmpDetailsActivity;
import com.huntkey.software.sceo.utils.EventBusUtil;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.TimeUtil;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils.TruncateAt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChatMsgAdapter extends BaseAdapter {

	private Context context;
	private Activity activity;
	private List<ChatDetails> data;
	private LayoutInflater mInflater = null;
	private DbUtils db;
	
	public ChatMsgAdapter(Context context, Activity activity, List<ChatDetails> details, DbUtils db){
		this.context = context;
		this.activity = activity;
		this.data = details;
		this.db = db;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		SceoUtil.setTmpChatSelection(context, SceoUtil.INT_NEGATIVE);
	}
	
	//listview的视图由IMsgViewType决定
	public static interface IMsgViewType{
		//对方发来的消息
		int IMVT_COM_MSG = 0;
		//自己发出的信息
		int IMVT_TO_MSG = 1;
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	//获取项的类型
	public int getItemViewType(int position){
		ChatDetails details = data.get(position);
		if (details.getMeflag() == 0) {//0表示接收的,1表示我发的
			return IMsgViewType.IMVT_COM_MSG;
		}else {
			return IMsgViewType.IMVT_TO_MSG;
		}
	} 
	
	//获取项的类型数
	public int getViewTypeCount(){
		return 2;
	}
	
	public void updateList(List<ChatDetails> details){
		this.data = details;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ChatDetails details = data.get(position);
		int isComMsg = details.getMeflag();
		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg == 0) {
				convertView = mInflater.inflate(R.layout.chat_msg_receive, null);
			}else {
				convertView = mInflater.inflate(R.layout.chat_msg_send, null);
			}
			
			viewHolder = new ViewHolder();
			viewHolder.timeTv = (TextView) convertView.findViewById(R.id.chat_msg_time);
			viewHolder.photoIv = (ImageView) convertView.findViewById(R.id.chat_msg_img);
			viewHolder.nameTv = (TextView) convertView.findViewById(R.id.chat_msg_name);
			viewHolder.contentTv = (TextView) convertView.findViewById(R.id.chat_msg_content);
			viewHolder.systemTv = (TextView) convertView.findViewById(R.id.chat_msg_system);
			viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.chat_msg_layout);
			viewHolder.link = (ImageView) convertView.findViewById(R.id.chat_msg_content_link);
			viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.chat_msg_progressbar);
			viewHolder.reSend = (ImageView) convertView.findViewById(R.id.chat_msg_resend);
			viewHolder.gotoweb = (RelativeLayout) convertView.findViewById(R.id.chat_msg_gotoweb);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		//判断时间间隔，1分钟之内不再重复显示时间，大于1分钟显示
		if (position == 0) {
			viewHolder.timeTv.setText(details.getCreattime());
			viewHolder.timeTv.setVisibility(View.VISIBLE);
		}else {
			if (TimeUtil.isClose(data.get(position - 1).getCreattime(), details.getCreattime())) {
				viewHolder.timeTv.setVisibility(View.GONE);
			}else {
				viewHolder.timeTv.setText(details.getCreattime());
				viewHolder.timeTv.setVisibility(View.VISIBLE);
			}
		}
		
		viewHolder.nameTv.setText(details.getEmp_name());
		if (SceoUtil.getIsNameShow(context)) {
			viewHolder.nameTv.setVisibility(View.VISIBLE);
		}else {			
			viewHolder.nameTv.setVisibility(View.GONE);
		}
		
		if ("系统".equals(details.getEmp_name())) {
			viewHolder.layout.setVisibility(View.GONE);
			viewHolder.systemTv.setVisibility(View.VISIBLE);
		}else {
			viewHolder.layout.setVisibility(View.VISIBLE);
			viewHolder.systemTv.setVisibility(View.GONE);
		}
		
		viewHolder.systemTv.setText(details.getContent());
		
		if (details.getWeburl() != null && !"".equals(details.getWeburl())) {
			viewHolder.contentTv.setMaxLines(5);
			viewHolder.contentTv.setEllipsize(TruncateAt.END);
			viewHolder.contentTv.setText(details.getContent());
			viewHolder.link.setVisibility(View.VISIBLE);
		}else {
			viewHolder.contentTv.setText(details.getContent());
			viewHolder.link.setVisibility(View.GONE);
		}
		
		Glide
				.with(context)
				.load(details.getPhoto())
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(viewHolder.photoIv);
		
		//监听listview隐藏软键盘
		convertView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(activity.findViewById(R.id.chat_input_et).getWindowToken(), 0);
			}
		});
		
		//监听content部分点击事件
		viewHolder.gotoweb.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (details.getWeburl() != null && !"".equals(details.getWeburl()) && details.getFlag() == 1) {
					//跳转到webview
					Intent intent = new Intent(context, WebViewActivity.class);
					intent.putExtra("webUrl", details.getWeburl());
					context.startActivity(intent);
					
					SceoUtil.setTmpChatSelection(context, position);
				}
			}
		});
		
		//监听头像点击事件
		viewHolder.photoIv.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, EmpDetailsActivity.class);
				intent.putExtra("empCode", details.getEmp_id());
				context.startActivity(intent);
			}
		});
		
		//长按头像出现@name
		viewHolder.photoIv.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				EventBusEmp eventBusEmp = new EventBusEmp();
				eventBusEmp.setEmp_id(details.getEmp_id());
				eventBusEmp.setEmp_name(details.getEmp_name());
				EventBusUtil.getInstence().post(eventBusEmp);
				
				return true;
			}
		});
		
		if (isComMsg == 0) {
			viewHolder.progressBar.setVisibility(View.INVISIBLE);
			viewHolder.reSend.setVisibility(View.INVISIBLE);
		}else {
			if (details.getMsgresult() == 1) {
				viewHolder.progressBar.setVisibility(View.INVISIBLE);
				viewHolder.reSend.setVisibility(View.INVISIBLE);
			}else {
				viewHolder.progressBar.setVisibility(View.VISIBLE);
				viewHolder.reSend.setVisibility(View.INVISIBLE);
			}
			
			final String sendMsg = viewHolder.contentTv.getText().toString();
			if (details.getIsReSendShow() == 0) {
				viewHolder.reSend.setVisibility(View.INVISIBLE);
			}else {
				viewHolder.reSend.setVisibility(View.VISIBLE);
				viewHolder.progressBar.setVisibility(View.INVISIBLE);
				
				viewHolder.reSend.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						ChatActivity.getInstence().send(sendMsg);
						data.remove(details);//从当前list中删除
						try {
							db.delete(details);//从数据库删除
						} catch (DbException e) {
							e.printStackTrace();
						}
						notifyDataSetChanged();
					}
				});
			}
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView timeTv;
		ImageView photoIv;
		TextView nameTv;
		TextView contentTv;
		TextView systemTv;
		RelativeLayout layout;
		ImageView link;
		ProgressBar progressBar;
		ImageView reSend;
		RelativeLayout gotoweb;
	}

}
