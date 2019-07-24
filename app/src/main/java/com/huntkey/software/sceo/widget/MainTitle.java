package com.huntkey.software.sceo.widget;

import com.huntkey.software.sceo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by chenl
 */
public class MainTitle extends RelativeLayout {

	private TextView main_title;
	private RelativeLayout title_publish;
	private RelativeLayout title_query;
	private ImageView title_publish_iv;
	private ImageView title_search_iv;

	public MainTitle(Context context) {
		super(context);
		
	}
	
	public MainTitle(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_main_title, this);
		
		main_title = (TextView) findViewById(R.id.main_title_txt);
		title_publish = (RelativeLayout) findViewById(R.id.main_title_publish);
		title_query = (RelativeLayout) findViewById(R.id.main_title_query);
		
		title_publish_iv = (ImageView) findViewById(R.id.main_title_publish_iv);
		title_search_iv = (ImageView) findViewById(R.id.main_title_search_iv);
		
//		initAttrs(context, attrs);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MainTitle);
		int resourceId = -1;
		
		resourceId = typedArray.getResourceId(R.styleable.MainTitle_main_title_txt, getResources().getColor(R.color.white));
		main_title.setTextColor(resourceId);
		
		typedArray.recycle();
	}
	
	public void setMainTitle(String title){
		main_title.setText(title);
	}
	public void setMainTitleColor(int color){
		main_title.setTextColor(color);
	}
	
	public void setTitlePublishVisible(){
		title_publish.setVisibility(View.VISIBLE);
		title_publish_iv.setVisibility(View.VISIBLE);
		title_search_iv.setVisibility(View.GONE);
	}
	
	public void setTitlePublishImage(Drawable drawable){
		title_publish_iv.setImageDrawable(drawable);
	}
	
	public void setTitleSearchVisible(){
		title_publish.setVisibility(View.VISIBLE);
		title_publish_iv.setVisibility(View.GONE);
		title_search_iv.setVisibility(View.VISIBLE);
	}
	
	public void setTitlePublishClickLis(OnClickListener l){
		title_publish.setOnClickListener(l);
	}
	
	public void setTitleQueryVisible(){
		title_query.setVisibility(View.VISIBLE);
	}
	
	public void setTitleQueryClickLis(OnClickListener l){
		title_query.setOnClickListener(l);
	}
	
	public View getQueryView(){
		return title_query;
	}

}
