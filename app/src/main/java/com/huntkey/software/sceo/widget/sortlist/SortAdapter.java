package com.huntkey.software.sceo.widget.sortlist;

import java.util.List;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog.OnSweetClickListener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

public class SortAdapter extends BaseAdapter implements SectionIndexer{

	private List<SortModel> list = null;
	private Context mContext;
	
	public SortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.sort_item, null);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.sort_item_catalog);
			viewHolder.photo = (ImageView) view.findViewById(R.id.sort_item_photo);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.sort_item_name);
			viewHolder.makeCall = (ImageView) view.findViewById(R.id.sort_item_call);
			viewHolder.makeSms = (ImageView) view.findViewById(R.id.sort_item_sms);
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
	
		viewHolder.tvTitle.setText(this.list.get(position).getEmp_name());
		
		Glide
				.with(mContext)
				.load(this.list.get(position).getEmp_photo())
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(viewHolder.photo);
		
		if (list.get(position).getEmp_cellphone() == null || "".equals(list.get(position).getEmp_cellphone())) {
			viewHolder.makeCall.setVisibility(View.GONE);
		}else {
			viewHolder.makeCall.setVisibility(View.VISIBLE);
		}
		if (list.get(position).getSmsflag() != 1) {
			viewHolder.makeSms.setVisibility(View.GONE);
		}else {
			viewHolder.makeSms.setVisibility(View.VISIBLE);
		}
		
		viewHolder.makeCall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (list.get(position).getEmp_cellphone() != null && !"".equals(list.get(position).getEmp_cellphone())) {
					telDialog(list.get(position).getEmp_cellphone());
				}else {
					Toasty.warning(mContext, "无可用号码", Toast.LENGTH_SHORT, true).show();
				}
			}
		});
		
		viewHolder.makeSms.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if (list.get(position).getSmsflag() == 1) {
					smsDialog(list.get(position).getEmp_cellphone());
				}else {
					Toasty.warning(mContext, "无可用号码", Toast.LENGTH_SHORT, true).show();
				}
			}
		});
		
		return view;
	}
	


	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		ImageView photo;
		ImageView makeCall;
		ImageView makeSms;
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
	
	/**
	 * 打电话
	 * @param telNum
	 */
	private void telDialog(final String telNum){
		new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
			.setTitleText("是否拨打电话？")
			.setContentText(telNum)
			.setCancelText("暂不拨打")
			.setConfirmText("立即拨打")
			.showCancelButton(true)
			.setCancelClickListener(new OnSweetClickListener() {
				
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					sweetAlertDialog.dismiss();
				}
			})
			.setConfirmClickListener(new OnSweetClickListener() {
				
				@Override
				public void onClick(SweetAlertDialog sweetAlertDialog) {
					Uri uri = Uri.parse("tel:"+telNum);
					Intent intent = new Intent(Intent.ACTION_DIAL, uri);
					mContext.startActivity(intent);
					sweetAlertDialog.dismiss();
				}
			})
			.show();
	}
	
	/**
	 * 发短信
	 */
	private void smsDialog(final String smsNum){
		new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE)
		.setTitleText("是否发送短信？")
		.setContentText(smsNum)
		.setCancelText("暂不发送")
		.setConfirmText("立即发送")
		.showCancelButton(true)
		.setCancelClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				sweetAlertDialog.dismiss();
			}
		})
		.setConfirmClickListener(new OnSweetClickListener() {
			
			@Override
			public void onClick(SweetAlertDialog sweetAlertDialog) {
				Uri uri = Uri.parse("smsto:"+smsNum);
				Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
				mContext.startActivity(intent);
				sweetAlertDialog.dismiss();
			}
		})
		.show();
	}

}
