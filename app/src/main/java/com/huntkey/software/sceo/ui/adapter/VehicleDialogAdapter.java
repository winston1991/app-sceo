package com.huntkey.software.sceo.ui.adapter;

import com.huntkey.software.sceo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VehicleDialogAdapter extends BaseAdapter {

	private Context context;
	private String[] vehicleArray;
	private LayoutInflater mInflater;
	
	public VehicleDialogAdapter(Context context, String[] array){
		this.context = context;
		this.vehicleArray = array;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return vehicleArray.length;
	}

	@Override
	public Object getItem(int position) {
		return vehicleArray[position];
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
			convertView = mInflater.inflate(R.layout.vehicle_dialog_item, null);
			viewHolder.tv = (TextView) convertView.findViewById(R.id.vehicle_dialog_item_tv);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tv.setText(vehicleArray[position]);
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView tv;
	}

}
