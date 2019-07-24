package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.EmpDetailsWorkInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmpDetailsAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater = null;
	private List<EmpDetailsWorkInfo> data;
	
	public EmpDetailsAdapter(Context context, List<EmpDetailsWorkInfo> workInfo){
		this.context = context;
		this.data = workInfo;
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
			convertView = mInflater.inflate(R.layout.view_entry_item_tmp, null);
			viewHolder.leftTv = (TextView) convertView.findViewById(R.id.item_tmp_leftTv);
			viewHolder.rightTv = (TextView) convertView.findViewById(R.id.item_tmp_rightTv);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		EmpDetailsWorkInfo info = data.get(position);
		viewHolder.leftTv.setText(info.getKey());
		viewHolder.rightTv.setText(info.getValue());
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView leftTv;
		TextView rightTv;
	}

}
