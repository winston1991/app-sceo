package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.SearchResultDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResultListAdapter extends BaseAdapter {

	private Context context;
	private List<SearchResultDetails> datas;
	private LayoutInflater inflater = null;
	
	public SearchResultListAdapter(Context context, List<SearchResultDetails> datas){
		this.context = context;
		this.datas = datas;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return datas.size();
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
			convertView = inflater.inflate(R.layout.search_result_adapter_item, null);
			viewHolder.photo = (ImageView) convertView.findViewById(R.id.search_result_adapter_item_photo);
			viewHolder.title = (TextView) convertView.findViewById(R.id.search_result_adapter_item_title);
			viewHolder.number = (TextView) convertView.findViewById(R.id.search_result_adapter_item_number);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Glide
				.with(context)
				.load(datas.get(position).getPhoto())
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(viewHolder.photo);
		
		viewHolder.title.setText(datas.get(position).getEmp_name());
		viewHolder.number.setText(datas.get(position).getEmp_id());
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView photo;
		TextView title;
		TextView number;
	}

}
