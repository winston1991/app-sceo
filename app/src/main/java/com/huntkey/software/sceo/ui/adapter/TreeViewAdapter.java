package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.TreeElement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TreeViewAdapter extends BaseAdapter {

    class ViewHolder {
        ImageView icon;
        TextView title;
        
        ImageView photo;
        TextView number;
        RelativeLayout layout;
     }
   
     Context context;
     ViewHolder holder;
     LayoutInflater inflater;
   
     List<TreeElement> elements;
   
     public TreeViewAdapter(Context context, List<TreeElement> elements) {
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
     public View getView(int position, View convertView, ViewGroup parent) {
        /**
         * ---------------------- get holder------------------------
         */
        if (convertView == null) {
            if (inflater == null) {
               inflater = LayoutInflater.from(context);
            }
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.tree_view_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.tree_view_item_icon);
            holder.title = (TextView) convertView.findViewById(R.id.tree_view_item_title);
            
            holder.photo = (ImageView) convertView.findViewById(R.id.tree_view_item_photo);
            holder.number = (TextView) convertView.findViewById(R.id.tree_view_item_number);
            holder.layout = (RelativeLayout) convertView.findViewById(R.id.tree_view_item_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
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
            
            holder.number.setVisibility(View.GONE);
        } else {// 没有子节点
            holder.icon.setImageResource(R.drawable.tree_view_icon_close);
            holder.icon.setVisibility(View.INVISIBLE);// 没有子节点，要隐藏图标
            
            holder.number.setVisibility(View.VISIBLE);
            holder.number.setText(elements.get(position).getCc_code());
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
        
        return convertView;
     }

}
