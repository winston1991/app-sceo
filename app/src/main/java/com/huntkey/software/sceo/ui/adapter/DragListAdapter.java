package com.huntkey.software.sceo.ui.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormEditDetails;
import com.huntkey.software.sceo.ui.activity.forms.FormEditActivity;
import com.huntkey.software.sceo.utils.SceoUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

public class DragListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater mInflater;
	private List<FormEditDetails> data;
	public boolean isHidden;
	private int invisilePosition = -1;
	private boolean isChanged = true;
	private boolean ShowItem = false;
	private boolean isSameDragDirection = true;
	private int lastFlag = -1;
	private int height;
	private int dragPosition = -1;
	
	//CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
	private Map<Integer, Boolean> checkPositionMap = new HashMap<Integer, Boolean>();
	private Map<String, Boolean> isCheckMap = new HashMap<String, Boolean>();
	
	private List<FormEditDetails> mCopydata = new ArrayList<>();
	
	public DragListAdapter(Context context, List<FormEditDetails> details){
		this.context = context;
		this.data = details;
		mInflater = LayoutInflater.from(context);
		
		configCheckMap();
	}
	
	/**
	 * 设置默认选中项
	 * 默认无选中项
	 */
	public void configCheckMap(){
		for (int i = 0; i < data.size(); i++) {
			isCheckMap.put(data.get(i).getKmas_id(), false);
			checkPositionMap.put(i, false);
		}
	}
	
	public String getDelMapToString(){
		if (isCheckMap == null) {
			return "";
		}
		StringBuilder result = new StringBuilder();
		boolean flag = false;
		for (int i = 0; i < data.size(); i++) {
			if (isCheckMap.get(data.get(i).getKmas_id())) {
				if (flag) {
					result.append(",");
				}else {
					flag = true;
				}
				result.append(data.get(i).getKmas_id());
			}
		}
		
		return result.toString();
	}
	
	public Map<Integer, Boolean> getDelMap(){
		return checkPositionMap;
	}
	
	public void updateList(List<FormEditDetails> details){
		this.data = details;
		notifyDataSetChanged();
	}
	
	public void showDropItem(boolean showItem){
		this.ShowItem = showItem;
	}
	
	public void setInvisiblePosition(int position){
		this.invisilePosition = position;
	}
	
	public Object getCopyItem(int position){
		return mCopydata.get(position);
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
		/**
		 * 这里尽可能每次都进行实例化新的，这样在拖拽listview的时候不会出现错乱
		 * 具体原因不明，不过这样经过测试，目前没有发现错乱
		 */
		convertView = LayoutInflater.from(context).inflate(R.layout.formedit_item, null);
		TextView name = (TextView) convertView.findViewById(R.id.formedit_item_name);
		TextView depart = (TextView) convertView.findViewById(R.id.formedit_item_dep);
//		TextView desc = (TextView) convertView.findViewById(R.id.formedit_item_desc);//部门，不再显示部门
		ImageView toTop = (ImageView) convertView.findViewById(R.id.formedit_item_top);
		ImageView toOrder = (ImageView) convertView.findViewById(R.id.formedit_item_order);
		CheckBox delBox = (CheckBox) convertView.findViewById(R.id.formedit_item_del);
		
		final FormEditDetails details = data.get(position);
		
		name.setText(details.getPpif_name());
		depart.setText(details.getPpit_name());
//		desc.setText(details.getUpper_desc());
		if (isChanged) {
			if (position == invisilePosition) {
				if (!ShowItem) {
					convertView.findViewById(R.id.formedit_item_name).setVisibility(View.INVISIBLE);
					convertView.findViewById(R.id.formedit_item_dep).setVisibility(View.INVISIBLE);
					convertView.findViewById(R.id.formedit_item_top).setVisibility(View.INVISIBLE);
					convertView.findViewById(R.id.formedit_item_order).setVisibility(View.INVISIBLE);
					convertView.findViewById(R.id.formedit_item_del).setVisibility(View.INVISIBLE);
//					convertView.findViewById(R.id.formedit_item_desc).setVisibility(View.INVISIBLE);
//					convertView.setVisibility(View.INVISIBLE);
				}
			}
			if (lastFlag != -1) {
				if (lastFlag == 1) {
					if (position > invisilePosition) {
						Animation animation;
						animation = getFromSelfAnimation(0, -height);
						convertView.startAnimation(animation);
					}
				}else if (lastFlag == 0) {
					if (position < invisilePosition) {
						Animation animation;
						animation = getFromSelfAnimation(0, height);
						convertView.startAnimation(animation);
					}
				}
			}
		}
		
		toTop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View view) {
				exchange(position, 0);
				notifyDataSetChanged();
			}
		});
		
		delBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//将选择项加载到到map里面寄存
				isCheckMap.put(details.getKmas_id(), isChecked);
				checkPositionMap.put(position, isChecked);
				if (isChecked) {
					FormEditActivity.getInstence().getLayout().setVisibility(View.VISIBLE);
				}else {
					FormEditActivity.getInstence().getLayout().setVisibility(View.GONE);
				}
			}
		});
		if (isCheckMap.get(details.getKmas_id()) == null) {
			isCheckMap.put(details.getKmas_id(), false);
			checkPositionMap.put(position, false);
		}
		delBox.setChecked(isCheckMap.get(details.getKmas_id()));
		
		return convertView;
	}
	
	/**
	 * 动态修改listview的方位
	 */
	public void exchange(int startPosition, int endPosition){
		Object startObject = getItem(startPosition);
		if (startPosition < endPosition) {
			data.add(endPosition + 1, (FormEditDetails) startObject);
			data.remove(startPosition);
		}else {
			data.add(endPosition, (FormEditDetails) startObject);
			data.remove(startPosition + 1);
		}
		isChanged = true;
//		notifyDataSetChanged();
		SceoUtil.setFormEditChange(context, true);
	}
	
	public void exchangeCopy(int startPosition, int endPosition){
		Object startObject = getCopyItem(startPosition);
		if (startPosition < endPosition) {
			mCopydata.add(endPosition + 1, (FormEditDetails) startObject);
			mCopydata.remove(startPosition);
		}else {
			mCopydata.add(endPosition, (FormEditDetails) startObject);
			mCopydata.remove(startPosition + 1);
		}
		isChanged = true;
//		notifyDataSetChanged();
		SceoUtil.setFormEditChange(context, true);
	}
	
	public void addDragItem(int start, Object obj){
		data.remove(start);
		data.add(start, (FormEditDetails) obj);
	}
	
	public void copyList(){
		mCopydata.clear();
		for (FormEditDetails details : data) {
			mCopydata.add(details);
		}
	}
	
	public void pastList(){
		data.clear();
		for (FormEditDetails details : mCopydata) {
			data.add(details);
		}
	}
	
	public void setIsSameDragDirection(boolean value){
		isSameDragDirection = value;
	}
	
	public void setLastFlag(int flag){
		lastFlag = flag;
	}
	
	public void setHeight(int value){
		height = value;
	}
	
	public void setCurrentDragPosition(int position){
		dragPosition = position;
	}
	
	public Animation getFromSelfAnimation(int x, int y){
		TranslateAnimation go = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, x,
				Animation.RELATIVE_TO_SELF, 0, Animation.ABSOLUTE, y);
		go.setInterpolator(new AccelerateDecelerateInterpolator());
		go.setFillAfter(true);
		go.setDuration(100);
		go.setInterpolator(new AccelerateInterpolator());
		return go;
	}
	
	public Animation getToSelfAnimation(int x, int y){
		TranslateAnimation go = new TranslateAnimation(
				Animation.ABSOLUTE, x, Animation.RELATIVE_TO_SELF, 0, 
				Animation.ABSOLUTE, y, Animation.RELATIVE_TO_SELF, 0);
		go.setInterpolator(new AccelerateDecelerateInterpolator());
		go.setFillAfter(true);
		go.setDuration(100);
		go.setInterpolator(new AccelerateInterpolator());
		return go;
	}
	
}
