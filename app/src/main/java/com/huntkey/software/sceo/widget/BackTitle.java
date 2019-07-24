package com.huntkey.software.sceo.widget;

import com.huntkey.software.sceo.R;

import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author chenliang3
 */
public class BackTitle extends RelativeLayout {

	private RelativeLayout title_back;
	private TextView title_tv;
	private RelativeLayout title_launch;
	private RelativeLayout title_accessory;
	private RelativeLayout title_confirm;
	private TextView title_confirm_tv;

	public BackTitle(Context context) {
		super(context);
	}
	
	public BackTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_back_title, this);
		
		title_back = (RelativeLayout) findViewById(R.id.back_title_back);
		title_tv = (TextView) findViewById(R.id.back_title_tv); 
		title_launch = (RelativeLayout) findViewById(R.id.back_title_launch);
		title_confirm = (RelativeLayout) findViewById(R.id.back_title_confirm);
		title_confirm_tv = (TextView) findViewById(R.id.back_title_confirm_tv);
		title_accessory = (RelativeLayout) findViewById(R.id.back_title_accessory);
		
		initAttrs(context, attrs);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
//		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BackTitle);
//		int resourceId = -1;
		
//		resourceId = typedArray.getResourceId(R.styleable.BackTitle_back_title_txt, getResources().getColor(R.color.white));
//		title_tv.setTextColor(resourceId);
		
//		typedArray.recycle();
	}
	
	public void setBackBtnClickLis(OnClickListener l){
		title_back.setOnClickListener(l);
	}
	
	public void setBackTitle(String title){
		title_tv.setText(title);
	}
	
	public String getBackTitle(){
		return title_tv.getText().toString().trim();
	}
	
	public void setBackTitleColor(int color){
		title_tv.setTextColor(color);
	}
	
	public void setLunchBtnVisible(){
		title_launch.setVisibility(View.VISIBLE);
	}
	
	public void setLunchBtnClickLis(OnClickListener l){
		title_launch.setOnClickListener(l);
	}

	public void setAccessoryBtnVisible(){
		title_accessory.setVisibility(View.VISIBLE);
	}

	public void setAccessoryBtnClickLis(OnClickListener l){
		title_accessory.setOnClickListener(l);
	}
	
	public void setConfirmBtnVisible(){
		title_confirm.setVisibility(View.VISIBLE);
	}
	
	public void setConfirmClicklis(OnClickListener l){
		title_confirm.setOnClickListener(l);
	}
	
	public void setConfirmText(String s){
		title_confirm_tv.setText(s);
	}

}
