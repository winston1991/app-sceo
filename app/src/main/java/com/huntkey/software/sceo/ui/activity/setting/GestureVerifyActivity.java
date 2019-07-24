package com.huntkey.software.sceo.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.others.ClickLogTask;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.login.LoginActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.gesturelock.GestureContentView;
import com.huntkey.software.sceo.widget.gesturelock.GestureDrawline.GestureCallBack;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog.OnSweetClickListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import es.dmoral.toasty.Toasty;

/**
 * 手势绘制/校验
 * @author chenliang3
 *
 */
public class GestureVerifyActivity extends BaseActivity {

	@ViewInject(R.id.gesture_verify_empcode)
	TextView empCode;
	@ViewInject(R.id.gesture_verify_tip)
	TextView textTip;
	@ViewInject(R.id.gesture_verify_warn)
	TextView textWarn;
	@ViewInject(R.id.gesture_verify_container)
	FrameLayout gestureContainer;
	@ViewInject(R.id.gesture_verify_forget)
	TextView textForget;
	
	private GestureContentView gestureContentView;
	private String password;
	private int count = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSystemBarTint(false);
		setContentView(R.layout.activity_gesture_verify);
		ViewUtils.inject(this);
		
		password = SceoUtil.getGesturePassword(GestureVerifyActivity.this);
		
		initView();
	}

	private void initView() {
		empCode.setText(SceoUtil.getEmpName(GestureVerifyActivity.this));
		
		//初始化一个显示各个点得viewGroup
		gestureContentView = new GestureContentView(this, true, password, new GestureCallBack() {
			
			@Override
			public void onGestureCodeInput(String inputCode) {
				
			}
			
			@Override
			public void checkedSuccess() {
				gestureContentView.clearDrawlineState(0L);
//				Toast.makeText(GestureVerifyActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
				finish();
			}
			
			@Override
			public void checkedFailed() {
				count--;
				
				if (count > 0) {					
					gestureContentView.clearDrawlineState(1300L);
					textTip.setText(Html.fromHtml("<font color='#FF3F3E'>密码错误</font>"));
					//左右移动动画
					Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
					textTip.startAnimation(shakeAnimation);
					
					textWarn.setVisibility(View.VISIBLE);
					textWarn.setText(count + "次失败后需要重新登录");
				}else {
					Toasty.error(GestureVerifyActivity.this, "手势错误请重新登录", Toast.LENGTH_SHORT, true).show();
					Intent intent = new Intent(GestureVerifyActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}
			}
		});
		//设置手势解锁显示到哪个布局里面
		gestureContentView.setParentView(gestureContainer);
		
		textForget.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SceoUtil.setGesturePassword(GestureVerifyActivity.this, "");
				SceoApplication.getInstance().exit();
				Intent intent = new Intent(GestureVerifyActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			new SweetAlertDialog(GestureVerifyActivity.this, SweetAlertDialog.WARNING_TYPE)
				.setTitleText("确认退出？")
				.setCancelText("取消")
				.setConfirmText("确定")
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
						ClickLogTask clickLogTask5 = new ClickLogTask();
						clickLogTask5.execute(GestureVerifyActivity.this, "退出系统", "", 3, 1);
						
						sweetAlertDialog.dismiss();
						finish();
					}
				})
				.show();
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
