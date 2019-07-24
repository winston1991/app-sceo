package com.huntkey.software.sceo.ui.fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.facebook.shimmer.ShimmerFrameLayout.MaskAngle;
import com.huntkey.software.sceo.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

public class WizardOneFragment extends Fragment {

	@ViewInject(R.id.wizard_one_tv)
	TextView textView;
	@ViewInject(R.id.wizard_one_iv)
	ImageView imageView;
	@ViewInject(R.id.wizard_one_shimmer)
	ShimmerFrameLayout shimmerFrameLayout;
	
	private AlphaAnimation alphaAnimation;
	private TranslateAnimation translateAnimation;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_wizard_one, null);
		ViewUtils.inject(this, view);
		
		init();
		
		return view;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		shimmerFrameLayout.useDefaults();
		shimmerFrameLayout.setAngle(MaskAngle.CW_180);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		shimmerFrameLayout.startShimmerAnimation();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		shimmerFrameLayout.stopShimmerAnimation();
	}
	
	private void init() {
		startAlphaAnimation(textView);
		Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.in_translate_top);
		imageView.startAnimation(animation);
	}
	
	private void startTranslateAnimation(View view, float fromXDelta, float toXDelta, float fromYDelta, float toYDelta){
		if (translateAnimation == null) {
			translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
			translateAnimation.setDuration(500);
		}
		view.setAnimation(translateAnimation);
		translateAnimation.start();
	}

	private void startAlphaAnimation(View view){
		if (alphaAnimation == null) {
			// 创建一个AlphaAnimation对象
			alphaAnimation = new AlphaAnimation(0.01f, 1f);
			// 设置动画执行的时间（单位：毫秒） 
			alphaAnimation.setDuration(1000);
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
