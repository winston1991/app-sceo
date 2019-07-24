package com.huntkey.software.sceo.ui.adapter;

import java.io.Serializable;
import java.util.List;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.ChatSettingLinkman;
import com.huntkey.software.sceo.ui.activity.chat.CreatChatRoomActivity;
import com.huntkey.software.sceo.ui.activity.empdetails.EmpDetailsActivity;
import com.huntkey.software.sceo.utils.SceoUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class ChatGridViewAdapter extends BaseAdapter {

	private List<ChatSettingLinkman> data;
	private int raiseflag;
	private Context context;
	private Activity activity;
	private LayoutInflater mInflater = null;
	public boolean isInDelMode;//是否处于删除模式下
	private int requestCode;
	private int setFlag;
	
	public ChatGridViewAdapter(Context context, Activity activity, 
			List<ChatSettingLinkman> linkmans, int isRaise, int requestCode, int setFlag){
		this.context = context;
		this.activity = activity;
		this.data = linkmans;
		this.raiseflag = isRaise;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		isInDelMode = false;
		this.requestCode = requestCode;
		this.setFlag = setFlag;
	}
	
	@Override
	public int getCount() {
		if (raiseflag == 1) {
			return data.size() + 2;
		}else {			
			return data.size() + 1;
		}
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public List<ChatSettingLinkman> getData(){
		return this.data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final int tmpPosition = position; 
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.chat_setting_gridview_item, null);
			viewHolder.photo = (ImageView) convertView.findViewById(R.id.chat_setting_item_iv);
			viewHolder.sub = (ImageView) convertView.findViewById(R.id.chat_setting_item_sub);
			viewHolder.name = (TextView) convertView.findViewById(R.id.chat_setting_item_tv);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		//最后一个item，减人按钮
		if (position == getCount() - 1 && raiseflag == 1) {
			if (setFlag == 0) {
				viewHolder.name.setText("");
				viewHolder.photo.setEnabled(false);
				viewHolder.photo.setImageResource(R.drawable.ic_chat_setting_item_del_enable);
				viewHolder.sub.setVisibility(View.INVISIBLE);
			}else {				
				viewHolder.name.setText("");
				viewHolder.sub.setVisibility(View.INVISIBLE);
				viewHolder.photo.setImageResource(R.drawable.ic_chat_setting_item_del);
				
				if (isInDelMode) {
					//处于删除模式下，隐藏删除按钮
					convertView.setVisibility(View.GONE);
				}else {
					convertView.setVisibility(View.VISIBLE);
				}
				
				viewHolder.photo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						isInDelMode = true;
						notifyDataSetChanged();
					}
				});
			}
		}else if ((raiseflag == 1 && position == getCount() - 2) || 
				(raiseflag == 0 && position == getCount() - 1)) {	//添加按钮
			if (setFlag == 0) {
				viewHolder.name.setText("");
				viewHolder.photo.setEnabled(false);
				viewHolder.photo.setImageResource(R.drawable.ic_chat_setting_item_add_enable);
				viewHolder.sub.setVisibility(View.INVISIBLE);
			}else {		
				viewHolder.name.setText("");
				viewHolder.sub.setVisibility(View.INVISIBLE);
				viewHolder.photo.setImageResource(R.drawable.ic_chat_setting_item_add);
				if (isInDelMode) {
					//正处于删除模式下，隐藏添加按钮
					convertView.setVisibility(View.GONE);
				}else {
					convertView.setVisibility(View.VISIBLE);
				}
				
				viewHolder.photo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						//进入选人界面
						Intent intent = new Intent(context, CreatChatRoomActivity.class);
						intent.putExtra("linkmans", (Serializable)data);
						activity.startActivityForResult(intent, requestCode);
					}
				});
			}
		}else {	//普通item，显示群组成员
			ChatSettingLinkman linkman = data.get(position);
			final String empCode = linkman.getEmp_id();
			viewHolder.name.setText(linkman.getEmp_name());
			Glide
					.with(context)
					.load(linkman.getEmp_photo())
					.centerCrop()
					.placeholder(R.drawable.ic_avatar)
					.crossFade()
					.into(viewHolder.photo);
			
			if (isInDelMode) {
				//如果在删除模式下，显示删除图标
				viewHolder.sub.setVisibility(View.VISIBLE);
			}else {
				viewHolder.sub.setVisibility(View.GONE);
			}
			
			viewHolder.photo.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (isInDelMode) {
						//如果删除自己，return
						if (empCode.equals(SceoUtil.getEmpCode(context))) {
							Toasty.warning(context, "不能删除自己", Toast.LENGTH_SHORT, true).show();
							return;
						}
						
						//删除该item
						data.remove(tmpPosition);
						notifyDataSetChanged();
					}else {
						Intent intent = new Intent(context, EmpDetailsActivity.class);
						intent.putExtra("empCode", empCode);
						context.startActivity(intent);
					}
				}
			});
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView photo;
		ImageView sub;//角标
		TextView name;
	}

}
