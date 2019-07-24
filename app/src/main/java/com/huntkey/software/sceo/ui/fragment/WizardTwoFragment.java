package com.huntkey.software.sceo.ui.fragment;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.ui.activity.login.LoginActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nineoldandroids.animation.Keyframe;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.PropertyValuesHolder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 引导界面2
 * @author chenliang3
 *
 */
public class WizardTwoFragment extends Fragment {

	@ViewInject(R.id.wizard_two_tv)
	TextView textView;
	@ViewInject(R.id.wizard_two_iv)
	ImageView imageView;
	@ViewInject(R.id.wizard_two_iv1)
	ImageView imageView1;
	@ViewInject(R.id.wizard_two_iv2)
	ImageView imageView2;
	@ViewInject(R.id.wizard_two_iv3)
	ImageView imageView3;
	@ViewInject(R.id.wizard_two_iv4)
	ImageView imageView4;
	@ViewInject(R.id.wizard_two_iv5)
	ImageView imageView5;
	@ViewInject(R.id.wizard_two_btn)
	Button button;
	
	private AlphaAnimation alphaAnimation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wizard_two, null);
		ViewUtils.inject(this, view);
		
//		init();
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			init();
		}
		
		super.setUserVisibleHint(isVisibleToUser);
	}
	
	private void init() {
		startAlphaAnimation(textView);
//		startAlphaAnimation(imageView);
//		Animation anima = AnimationUtils.loadAnimation(getActivity(), R.anim.push_up_in);
//		imageView.startAnimation(anima);
		testKeyFrames(imageView);
		
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.small_2_big);
		imageView1.startAnimation(animation);
		imageView2.startAnimation(animation);
		imageView3.startAnimation(animation);
		imageView4.startAnimation(animation);
		imageView5.startAnimation(animation);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), LoginActivity.class);
				startActivity(intent);
				cancelAlphaAnimation();
				getActivity().finish();
			}
		});
	}
	
	/**
	 * 帧动画
	 * @param view
	 */
	private void testKeyFrames(View view){
		float h = view.getHeight();
		float w = view.getWidth();
		float x = view.getX();
		float y = view.getY();
		
		Keyframe kf0 = Keyframe.ofFloat(0.2f, 120);
		Keyframe kf1 = Keyframe.ofFloat(0.5f, 10);
		Keyframe kf2 = Keyframe.ofFloat(0.8f, 360);
		Keyframe kf3 = Keyframe.ofFloat(1f, 0);
		
		PropertyValuesHolder pvhRotation = PropertyValuesHolder.ofKeyframe(
				"rotation", kf0, kf1, kf2, kf3);
		
		PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("x", w, x);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("y", h, y);
		
		ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view,
				pvhRotation, pvhX, pvhY);
		anim.setDuration(1000);
		anim.start();
	}

	private void startAlphaAnimation(View view){
		if (alphaAnimation == null) {
			// 创建一个AlphaAnimation对象
			alphaAnimation = new AlphaAnimation(0.01f, 1f);
			// 设置动画执行的时间（单位：毫秒） 
			alphaAnimation.setDuration(2000);
			// 设置重复次数 
//			alphaAnimation.setRepeatCount(5);
		}
		view.setAnimation(alphaAnimation);
		alphaAnimation.start();
	}
	
	private void cancelAlphaAnimation(){
		if (alphaAnimation != null) {
			alphaAnimation.cancel();
		}
	}
	
}
