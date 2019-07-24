package com.huntkey.software.sceo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
/**
 * scrollview或者listview嵌套gridview显示不全解决方案
 * @author chenliang3
 *
 */
public class NewGridView extends GridView {

	public NewGridView(Context context) {
		super(context);
	}
	
	public NewGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public NewGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
