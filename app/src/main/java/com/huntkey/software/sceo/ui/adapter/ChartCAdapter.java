package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormSecDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 图表子节点页adapter
 * @author chenliang3
 *
 */
public class ChartCAdapter extends BaseAdapter {

	private Context context;
	private List<FormSecDetails> data;
	
	public ChartCAdapter(Context context, List<FormSecDetails> details){
		this.context = context;
		this.data = details;
	}
	
	public void updateList(List<FormSecDetails> details){
		this.data = details;
		notifyDataSetChanged();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.chart_c_item, null);
			viewHolder.depart = (TextView) convertView.findViewById(R.id.chartC_item_depart);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.chartC_item_iv);
			viewHolder.theNew = (TextView) convertView.findViewById(R.id.chartC_item_new);
			viewHolder.theMonth = (TextView) convertView.findViewById(R.id.chartC_item_month);
			viewHolder.theYear = (TextView) convertView.findViewById(R.id.chartC_item_year);
			viewHolder.theWeek = (TextView) convertView.findViewById(R.id.chartC_item_week);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		FormSecDetails details = data.get(position);
		
		viewHolder.depart.setText(details.getCc_full());
		viewHolder.theNew.setText(details.getPpid_yesterday());
		viewHolder.theMonth.setText(details.getFfl_cur());
		viewHolder.theYear.setText(details.getFfl_sum());
		viewHolder.theWeek.setText(details.getPpid_week());
		
		if (!details.getSt().equals("0")) {
			viewHolder.imageView.setVisibility(View.VISIBLE);
		}else {
			viewHolder.imageView.setVisibility(View.GONE);
		}
		
		if (details.getPpid_yesterday().length() > 8) {
			viewHolder.theNew.setTextSize(12);
		}else {
			viewHolder.theNew.setTextSize(14);
		}
		if (details.getFfl_cur().length() > 8) {
			viewHolder.theMonth.setTextSize(12);
		}else {
			viewHolder.theMonth.setTextSize(14);
		}
		if (details.getFfl_sum().length() > 8) {
			viewHolder.theYear.setTextSize(12);
		}else {
			viewHolder.theYear.setTextSize(14);
		}
		if (details.getPpid_week().length() > 8) {
			viewHolder.theWeek.setTextSize(12);
		}else {
			viewHolder.theWeek.setTextSize(14);
		}
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView depart;//部门
		TextView theNew;//昨日
		TextView theMonth;//当月
		TextView theYear;//财年
		ImageView imageView;//加号
		TextView theWeek;//当周
	}

}
