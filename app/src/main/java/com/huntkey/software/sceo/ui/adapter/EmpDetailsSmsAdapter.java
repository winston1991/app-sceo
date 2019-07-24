package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.EmpDetailsTelInfo;
import com.huntkey.software.sceo.bean.EmpDetailsWorkInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmpDetailsSmsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater = null;
	private List<EmpDetailsTelInfo> data;
	
	public EmpDetailsSmsAdapter(Context context, List<EmpDetailsTelInfo> telInfo){
		this.context = context;
		this.data = telInfo;
		this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
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
			convertView = mInflater.inflate(R.layout.popupwindow_sms_item, null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.pop_sms_item_tv);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		EmpDetailsTelInfo info = data.get(position);
		viewHolder.textView.setText(info.getValue());
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView textView;
	}

}
