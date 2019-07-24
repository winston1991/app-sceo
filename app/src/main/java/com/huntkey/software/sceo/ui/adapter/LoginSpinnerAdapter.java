package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.UrlDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class LoginSpinnerAdapter extends BaseAdapter {

	private Context context;
	private List<UrlDetails> data;
	private LayoutInflater mLayoutInflater = null;
	
	public LoginSpinnerAdapter(Context context, List<UrlDetails> list){
		this.context = context;
		this.data = list;
		this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			convertView = mLayoutInflater.inflate(R.layout.login_spinner_item, null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.login_spinner_tv);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		UrlDetails details = data.get(position);
		
		viewHolder.textView.setText(details.getSrvname());
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView textView;
	}

}
