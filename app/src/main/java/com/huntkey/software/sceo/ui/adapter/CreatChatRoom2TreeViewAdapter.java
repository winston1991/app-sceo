package com.huntkey.software.sceo.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.TreeElement;

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

public class CreatChatRoom2TreeViewAdapter extends BaseAdapter {

    public static class CSHolder {
        ImageView icon;
        TextView title;
        
        ImageView photo;
        public CheckBox checkBox;
        RelativeLayout layout;
     }
   
     Context context;
     CSHolder holder;
     LayoutInflater inflater;
   
     List<TreeElement> elements;
     
     //CheckBox 是否选择的存储集合,key 是 position , value 是该position是否选中
// 	 private Map<Integer, Boolean> isCheckMap = new HashMap<Integer, Boolean>();
     private Map<String, Boolean> isCheckMap = new HashMap<>();
   
     public CreatChatRoom2TreeViewAdapter(Context context, List<TreeElement> elements) {
        this.context = context;
        this.elements = elements;
     }
     
     @Override
     public int getCount() {
        return elements.size();
     }
   
     @Override
     public Object getItem(int position) {
        return elements.get(position);
     }
   
     @Override
     public long getItemId(int position) {
        return position;
     }
   
     @Override
     public View getView(final int position, View convertView, ViewGroup parent) {
        /**
         * ---------------------- get holder------------------------
         */
        if (convertView == null) {
            if (inflater == null) {
               inflater = LayoutInflater.from(context);
            }
            holder = new CSHolder();
            convertView = inflater.inflate(R.layout.creat_chatroom2_tree_view_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.cs_tree_view_item_icon);
            holder.title = (TextView) convertView.findViewById(R.id.cs_tree_view_item_title);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cs_tree_view_item_checkbox);
            holder.photo = (ImageView) convertView.findViewById(R.id.cs_tree_view_item_photo);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.cs_tree_view_item_layout);
            convertView.setTag(holder);
        } else {
            holder = (CSHolder) convertView.getTag();
        }
        /**
         * ---------------------- set holder--------------------------
         */
        if (elements.get(position).isHasChild()) {// 有子节点
            if (elements.get(position).isFold()) {
               holder.icon.setImageResource(R.drawable.tree_view_icon_open);
               holder.photo.setImageResource(R.drawable.ic_department_open);
            } else if (!elements.get(position).isFold()) {
               holder.icon.setImageResource(R.drawable.tree_view_icon_close);
               holder.photo.setImageResource(R.drawable.ic_department);
            }
            holder.icon.setVisibility(View.VISIBLE);// 有子节点，要显示图标
            holder.checkBox.setVisibility(View.GONE);
        } else {// 没有子节点
            holder.icon.setImageResource(R.drawable.tree_view_icon_close);
            holder.icon.setVisibility(View.INVISIBLE);// 没有子节点，要隐藏图标
            
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        holder.layout.setPadding(18 * (elements.get(position).getLevel()), 0, 0, 0);// 根据层级设置缩进
        holder.title.setText(elements.get(position).getTitle());
//        holder.title.setTextSize(16 - elements.get(position).getLevel() * 1); // 根据层级设置字体大小
        
		if (elements.get(position).getFlag() == 1) {
            Glide
                    .with(context)
                    .load(elements.get(position).getPhoto())
                    .centerCrop()
                    .placeholder(R.drawable.ic_avatar)
                    .crossFade()
                    .into(holder.photo);
		}
		
		holder.checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				//将选择项加载到map里面寄存
//				isCheckMap.put(position, isChecked);
				isCheckMap.put(elements.get(position).getCc_code(), isChecked);
			}
		});
		
		if (isCheckMap.get(elements.get(position).getCc_code()) == null) {
			isCheckMap.put(elements.get(position).getCc_code(), false);
		}
		holder.checkBox.setChecked(isCheckMap.get(elements.get(position).getCc_code()));
        
        return convertView;
     }

}
