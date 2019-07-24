package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormAddDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FormAddLeftAdapter extends BaseAdapter {

	private List<FormAddDetails> data;
	private Context context;
	private LayoutInflater mInflater;
	private int mSelect = -1;//选中项
	
	public FormAddLeftAdapter(Context context, List<FormAddDetails> details){
		this.context = context;
		this.data = details;
		mInflater = LayoutInflater.from(context);
	}
	
	/**
	 * 刷新方法
	 */
	public void changeSelected(int position){
		if (position != mSelect) {
			mSelect = position;
			notifyDataSetChanged();
		}
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.formadd_left_item, null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.formadd_left_item_tv);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.textView.setText(data.get(position).getPpit_name());
		
		if (mSelect == position) {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
			viewHolder.textView.setTextColor(context.getResources().getColor(R.color.title_color));
		}else {
			convertView.setBackgroundColor(context.getResources().getColor(R.color.gray));
			viewHolder.textView.setTextColor(context.getResources().getColor(R.color.text_color_normal));
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView textView;
	}

}
