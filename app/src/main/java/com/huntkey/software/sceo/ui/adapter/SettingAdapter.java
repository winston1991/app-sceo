package com.huntkey.software.sceo.ui.adapter;

import com.huntkey.software.sceo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter {

	private String titles[];
	private int images[];
	private boolean gestureState;
	private Context context;
	private LayoutInflater mInflater;
	
	public SettingAdapter(Context context, String[] titles, int[] images, boolean gestureState){
		this.context = context;
		this.titles = titles;
		this.images = images;
		this.gestureState = gestureState;
		mInflater = LayoutInflater.from(context);
	}
	
	public void updateGestureState(boolean gestureState){
		this.gestureState = gestureState;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return titles.length;
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
			convertView = mInflater.inflate(R.layout.setting_items, null);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.setting_item_iv);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.setting_item_tv);
			viewHolder.gestureStateTv = (TextView) convertView.findViewById(R.id.setting_item_state_tv);
			viewHolder.gestureStateIv = (ImageView) convertView.findViewById(R.id.setting_item_state_iv);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.imageView.setImageResource(images[position]);
		viewHolder.textView.setText(titles[position]);
		
		if (position == 1) {
			viewHolder.gestureStateTv.setVisibility(View.VISIBLE);
			viewHolder.gestureStateIv.setVisibility(View.VISIBLE);
			if (gestureState) {
				viewHolder.gestureStateTv.setText("已开启");
				viewHolder.gestureStateIv.setImageResource(R.drawable.icon_lock);
			}else {
				viewHolder.gestureStateTv.setText("未开启");
				viewHolder.gestureStateIv.setImageResource(R.drawable.icon_unlock);
			}
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView imageView;
		TextView textView;
		TextView gestureStateTv;
		ImageView gestureStateIv;
	}

}
