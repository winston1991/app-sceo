package com.huntkey.software.sceo.ui.activity.chat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.CommonTitle;
import com.huntkey.software.sceo.widget.wheelview.NumericWheelAdapter;
import com.huntkey.software.sceo.widget.wheelview.WheelView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
/**
 * 待办事务刷新频率设置
 * @author chenliang3
 *
 */
public class ChatTimeSettingActivity extends BaseActivity {

	@ViewInject(R.id.chat_time_setting_title)
	CommonTitle title;
	@ViewInject(R.id.wheel_hour)
	WheelView hourView;
	@ViewInject(R.id.wheel_min)
	WheelView minView;
	@ViewInject(R.id.wheel_sec)
	WheelView secView;
	private int hour;
	private int min;
	private int sec;
	private String flag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat_time_setting);
		ViewUtils.inject(this);
		
		int time = getIntent().getIntExtra("time", 6);
		flag = getIntent().getStringExtra("flag");
		hour = time/3600;
		min = (time-hour*3600)/60;
		sec = (time-hour*3600-min*60);
		
		initView();
	}

	private void initView() {
		if ("chatSetting".equals(flag)) {			
			title.setMiddleTitle("刷新频率");
		}else if ("meFragment".equals(flag)) {
			title.setMiddleTitle("超时退出");
		}
		title.setTitleLeftClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChatTimeSettingActivity.this.finish();
			}
		});
		title.setTitleRightClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int time = getTime();
				if ("chatSetting".equals(flag)) {	
					if (time < 6) {
						time = 6;
					}
					SceoUtil.setInterval(ChatTimeSettingActivity.this, time*1000);
				}else if ("meFragment".equals(flag)) {
					SceoUtil.setOutTimeExit(ChatTimeSettingActivity.this, time*1000);
				}
				finish();
			}
		});
		
		hourView.setViewAdapter(new NumericWheelAdapter(ChatTimeSettingActivity.this, 0, 23));
		hourView.setCyclic(true);
		hourView.setVisibleItems(7);
		hourView.setCurrentItem(hour);
		
		minView.setViewAdapter(new NumericWheelAdapter(ChatTimeSettingActivity.this, 0, 59));
		minView.setCyclic(true);
		minView.setVisibleItems(7);
		minView.setCurrentItem(min);
		
		secView.setViewAdapter(new NumericWheelAdapter(ChatTimeSettingActivity.this, 0, 59));
		secView.setCyclic(true);
		secView.setVisibleItems(7);
		secView.setCurrentItem(sec);
		
	}
	
	private int getTime(){
		int tmpTime = hourView.getCurrentItem()*3600 + minView.getCurrentItem()*60 + 
				secView.getCurrentItem();
		return tmpTime;
	}
	
}
