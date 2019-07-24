package com.huntkey.software.sceo.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormEditDetails;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class FormAddRightAdapter extends BaseAdapter {

	private Context context;
	private List<FormEditDetails> data;//分类下的数据
	private List<FormEditDetails> exitDetails;//已经选择的数据
	private List<FormEditDetails> saveDetails;//先前已保存到list的数据
	private LayoutInflater mInflater;
	
	//CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	private Map<String, Boolean> isCheckMap = new HashMap<String, Boolean>();
	//checkbox是否可点击
	private Map<String, Boolean> isCheckable = new HashMap<String, Boolean>();
	
	public FormAddRightAdapter(Context context, List<FormEditDetails> details, 
			List<FormEditDetails> editDetails, List<FormEditDetails> saveDetails){
		this.context = context;
		this.data = details;
		this.exitDetails = editDetails;
		this.saveDetails = saveDetails;
		mInflater = LayoutInflater.from(context);
		
		configCheckMap();
	}
	
	public void configCheckMap(){
		if (exitDetails != null && exitDetails.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < exitDetails.size(); j++) {
					if (data.get(i).getPpif_id().equals(exitDetails.get(j).getPpif_id())) {
						isCheckMap.put(data.get(i).getPpif_id(), true);
						isCheckable.put(data.get(i).getPpif_id(), false);
					}
				}
			}
		}else {
			for (int i = 0; i < data.size(); i++) {
				isCheckMap.put(data.get(i).getPpif_id(), false);
			}
		}
		
		if (saveDetails != null && saveDetails.size() > 0) {
			for (int i = 0; i < data.size(); i++) {
				for (int j = 0; j < saveDetails.size(); j++) {
					if (data.get(i).getPpif_id().equals(saveDetails.get(j).getPpif_id())) {
						isCheckMap.put(data.get(i).getPpif_id(), true);
					}
				}
			}
		}
	}
	
	public void updateSaveList(List<FormEditDetails> saveDetails){
		this.saveDetails = saveDetails;
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.formadd_right_item, null);
			viewHolder.textView = (TextView) convertView.findViewById(R.id.formadd_right_item_tv);
//			viewHolder.textView2 = (TextView) convertView.findViewById(R.id.formadd_right_item_tv2);
			viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.formadd_right_item_checkbox);
			viewHolder.imageView = (ImageView) convertView.findViewById(R.id.formadd_right_item_checked);
			viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.formadd_right_item_layout);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		final FormEditDetails details = data.get(position);
		
		viewHolder.textView.setText(details.getPpif_name());
//		viewHolder.textView2.setText(details.getUpper_desc());
		
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//将选择项加载到map里面寄存
				isCheckMap.put(details.getPpif_id(), isChecked);
			}
		});
		if (isCheckMap.get(details.getPpif_id()) == null) {
			isCheckMap.put(details.getPpif_id(), false);
		}
		viewHolder.checkBox.setChecked(isCheckMap.get(details.getPpif_id()));
		
		if (isCheckable.get(details.getPpif_id()) == null) {
			isCheckable.put(details.getPpif_id(), true);
		}
		viewHolder.layout.setClickable(!isCheckable.get(details.getPpif_id()));
		if (!isCheckable.get(details.getPpif_id())) {
			viewHolder.imageView.setVisibility(View.VISIBLE);
			viewHolder.checkBox.setVisibility(View.GONE);
		}else {
			viewHolder.imageView.setVisibility(View.GONE);
			viewHolder.checkBox.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	
	public static class ViewHolder{
		TextView textView;
//		TextView textView2;//部门，不再显示部门
		public CheckBox checkBox;
		ImageView imageView;
		RelativeLayout layout;
	}

}
