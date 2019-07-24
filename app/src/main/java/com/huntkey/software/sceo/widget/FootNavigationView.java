package com.huntkey.software.sceo.widget;

/**
 * 底部导航栏
 * Created by chenl
 */

import com.huntkey.software.sceo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FootNavigationView extends LinearLayout {

	private RelativeLayout navi_books;
	private RelativeLayout navi_affairs;
	private RelativeLayout navi_form;
	private RelativeLayout navi_me;
	private ImageView navi_books_iv;
	private ImageView navi_affairs_iv;
	private ImageView navi_form_iv;
	private ImageView navi_me_iv;
	private TextView navi_books_tv;
	private TextView navi_affairs_tv;
	private TextView navi_form_tv;
	private TextView navi_me_tv;
	private TextView navi_affairs_remind;
	private ImageView navi_me_remind;
	private TextView navi_books_remind;

	public FootNavigationView(Context context) {
		super(context);
	}
	
	public FootNavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_foot_navigation, this);
		
		navi_books = (RelativeLayout) findViewById(R.id.navi_books);
		navi_affairs = (RelativeLayout) findViewById(R.id.navi_affairs);
		navi_form = (RelativeLayout) findViewById(R.id.navi_form);
		navi_me = (RelativeLayout) findViewById(R.id.navi_me);
		
		navi_books_iv = (ImageView) findViewById(R.id.navi_books_iv);
		navi_affairs_iv = (ImageView) findViewById(R.id.navi_affairs_iv);
		navi_form_iv = (ImageView) findViewById(R.id.navi_form_iv);
		navi_me_iv = (ImageView) findViewById(R.id.navi_me_iv);
		
		navi_books_tv = (TextView) findViewById(R.id.navi_books_tv);
		navi_affairs_tv = (TextView) findViewById(R.id.navi_affairs_tv);
		navi_form_tv = (TextView) findViewById(R.id.navi_form_tv);
		navi_me_tv = (TextView) findViewById(R.id.navi_me_tv);
		
		navi_affairs_remind = (TextView) findViewById(R.id.navi_affairs_remind);
		navi_books_remind = (TextView) findViewById(R.id.navi_books_remind);
		navi_me_remind = (ImageView) findViewById(R.id.navi_me_remind);
		
		initAttrs(context, attrs);
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FootNavigationView);
		int resourceId = -1;
		
		//books
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_books_bg, 0);
		if (resourceId > 0) {
			navi_books_iv.setBackgroundResource(resourceId);
		}
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_books_src, 0);
		if (resourceId > 0) {
			navi_books_iv.setImageResource(resourceId);
		}
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_books_text_color, R.color.text_color_weak);
		navi_books_tv.setTextColor(resourceId);
		
		//affairs
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_affairs_bg, 0);
		if (resourceId > 0) {
			navi_affairs_iv.setBackgroundResource(resourceId);
		}
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_affairs_src, 0);
		if (resourceId > 0) {
			navi_affairs_iv.setImageResource(resourceId);
		}
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_affairs_text_color, R.color.text_color_weak);
		navi_affairs_tv.setTextColor(resourceId);
		
		//linkman
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_linkman_bg, 0);
		if (resourceId > 0) {
			navi_form_iv.setBackgroundResource(resourceId);
		}
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_linkman_src, 0);
		if (resourceId > 0) {
			navi_form_iv.setImageResource(resourceId);
		}
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_linkman_text_color, R.color.text_color_weak);
		navi_form_tv.setTextColor(resourceId);
		
		//me
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_me_bg, 0);
		if (resourceId > 0) {
			navi_me_iv.setBackgroundResource(resourceId);
		}
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_me_src, 0);
		if (resourceId > 0) {
			navi_me_iv.setImageResource(resourceId);
		}
		resourceId = typedArray.getResourceId(R.styleable.FootNavigationView_me_text_color, R.color.text_color_weak);
		navi_me_tv.setTextColor(resourceId);
		
		typedArray.recycle();
	}

	//address_book
	public void setBooksSrc(int resId){
		navi_books_iv.setImageResource(resId);
	}
	public void setBooksTxtColor(int color){
		navi_books_tv.setTextColor(color);
	}
	public void setBooksClickLis(OnClickListener l){
		navi_books.setOnClickListener(l);
	}
	
	//affairs
	public void setAffairsSrc(int resId){
		navi_affairs_iv.setImageResource(resId);
	}
	public void setAffairsTxtColor(int color){
		navi_affairs_tv.setTextColor(color);
	}
	public void setAffairsClickLis(OnClickListener l){
		navi_affairs.setOnClickListener(l);
	}
	
	//linkman
	public void setFormSrc(int resId){
		navi_form_iv.setImageResource(resId);
	}
	public void setFormTxtColor(int color){
		navi_form_tv.setTextColor(color);
	}
	public void setFormTxt(CharSequence title) {
		navi_form_tv.setText(title);
	}
	public void setFormClickLis(OnClickListener l){
		navi_form.setOnClickListener(l);
	}
	
	//me
	public void setMeSrc(int resId){
		navi_me_iv.setImageResource(resId);
	}
	public void setMeTxtColor(int color){
		navi_me_tv.setTextColor(color);
	}
	public void setMeClickLis(OnClickListener l){
		navi_me.setOnClickListener(l);
	}
	
	/**
	 * 设置默认点击项
	 */
	public void setPerformClick(int flag){
		if (flag == 0) {			
			navi_books.performClick();
		}else if (flag == 2) {
			navi_affairs.performClick();
		}
	}
	
	/**
	 * 待办事务的remindText
	 */
	public void setAffairsRemindTxtVisible(){
		navi_affairs_remind.setVisibility(View.VISIBLE);
	}
	public void setAffairsRemindTxtGone(){
		navi_affairs_remind.setVisibility(View.GONE);
	}
	public void setAffairsRemindText(String s){
		navi_affairs_remind.setText(s);
	}
	public void setAffairsRemindBackground(String s){
		if (s != null && !"".equals(s)) {
			int i = Integer.parseInt(s);
			if (i > 0 && i < 10) {
				navi_affairs_remind.setBackgroundResource(R.drawable.ic_remind_affairs);
			}else if (i > 9 && i < 100) {
				navi_affairs_remind.setBackgroundResource(R.drawable.ic_remind_2);
			}else {
				navi_affairs_remind.setBackgroundResource(R.drawable.ic_remind_3);
			}
		}
	}
	
	/**
	 * 待审单据的remindText
	 */
	public void setBookRemindTxtVisible(){
		navi_books_remind.setVisibility(View.VISIBLE);
	}
	public void setBookRemindTxtGone(){
		navi_books_remind.setVisibility(View.GONE);
	}
	public void setBookRemindText(String s){
		navi_books_remind.setText(s);
	}
	public void setBookRemindBackground(String s){
		if (s != null && !"".equals(s)) {
			int i = Integer.parseInt(s);
			if (i > 0 && i < 10) {
				navi_books_remind.setBackgroundResource(R.drawable.ic_remind_affairs);
			}else if (i > 9 && i < 100) {
				navi_books_remind.setBackgroundResource(R.drawable.ic_remind_2);
			}else {
				navi_books_remind.setBackgroundResource(R.drawable.ic_remind_3);
			}
		}
	}
	
	/**
	 * 我的remindImage
	 */
	public void setMeRemindImgVisible(){
		navi_me_remind.setVisibility(View.VISIBLE);
	}
	public void setMeRemindImgGone(){
		navi_me_remind.setVisibility(View.GONE);
	}
	
	
}
