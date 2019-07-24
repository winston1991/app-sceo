package com.huntkey.software.sceo.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.DepartmentDetails;
import com.huntkey.software.sceo.utils.SceoUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DepartmentAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context context;
	private List<DepartmentDetails> details;
	private ViewHolder viewHolder;
	private boolean isFirst = true;
	
	private Map<String, Boolean> isCheckMap = new HashMap<>();
	
	public DepartmentAdapter(Context context, List<DepartmentDetails> details){
		this.context = context;
		this.details = details;
	}
	
	@Override
	public int getCount() {
		return details.size();
	}

	@Override
	public Object getItem(int position) {
		return details.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	String tmpCcLevel;
	public void setCClevel(String str){
		this.tmpCcLevel = str;
	}
	
	public List<String> getCCData(){
		String ccCode = null;
		String ccName = null;
		String ccLevel = null;
		List<String> tmp = new ArrayList<>();
		for (String p : isCheckMap.keySet()) {
			if (isCheckMap.get(p)) {
				for (int i = 0; i < details.size(); i++) {
					if (details.size() > 0 && p != null && p.equals(details.get(i).getCc_level())) {
						ccCode = details.get(i).getCc_code();
						ccName = details.get(i).getCc_desc();
						ccLevel = details.get(i).getCc_level();
					}
				}
			}
		}
		tmp.add(ccCode);
		tmp.add(ccName);
		tmp.add(ccLevel);
		
		return tmp;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (convertView == null) {			
			if (inflater == null) {
				inflater = LayoutInflater.from(context);
			}
			viewHolder = new ViewHolder();
			
			convertView = inflater.inflate(R.layout.department_item, null);
			viewHolder.arrow = (ImageView) convertView.findViewById(R.id.depart_item_icon);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.depart_item_photo);
			viewHolder.name = (TextView) convertView.findViewById(R.id.depart_item_name);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.depart_item_checkbox);
			viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.depart_item_layout);
			
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
			
		DepartmentDetails detail = details.get(position);
		
		if (detail.isHasChild()) {//有子节点
			viewHolder.arrow.setVisibility(View.VISIBLE);//显示箭头
			if (detail.isFold()) {//展开
				viewHolder.arrow.setImageResource(R.drawable.ic_hassub_sub);
			}else {
				viewHolder.arrow.setImageResource(R.drawable.ic_hassub);
			}
		}else {//没有子节点
			viewHolder.arrow.setVisibility(View.INVISIBLE);//隐藏箭头
		}
		viewHolder.layout.setPadding(20 * detail.getLevel(), 0, 0, 0);//根据层级设置缩进
		viewHolder.name.setText(detail.getCc_desc());
						
		final String ccLevel = detail.getCc_level();
//		viewHolder.checkBox.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				boolean cb = !isCheckMap.get(ccLevel);
//				//将所有位置置为false
//				for (String p : isCheckMap.keySet()) {
//					isCheckMap.put(p, false);
//				}
//				isCheckMap.put(ccLevel, cb);
//				DepartmentAdapter.this.notifyDataSetChanged();
//				isFirst = false;
//			}
//		});
		
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//将所有位置置为false
				for (String p : isCheckMap.keySet()) {
					isCheckMap.put(p, false);
				}
				isCheckMap.put(tmpCcLevel, true);
				DepartmentAdapter.this.notifyDataSetChanged();
				isFirst = false;
			}
		});
		
		if (isCheckMap.get(ccLevel) == null) {
			isCheckMap.put(ccLevel, false);
		}
		
		String formCCcode = SceoUtil.shareGetString(context, SceoUtil.getAcctid(context)+SceoUtil.SHARE_FORM_CCCODE);
		if (isFirst && !"".equals(formCCcode) && detail.getCc_code().equals(formCCcode)) {
			viewHolder.checkBox.setChecked(true);
		}else {			
			viewHolder.checkBox.setChecked(isCheckMap.get(ccLevel));
		}
		
		return convertView;
	}
	
	public static class ViewHolder{
		ImageView arrow;
		ImageView image;
		TextView name;
		public CheckBox checkBox;
		RelativeLayout layout;
	}

}
