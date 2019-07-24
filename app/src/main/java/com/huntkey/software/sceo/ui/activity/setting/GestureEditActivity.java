package com.huntkey.software.sceo.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.gesturelock.GestureContentView;
import com.huntkey.software.sceo.widget.gesturelock.GestureDrawline.GestureCallBack;
import com.huntkey.software.sceo.widget.gesturelock.LockIndicator;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import es.dmoral.toasty.Toasty;

/**
 * 手势密码设置界面
 * @author chenliang3
 *
 */
public class GestureEditActivity extends BaseActivity {

	@ViewInject(R.id.gesture_edit_title)
	BackTitle title;
	@ViewInject(R.id.gesture_edit_lock_indicator)
	LockIndicator lockIndicator;
	@ViewInject(R.id.gesture_edit_text_tip)
	TextView textTip;
	@ViewInject(R.id.gesture_container)
	FrameLayout gestureContainer;
	@ViewInject(R.id.gesture_text_reset)
	TextView textReset;
	
	private GestureContentView gestureContentView;
	private boolean isFirstInput = true;
	private String firstPassword = null;
	private String confirmPassword = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_edit);
		ViewUtils.inject(this);
		
		initTitle();
		initView();
	}

	private void initView() {
		textReset.setClickable(false);
		//初始化一个显示各个点的viewGroup
		gestureContentView = new GestureContentView(this, false, "", new GestureCallBack() {
			
			@Override
			public void onGestureCodeInput(String inputCode) {
				if (!isInputPassValidate(inputCode)) {
					textTip.setText(Html.fromHtml("<font color='#FF3F3E'>最少链接4个点, 请重新输入</font>"));
					gestureContentView.clearDrawlineState(0L);
					return;
				}
				if (isFirstInput) {
					firstPassword = inputCode;
					updateCodeList(inputCode);
					gestureContentView.clearDrawlineState(0L);
					textReset.setClickable(true);
					textReset.setText("重新设置手势密码");
					textTip.setText("再次绘制解锁图案");
				}else {
					if (inputCode.equals(firstPassword)) {
						Toasty.success(GestureEditActivity.this, "设置成功", Toast.LENGTH_SHORT, true).show();
						gestureContentView.clearDrawlineState(0L);
						
						SceoUtil.setGesturePassword(GestureEditActivity.this, inputCode);
						Intent intent = new Intent();
						intent.putExtra("gestureState", true);
						setResult(RESULT_OK, intent);
						
						GestureEditActivity.this.finish();
					}else {
						textTip.setText(Html.fromHtml("<font color='#FF3F3E'>与上一次绘制不一致，请重新绘制</font>"));
						//左右移动动画
						Animation shakeAnimation = AnimationUtils.loadAnimation(GestureEditActivity.this, R.anim.shake);
						textTip.startAnimation(shakeAnimation);
						//保持绘制的线，1.5秒后清除
						gestureContentView.clearDrawlineState(1300L);
					}
				}
				isFirstInput = false;
			}
			
			@Override
			public void checkedSuccess() {
				
			}
			
			@Override
			public void checkedFailed() {
				
			}
		});
		//设置手势解锁显示到哪个布局里面
		gestureContentView.setParentView(gestureContainer);
		updateCodeList("");
		
		textReset.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isFirstInput = true;
				updateCodeList("");
				textTip.setText("绘制解锁图案");
			}
		});
	}
	
	private void updateCodeList(String inputCode){
		//更新选择的图案
		lockIndicator.setPath(inputCode);
	}
	
	private boolean isInputPassValidate(String inputPassword){
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
	}

	private void initTitle() {
		title.setBackTitle("设置手势密码");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
