package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.LeaveAgencyDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LeaveAgencyAdapter extends BaseAdapter {

	private Context context;
	private List<LeaveAgencyDetails> details;
	private LayoutInflater mInflater;
	
	public LeaveAgencyAdapter(Context context, List<LeaveAgencyDetails> list){
		this.context = context;
		this.details = list;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return details.size();
	}

	@Override
	public Object getItem(int position) {
		return details.get(position);
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
			viewHolder.name = (TextView) convertView.findViewById(R.id.leave_type_item_tv);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.name.setText(details.get(position).getEmp_name());
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView name;
	}

}
