package com.huntkey.software.sceo.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.SwitchButton;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class GestureSetActivity extends BaseActivity {

	@ViewInject(R.id.gesture_set_title)
	BackTitle title;
	@ViewInject(R.id.gesture_set_switchBtn)
	SwitchButton switchBtn;
	@ViewInject(R.id.gesture_set_modifBtn)
	Button modifBtn;
	
	private boolean gestureStatus;
	private String password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_set);
		ViewUtils.inject(this);
		
		password = SceoUtil.getGesturePassword(GestureSetActivity.this);
		
		initTitle();
		initView();
	}

	private void initView() {
		if (SceoUtil.getGesturePassword(GestureSetActivity.this) != null && !"".equals(SceoUtil.getGesturePassword(GestureSetActivity.this))) {
			switchBtn.setChecked(true);
			modifBtn.setVisibility(View.VISIBLE);
			gestureStatus = true;
		}else {
			switchBtn.setChecked(false);
			modifBtn.setVisibility(View.GONE);
			gestureStatus = false;
		}
		
		switchBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					gestureStatus = true;
					SceoUtil.setGesturePassword(GestureSetActivity.this, password);
				}else {
					SceoUtil.setGesturePassword(GestureSetActivity.this, "");
					gestureStatus = false;
				}
			}
		});
		
		modifBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GestureSetActivity.this, GestureEditActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private void initTitle() {
		title.setBackTitle("修改手势密码");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("gestureStatus", gestureStatus);
				setResult(0x11, intent);
				
				finish();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			Intent intent = new Intent();
			intent.putExtra("gestureStatus", gestureStatus);
			setResult(0x11, intent);
			
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
}
