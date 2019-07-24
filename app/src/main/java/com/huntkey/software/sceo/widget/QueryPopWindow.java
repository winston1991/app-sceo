package com.huntkey.software.sceo.widget;

import com.huntkey.software.sceo.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.provider.Telephony.Sms.Conversations;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by chenl
 */
public class QueryPopWindow extends PopupWindow {

	private View conentView;
	private RelativeLayout query_pop_all;
	private RelativeLayout query_pop_notclose;
	private RelativeLayout query_pop_close;
	private ImageView query_pop_all_iv;
	private ImageView query_pop_notclose_iv;
	private ImageView query_pop_close_iv;
	
	/**
	 * @param context
	 */
	public QueryPopWindow(final Activity context){
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		conentView = inflater.inflate(R.layout.popwindow_query, null);
		
		this.setContentView(conentView);
		this.setWidth(LayoutParams.WRAP_CONTENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setOutsideTouchable(true);
		this.update();// 刷新状态
		ColorDrawable dw = new ColorDrawable(0000000000);// 实例化一个ColorDrawable颜色为半透明
		this.setBackgroundDrawable(dw);// 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
		this.setAnimationStyle(R.style.AnimationPreview);// 设置SelectPicPopupWindow弹出窗体动画效果
		
		query_pop_all = (RelativeLayout) conentView.findViewById(R.id.query_pop_all);
		query_pop_notclose = (RelativeLayout) conentView.findViewById(R.id.query_pop_notclose);
		query_pop_close = (RelativeLayout) conentView.findViewById(R.id.query_pop_close);
		
		query_pop_all_iv = (ImageView) conentView.findViewById(R.id.query_pop_all_iv);
		query_pop_notclose_iv = (ImageView) conentView.findViewById(R.id.query_pop_notclose_iv);
		query_pop_close_iv = (ImageView) conentView.findViewById(R.id.query_pop_close_iv);
		
//		query_pop_notclose.performClick();
	}
	
	public void showPopWindow(View parent){
		if (!this.isShowing()) {
			// 以下拉方式显示popupwindow
			this.showAsDropDown(parent, 0, 0);
		}else {
			this.dismiss();
		}
	}
	
	public void dismissPopWindow(){
		if (this.isShowing()) {
			this.dismiss();
		}
	}
	
	public void setQueryPopAllClickLis(OnClickListener l){
		query_pop_all.setOnClickListener(l);
	}
	
	public void setQueryPopNotcloseClickLis(OnClickListener l){
		query_pop_notclose.setOnClickListener(l);
	}
	
	public void setQueryPopCloseClickLis(OnClickListener l){
		query_pop_close.setOnClickListener(l);
	}
	
	public void setQUeryPopAllEnable(boolean isTrue){
		if (isTrue) {
			query_pop_all_iv.setVisibility(View.VISIBLE);
		}else {
			query_pop_all_iv.setVisibility(View.GONE);
		}
	}
	
	public void setQUeryPopNotcloseEnable(boolean isTrue){
		if (isTrue) {
			query_pop_notclose_iv.setVisibility(View.VISIBLE);
		}else {
			query_pop_notclose_iv.setVisibility(View.GONE);
		}
	}
	
	public void setQUeryPopCloseEnable(boolean isTrue){
		if (isTrue) {
			query_pop_close_iv.setVisibility(View.VISIBLE);
		}else {
			query_pop_close_iv.setVisibility(View.GONE);
		}
	}
	
}
