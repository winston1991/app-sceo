package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.InvoiceFlowDetails;
import com.huntkey.software.sceo.widget.RotateTextView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InvoiceFlowAdapter extends BaseAdapter {

	private Context context;
	private List<InvoiceFlowDetails> flowDetails;
	private String count1;
	private String count2;
	private String count3;
	private String count4;
	private LayoutInflater mInflater;
	
	public InvoiceFlowAdapter(Context context, List<InvoiceFlowDetails> flowDetails,
			String count1, String count2, String count3, String count4){
		this.context = context;
		this.flowDetails = flowDetails;
		this.count1 = count1;
		this.count2 = count2;
		this.count3 = count3;
		this.count4 = count4;
		mInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return flowDetails.size();
	}

	@Override
	public Object getItem(int position) {
		return flowDetails.get(position);
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
			convertView = mInflater.inflate(R.layout.invoice_flow_item, null);
			viewHolder.spaceView = convertView.findViewById(R.id.invoiceFlow_item_space);
			viewHolder.article = (TextView) convertView.findViewById(R.id.invoiceFlow_item_article);
			viewHolder.name = (TextView) convertView.findViewById(R.id.invoiceFlow_item_name);
			viewHolder.way = (ImageView) convertView.findViewById(R.id.invoiceFlow_item_way);
			viewHolder.time = (TextView) convertView.findViewById(R.id.invoiceFlow_item_time);
			viewHolder.desc = (TextView) convertView.findViewById(R.id.invoiceFlow_item_desc);
			viewHolder.status = (RotateTextView) convertView.findViewById(R.id.invoiceFlow_item_status);
			viewHolder.role = (TextView) convertView.findViewById(R.id.invoiceFlow_item_role);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		InvoiceFlowDetails details = flowDetails.get(position);
				
		if ("1".equals(details.getAudit_state())) {//已审
			viewHolder.article.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_corners_blue));
		}else {
			viewHolder.article.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_corners_gray_v2));
		}
		viewHolder.name.setText(details.getAudit_type()+"："+details.getWfnd_emp_name());
		if ("0".equals(details.getWfna_phone())) {//pc
			viewHolder.way.setVisibility(View.GONE);
		}else if ("1".equals(details.getWfna_phone())) {//mobile
			viewHolder.way.setVisibility(View.VISIBLE);
		}
		viewHolder.time.setText(details.getAudit_time());
		viewHolder.role.setText(details.getWfnd_name());
		if ("".equals(details.getRemark())) {
			viewHolder.desc.setVisibility(View.GONE);
		}else {	
			viewHolder.desc.setVisibility(View.VISIBLE);
			viewHolder.desc.setText(details.getRemark());
		}
		viewHolder.status.setText(details.getFlowstate());
		
		switch (details.getFlowstate()) {
		case "通过":
			viewHolder.status.setTextColor(Color.parseColor("#5CC51A"));
			break;
		case "退回":
			viewHolder.status.setTextColor(Color.parseColor("#FE3426"));
			break;
		case "待审":
			viewHolder.status.setTextColor(Color.parseColor("#8F8F8F"));
			break;
		case "待知会":
			viewHolder.status.setTextColor(Color.parseColor("#7EBCFF"));
			break;
		case "--":
			viewHolder.status.setTextColor(Color.parseColor("#2B2B2B"));
			break;
		case "作废":
			viewHolder.status.setTextColor(Color.parseColor("#FE3426"));
			break;

		default:
			viewHolder.status.setTextColor(Color.parseColor("#5CC51A"));
			break;
		}
		
		switch (details.getWfnd_stepname()) {
		case "申请":
			viewHolder.article.setText(details.getWfnd_stepname()+count1);
			break;
		case "审核":
			viewHolder.article.setText(details.getWfnd_stepname()+count2);
			break;
		case "复核":
			viewHolder.article.setText(details.getWfnd_stepname()+count3);
			break;
		case "批准":
			viewHolder.article.setText(details.getWfnd_stepname()+count4);
			break;
		}
		
		//----------------------todo-----------------------
		if ("1".equals(details.getStepname_show())) {
			viewHolder.article.setVisibility(View.VISIBLE);
			viewHolder.spaceView.setVisibility(View.VISIBLE);
		}else {
			viewHolder.article.setVisibility(View.INVISIBLE);
			viewHolder.spaceView.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		View spaceView;
		TextView article;
		TextView name;
		ImageView way;
		TextView time;
		TextView desc;
		RotateTextView status;
		TextView role;
	}

}
