package com.huntkey.software.sceo.ui.adapter;

import java.util.List;
import java.util.zip.Inflater;

import com.huntkey.software.sceo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SearchMemoryListAdapter extends BaseAdapter {

	private Context context;
	private List<String> memoryData;
	private LayoutInflater inflater = null;
	
	public SearchMemoryListAdapter(Context context, List<String> memoryData){
		this.context = context;
		this.memoryData = memoryData;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return memoryData.size();
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
			convertView = inflater.inflate(R.layout.search_memory_adapter_item, null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.search_memory_adapter_item_tv);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.textView.setText(memoryData.get(position));
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView textView;
	}

}
