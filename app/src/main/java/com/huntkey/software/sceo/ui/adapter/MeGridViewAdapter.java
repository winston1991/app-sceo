package com.huntkey.software.sceo.ui.adapter;

import com.huntkey.software.sceo.R;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MeGridViewAdapter extends BaseAdapter {

	private int[] imageArray;
	private String[] textArray;
	private boolean hasNewVersion;
	private Context context;
	private LayoutInflater inflater;
	
	public MeGridViewAdapter(Context contexts, int[] imageArray, String[] textArray, boolean hasNewVersion){
		this.context = contexts;
		this.imageArray = imageArray;
		this.textArray = textArray;
		this.hasNewVersion = hasNewVersion;
		this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return imageArray.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void updateAdapter(boolean hasNewVersion){
		this.hasNewVersion = hasNewVersion;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.me_gridview_item, null);
			viewHolder.iv = (ImageView) convertView.findViewById(R.id.me_grid_item_iv);
			viewHolder.tv = (TextView) convertView.findViewById(R.id.me_grid_item_tv);
			viewHolder.numView = (ImageView) convertView.findViewById(R.id.me_grid_item_num);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.iv.setImageResource(imageArray[position]);
		viewHolder.tv.setText(textArray[position]);
		if (position == 2 && hasNewVersion) {
			viewHolder.numView.setVisibility(View.VISIBLE);
		}else {
			viewHolder.numView.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView iv;
		TextView tv;
		ImageView numView;
	}

}
