package com.huntkey.software.sceo.ui.adapter;

import java.util.List;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.AffairsDetails;
import com.huntkey.software.sceo.utils.StringUtil;
import com.huntkey.software.sceo.widget.bitmaps2one.PuzzleView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AffairsAdapter extends BaseAdapter {

	private Context context;
	private List<AffairsDetails> data;
	private LayoutInflater mInflater = null;

	public AffairsAdapter(Context context, List<AffairsDetails> list){
		this.context = context;
		this.data = list;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		return position%2;
	}
	
	public void updateList(List<AffairsDetails> details){
		this.data = details;
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.affairs_entry_item, null);
			viewHolder.photo = (ImageView) convertView.findViewById(R.id.affairs_item_iv);
			viewHolder.puzzleView = (PuzzleView) convertView.findViewById(R.id.affairs_item_puzzleview);
			viewHolder.num = (TextView) convertView.findViewById(R.id.affairs_item_num);
			viewHolder.name = (TextView) convertView.findViewById(R.id.affairs_item_name);
			viewHolder.lastTask = (TextView) convertView.findViewById(R.id.affairs_item_lasttask);
			viewHolder.time = (TextView) convertView.findViewById(R.id.affairs_item_time);
			viewHolder.people = (TextView) convertView.findViewById(R.id.affairs_item_people);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		AffairsDetails details = data.get(position);
		
		if (details.getUnreadno() == 0) {
			viewHolder.num.setVisibility(View.GONE);
		}else {
			viewHolder.num.setVisibility(View.VISIBLE);
			viewHolder.num.setText(details.getUnreadno()+"");
		}
		
		viewHolder.lastTask.setText(details.getLasttask());
		viewHolder.time.setText(details.getTasktime());
		viewHolder.name.setText(details.getTaskname());
		viewHolder.people.setText("("+details.getEmpcount()+")");
		
		Glide
				.with(context)
				.load(StringUtil.getImgPaths(details.getEmp_photo()).get(0))
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(viewHolder.photo);
		
		return convertView;
	}
	
	private class ViewHolder{
		ImageView photo;
		PuzzleView puzzleView;
		TextView people;
		TextView num;
		TextView name;
		TextView time;
		TextView lastTask;
	}

}
