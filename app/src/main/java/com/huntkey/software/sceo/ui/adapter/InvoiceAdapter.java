package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.R.color;
import com.huntkey.software.sceo.bean.InvoiceDetails;
import com.huntkey.software.sceo.bean.InvoiceInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InvoiceAdapter extends BaseAdapter {

	private Context context;
	private List<InvoiceDetails> data;
	private LayoutInflater mInflater = null;
	private String tmpId;
	
	public InvoiceAdapter(Context context, List<InvoiceDetails> details){
		this.context = context;
		this.data = details;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
	
	public void updateList(List<InvoiceDetails> details){
		this.data = details;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.invoice_entry_item, null);
			viewHolder.name = (TextView) convertView.findViewById(R.id.invoice_item_name);
			viewHolder.number = (TextView) convertView.findViewById(R.id.invoice_item_number);
			viewHolder.time = (TextView) convertView.findViewById(R.id.invoice_item_time);
			viewHolder.n1 = (TextView) convertView.findViewById(R.id.invoice_item_n1);
			viewHolder.n2 = (TextView) convertView.findViewById(R.id.invoice_item_n2);
			viewHolder.n3 = (TextView) convertView.findViewById(R.id.invoice_item_n3);
			viewHolder.n4 = (TextView) convertView.findViewById(R.id.invoice_item_n4);
			viewHolder.c1 = (TextView) convertView.findViewById(R.id.invoice_item_c1);
			viewHolder.c2 = (TextView) convertView.findViewById(R.id.invoice_item_c2);
			viewHolder.c3 = (TextView) convertView.findViewById(R.id.invoice_item_c3);
			viewHolder.c4 = (TextView) convertView.findViewById(R.id.invoice_item_c4);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		InvoiceDetails details = data.get(position);
		
		viewHolder.name.setText(details.getWfm_name());
		viewHolder.number.setText(details.getWfna_nbr());
		if (details.getColor_nbr() != null && !"".equals(details.getColor_nbr())) {			
			viewHolder.number.setTextColor(Color.parseColor(details.getColor_nbr()));
		}else {
			viewHolder.number.setTextColor(context.getResources().getColor(color.text_color_weak));
		}
		viewHolder.time.setText(details.getWfna_addtime());
		viewHolder.n1.setText(details.getShowname_n1());
		if (details.getColor_n1() != null && !"".equals(details.getColor_n1())) {			
			viewHolder.n1.setTextColor(Color.parseColor(details.getColor_n1()));
		}else {
			viewHolder.n1.setTextColor(context.getResources().getColor(color.text_color_weak));
		}
		if (details.getBcolor_n1() != null && !"".equals(details.getBcolor_n1())) {			
			viewHolder.n1.setBackgroundColor(Color.parseColor(details.getBcolor_n1()));
		}else {
			viewHolder.n1.setBackgroundColor(context.getResources().getColor(color.transparent));
		}
		viewHolder.n2.setText(details.getShowname_n2());
		if (details.getColor_n2() != null && !"".equals(details.getColor_n2())) {
			viewHolder.n2.setTextColor(Color.parseColor(details.getColor_n2()));			
		}else {
			viewHolder.n2.setTextColor(context.getResources().getColor(color.text_color_weak));
		}
		if (details.getBcolor_n2() != null && !"".equals(details.getBcolor_n2())) {			
			viewHolder.n2.setBackgroundColor(Color.parseColor(details.getBcolor_n2()));
		}else {
			viewHolder.n2.setBackgroundColor(context.getResources().getColor(color.transparent));
		}
		viewHolder.n3.setText(details.getShowname_n3());
		if (details.getColor_n3() != null && !"".equals(details.getColor_n3())) {			
			viewHolder.n3.setTextColor(Color.parseColor(details.getColor_n3()));
		}else {
			viewHolder.n3.setTextColor(context.getResources().getColor(color.text_color_weak));
		}
		if (details.getBcolor_n3() != null && !"".equals(details.getBcolor_n3())) {			
			viewHolder.n3.setBackgroundColor(Color.parseColor(details.getBcolor_n3()));
		}else {
			viewHolder.n3.setBackgroundColor(context.getResources().getColor(color.transparent));
		}
		viewHolder.n4.setText(details.getShowname_n4());
		if (details.getColor_n4() != null && !"".equals(details.getColor_n4())) {			
			viewHolder.n4.setTextColor(Color.parseColor(details.getColor_n4()));
		}else {
			viewHolder.n4.setTextColor(context.getResources().getColor(color.text_color_weak));
		}
		if (details.getBcolor_n4() != null && !"".equals(details.getBcolor_n4())) {			
			viewHolder.n4.setBackgroundColor(Color.parseColor(details.getBcolor_n4()));
		}else {
			viewHolder.n4.setBackgroundColor(context.getResources().getColor(color.transparent));
		}
		
		showNameCountControl(details.getShowname_count1(), viewHolder.c1);
		showNameCountControl(details.getShowname_count2(), viewHolder.c2);
		showNameCountControl(details.getShowname_count3(), viewHolder.c3);
		showNameCountControl(details.getShowname_count4(), viewHolder.c4);
		
		return convertView;
	}
	
	private void showNameCountControl(String count, TextView textView){
		if (count.length() < 10 && !"0".equals(count) && !"1".equals(count)) {
			textView.setText(count);
		}else if ("0".equals(count) || "1".equals(count)) {
			textView.setText("");
		}else {
			textView.setText("*");
		}
	}
	
	private class ViewHolder{
		TextView name;
		TextView number;
		TextView time;
		TextView n1;
		TextView n2;
		TextView n3;
		TextView n4;
		TextView c1;
		TextView c2;
		TextView c3;
		TextView c4;
	}

}
