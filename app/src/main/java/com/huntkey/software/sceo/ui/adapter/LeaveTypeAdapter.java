package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.LeaveInitType;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LeaveTypeAdapter extends BaseAdapter {

	private List<LeaveInitType> types;
	private Context context;
	private LayoutInflater mInflater;
	
	public LeaveTypeAdapter(Context context, List<LeaveInitType> list){
		this.context = context;
		this.types = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return types.size();
	}

	@Override
	public Object getItem(int position) {
		return types.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.leave_type_item, null);
			viewHolder.typeTv = (TextView) convertView.findViewById(R.id.leave_type_item_tv);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.typeTv.setText(types.get(position).getAdlt_name());
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView typeTv;
	}

}
