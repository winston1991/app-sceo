package com.huntkey.software.sceo.ui.activity.splash;

import java.util.ArrayList;
import java.util.List;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.fragment.WizardOneFragment;
import com.huntkey.software.sceo.ui.fragment.WizardTwoFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

/**
 * 启动界面
 * @author chenliang3
 * 
 */
public class WizardActivity extends BaseActivity {

	@ViewInject(R.id.wizard_viewpager)
	ViewPager viewPager;
	
	private List<Fragment> fragmentList;
	private WizardOneFragment wizardOneFragment;
	private WizardTwoFragment wizardTwoFragment;
	private int currentTab = -1;//当前选中的项
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setSystemBarTint(false);
		setContentView(R.layout.activity_wizard);
		ViewUtils.inject(this);
		
		init();
		
	}

	private void init() {
		fragmentList = new ArrayList<>();
		
		wizardOneFragment = new WizardOneFragment();
		wizardTwoFragment = new WizardTwoFragment();
		fragmentList.add(wizardOneFragment);
		fragmentList.add(wizardTwoFragment);
		
		viewPager.setOffscreenPageLimit(1);//关闭预加载，默认一次只加载一个Fragment
		viewPager.setAdapter(new MyFragmentStatePagerAdapter(getSupportFragmentManager()));
	}
	
	private class MyFragmentStatePagerAdapter extends FragmentStatePagerAdapter{

		public MyFragmentStatePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentList.get(position);
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}
		
	}
	
}
