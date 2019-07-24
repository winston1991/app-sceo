package com.huntkey.software.sceo.widget;

import com.huntkey.software.sceo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 通用标题栏
 * @author chenliang3
 *
 */
public class CommonTitle extends RelativeLayout {

	private TextView title_middle;
	private TextView title_left;
	private TextView title_right;

	public CommonTitle(Context context) {
		super(context);
	}
	
	public CommonTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_common_title, this);
		
		title_middle = (TextView) findViewById(R.id.common_title_middle); 
		title_left = (TextView) findViewById(R.id.common_title_left);
		title_right = (TextView) findViewById(R.id.common_title_right);
		
//		initAttrs(context, attrs);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonTitle);
		int resourceId = -1;
		
		resourceId = typedArray.getResourceId(R.styleable.CommonTitle_common_txt_left, R.color.white);
		title_left.setTextColor(resourceId);
		
		resourceId = typedArray.getResourceId(R.styleable.CommonTitle_common_txt_middle, R.color.white);
		title_middle.setTextColor(resourceId);
		
		resourceId = typedArray.getResourceId(R.styleable.CommonTitle_common_txt_right, R.color.white);
		title_right.setTextColor(resourceId);
		
		typedArray.recycle();
	}
	
	public void setTitleLeftClickLis(OnClickListener l){
		title_left.setOnClickListener(l);
	}
	
	public void setMiddleTitle(String title){
		title_middle.setText(title);
	}
	public void setTitleMiddleClickLis(OnClickListener l){
		title_middle.setOnClickListener(l);
	}
	
	public void setTitleRightClickLis(OnClickListener l){
		title_right.setOnClickListener(l);
	}

}
