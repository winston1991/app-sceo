package com.huntkey.software.sceo.others;

import java.util.Calendar;

import android.view.View;
import android.view.View.OnClickListener;
/**
 * 防止过快点击造成多次事件
 * @author chenliang3
 *
 */
public abstract class NoDoubleClickListener implements OnClickListener{

	private static final int MIN_CLICK_DELAY_TIME = 800;
	private long lastClickTime = 0;
	
	@Override
	public void onClick(View v) {
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
			lastClickTime = currentTime;
			onNoDoubleClick(v);
		}
	}
	
	public abstract void onNoDoubleClick(View view);

}
