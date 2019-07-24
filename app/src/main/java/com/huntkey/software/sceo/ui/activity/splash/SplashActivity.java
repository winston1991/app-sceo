package com.huntkey.software.sceo.ui.activity.splash;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.login.LoginActivity;
import com.huntkey.software.sceo.utils.SceoUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
/**
 * 欢迎界面
 * @author chenliang3
 *
 */
public class SplashActivity extends BaseActivity {

	private RelativeLayout splash_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
//		setSystemBarTint(false);
		setContentView(R.layout.activity_splash);
		
		//点击home键再进入不重新启动程序
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {  
            finish();
            return;
		}
		
		//如果是第一次启动，则跳转到启动界面
		if (isFirstStart()) {
			Intent intent = new Intent(SplashActivity.this, WizardActivity.class);
			startActivity(intent);
			finish();
		}else {
			initView();
		}
	}
	
	/**
	 * 检查是否是第一次启动
	 */
	private boolean isFirstStart(){
		return !SceoUtil.shareGetBoolean(this, "isWizard");
	}

	protected void initView() {
		splash_layout = (RelativeLayout) findViewById(R.id.splash_layout);
		
		//渐变展示欢迎界面
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		splash_layout.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}
	
}
