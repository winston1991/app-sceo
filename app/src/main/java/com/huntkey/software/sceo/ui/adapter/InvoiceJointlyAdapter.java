package com.huntkey.software.sceo.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.JointlyListDetails;
import com.huntkey.software.sceo.bean.SearchResultDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class InvoiceJointlyAdapter extends BaseAdapter {

	private List<SearchResultDetails> data;
	private List<JointlyListDetails> jointlyData;
	private List<JointlyListDetails> jointlyAdd;
	private List<JointlyListDetails> totalData = new ArrayList<>();
	private Context context;
	private LayoutInflater inflater = null;
	
	//CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();
	//checkbox是否可点击
	private Map<Integer, Boolean> isCheckable = new HashMap<Integer, Boolean>();
	
	public InvoiceJointlyAdapter(Context context, List<SearchResultDetails> d, 
			List<JointlyListDetails> jd, List<JointlyListDetails> ja){
		this.context = context;
		this.data = d;
		this.jointlyData = jd;
		this.jointlyAdd = ja;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public void updateAdds(List<JointlyListDetails> datas, List<JointlyListDetails> adds){
		this.jointlyData = datas;
		this.jointlyAdd = adds;
		notifyDataSetChanged();
	}
	
	/**
	 * 设置默认选中item
	 */
	public void configCheckMap(){
		if (jointlyAdd != null && jointlyAdd.size() > 0) {
				totalData.addAll(jointlyAdd);
		}
		if (jointlyData != null && jointlyData.size() > 0) {
			totalData.addAll(jointlyData);
		}
		
		if (totalData != null && totalData.size() > 0) { 
			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < totalData.size(); j++) {
					if ((data.get(i).getEmp_id()).equals(totalData.get(j).getEmp_id())) {
						isCheckMap.put(i, true);
						isCheckable.put(i, false);
					}
				}
			}
		}else { 
			for (int i = 0; i < data.size(); i++) {
				isCheckMap.put(i, false);
			}
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
	
	public void updateList(List<SearchResultDetails> data){
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		configCheckMap();
		
		JointlyViewHolder viewHolder = null;
		final SearchResultDetails details = data.get(position);
		if (convertView == null) {
			viewHolder = new JointlyViewHolder();
			convertView = inflater.inflate(R.layout.invoice_jointly_adapter_item, null);
			viewHolder.photo = (ImageView) convertView.findViewById(R.id.invoice_jointly_adapter_item_photo);
			viewHolder.title = (TextView) convertView.findViewById(R.id.invoice_jointly_adapter_item_title);
			viewHolder.number = (TextView) convertView.findViewById(R.id.invoice_jointly_adapter_item_number);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.invoice_jointly_adapter_item_checkbox);
			viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.invoice_jointly_adapter_item_all);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (JointlyViewHolder) convertView.getTag();
		}
		
		Glide
				.with(context)
				.load(details.getPhoto())
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(viewHolder.photo);
		
		viewHolder.title.setText(details.getEmp_name());
		viewHolder.number.setText(details.getEmp_id());
		
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//将选择项加载到map里面寄存
				isCheckMap.put(position, isChecked);
			}
		});
		
		if (isCheckMap.get(position) == null) {
			isCheckMap.put(position, false);
		}
		viewHolder.checkBox.setChecked(isCheckMap.get(position));
		
		if (isCheckable.get(position) == null) {
			isCheckable.put(position, true);
		}
		viewHolder.layout.setClickable(!isCheckable.get(position));
		
		return convertView;
	}
	
	public static class JointlyViewHolder{
		ImageView photo;
		TextView title;
		TextView number;
		public CheckBox checkBox;
		RelativeLayout layout;
	}

}
