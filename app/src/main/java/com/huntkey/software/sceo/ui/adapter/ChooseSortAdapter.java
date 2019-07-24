package com.huntkey.software.sceo.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.ChatSettingLinkman;
import com.huntkey.software.sceo.widget.sortlist.SortModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ChooseSortAdapter extends BaseAdapter implements SectionIndexer{

	private List<SortModel> list = null;
	private Context mContext;
	private List<ChatSettingLinkman> linkmans;//group中一开始就有的成员
	
	//CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	private Map<String, Boolean> isCheckMap = new HashMap<String, Boolean>();
	//checkbox是否可点击
	private Map<String, Boolean> isCheckable = new HashMap<String, Boolean>();
	
	public ChooseSortAdapter(Context mContext, List<SortModel> list, List<ChatSettingLinkman> linkmans){
		this.mContext = mContext;
		this.list = list;
		this.linkmans = linkmans;
		
		configCheckMap();
	}
	
	/**
	 * 设置默认选中item
	 */
	public void configCheckMap(){
		if (linkmans != null && linkmans.size() > 0) { //从事务设置进入该界面，默认有选中项
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < linkmans.size(); j++) {
					if ((list.get(i).getEmp_id()).equals(linkmans.get(j).getEmp_id())) {
						isCheckMap.put(list.get(i).getEmp_id(), true);
						isCheckable.put(list.get(i).getEmp_id(), false);
					}
				}
			}
		}else { //新建事务进入该界面，默认无选中项
			for (int i = 0; i < list.size(); i++) {
				isCheckMap.put(list.get(i).getEmp_id(), false);
			}
		}
	}
	
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.choose_sort_item, null);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.choose_sort_item_catalog);
			viewHolder.photo = (ImageView) view.findViewById(R.id.choose_sort_item_photo);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.choose_sort_item_name);
			viewHolder.checkBox = (CheckBox) view.findViewById(R.id.choose_sort_item_checkbox);
			viewHolder.layout = (LinearLayout) view.findViewById(R.id.choose_sort_item_all);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		
		viewHolder.tvTitle.setText(mContent.getEmp_name());
		Glide
				.with(mContext)
				.load(mContent.getEmp_photo())
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(viewHolder.photo);
		
		viewHolder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//将选择项加载到map里面寄存
				isCheckMap.put(mContent.getEmp_id(), isChecked);
			}
		});
		
		if (isCheckMap.get(mContent.getEmp_id()) == null) {
			isCheckMap.put(mContent.getEmp_id(), false);
		}
		viewHolder.checkBox.setChecked(isCheckMap.get(mContent.getEmp_id()));
		
		if (isCheckable.get(mContent.getEmp_id()) == null) {
			isCheckable.put(mContent.getEmp_id(), true);
		}
		viewHolder.layout.setClickable(!isCheckable.get(mContent.getEmp_id()));
		
		return view;
	}
	
	public static class ViewHolder{
		TextView tvLetter;
		TextView tvTitle;
		ImageView photo;
		public CheckBox checkBox;
		LinearLayout layout;
	}
	
	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String  sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

}
