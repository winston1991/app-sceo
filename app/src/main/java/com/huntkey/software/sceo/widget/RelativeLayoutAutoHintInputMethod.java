package com.huntkey.software.sceo.widget;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 
 * @author chenliang3
 * 点击输入框以外其它区域自动隐藏软键盘
 * 只需将原布局中的根布局替换为该控件
 *
 */
public class RelativeLayoutAutoHintInputMethod extends RelativeLayout {

	public RelativeLayoutAutoHintInputMethod(Context context) {
		this(context, null);
	}
	
	public RelativeLayoutAutoHintInputMethod(Context context, 
			AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public RelativeLayoutAutoHintInputMethod(Context context,
			AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setFocusable (true);
        setFocusableInTouchMode (true);
	}
	
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		initFocusChangeListener (this);
	}
	
	/** 
	 * 初始化焦点变化监听
	 */
    private void initFocusChangeListener(ViewGroup v){
        for ( int i = 0 ; i < v.getChildCount () ; i++ ) {
            View view = v.getChildAt (i);
            view.setOnFocusChangeListener (mFocusChangeListener);
            if (view instanceof ViewGroup) {
                initFocusChangeListener ((ViewGroup) view);
            } else {
                ArrayList<View> TouchableViews = view.getTouchables ();
                for ( View child : TouchableViews ) {
                    child.setOnFocusChangeListener (mFocusChangeListener);
                }
            }
        }
    }
    
    /** 当前获得焦点的View */
    private View mCurrentFouceView    = null;

    private OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener () {

    	public void onFocusChange(View v,boolean hasFocus){
    		if (!hasFocus) mCurrentFouceView = null;
            else mCurrentFouceView = v;
        }
    };


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if (ev.getAction () == MotionEvent.ACTION_DOWN) {
            // View v = ((Activity) getContext ()).getCurrentFocus ();
            View v = mCurrentFouceView;
            if (isShouldHideInput (v, ev)) { // 如果虚拟键盘为弹出状态执行一下操作
                // 还需要判断当前点击的地方不为另外一个EditText，否则会出现虚拟键盘跳跃的错误
                boolean isNextEditText = false;
                View clickView = getViewAtViewGroup ((int) ev.getX (), (int) ev.getY ());
                if (clickView != null && (clickView instanceof EditText || clickView instanceof TextView || clickView instanceof ImageView)){
                	isNextEditText = true;
                }
                if (!isNextEditText) { // 一下点击的控件不是一个EditText
                    InputMethodManager imm = (InputMethodManager) getContext ().getSystemService (Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow (v.getWindowToken (), 0);
                        v.clearFocus ();
                    }
                }
            }
        }
        return super.dispatchTouchEvent (ev);
    }
    
    /** 判断虚拟键盘是否被弹出 */
    public boolean isShouldHideInput(View v,MotionEvent event){
        if (v != null && (v instanceof EditText)) {
            int[] fatherLeftTop = { 0, 0 }; // 父控件在屏幕中的绝对坐标
            getLocationOnScreen (fatherLeftTop);


            int[] chileLeftTop = { 0, 0 };
            v.getLocationOnScreen (chileLeftTop);
            // 子控件在屏幕中的绝对坐标需要减去父控件的绝对坐标才能匹配真正的坐标
            int left = chileLeftTop[0] - fatherLeftTop[0];
            int top = chileLeftTop[1] - fatherLeftTop[1];
            int right = left + v.getWidth ();
            int bottom = top + v.getHeight ();


            if (new RectF (left,top,right,bottom).contains (event.getX (), event.getY ())) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 根据坐标获取相对应的子控件 在重写ViewGroup使用
     * 
     * @param x坐标
     * @param y坐标
     * @return 目标View
     */
    public View getViewAtViewGroup(int x,int y){
        return findViewByXY (this, x, y);
    }
    
    private View findViewByXY(View view,int x,int y){
        View targetView = null;
        if (view instanceof ViewGroup) {
            ViewGroup v = (ViewGroup) view;
            for ( int i = 0 ; i < v.getChildCount () ; i++ ) {
                targetView = findViewByXY (v.getChildAt (i), x, y);
                if (targetView != null) {
                    break;
                }
            }
        } else {
            targetView = getTouchTarget (view, x, y);
        }
        return targetView;
    }
    
    private View getTouchTarget(View view,int x,int y){
        // 判断view是否可以聚焦
        ArrayList<View> TouchableViews = view.getTouchables ();
        for ( View child : TouchableViews ) {
            if (isTouchPointInView (child, x, y)) { return child; }
        }
        return null;
    }
    
    private boolean isTouchPointInView(View view,int x,int y){
        int[] fatherLeftTop = { 0, 0 }; // 父控件在屏幕中的绝对坐标
        getLocationOnScreen (fatherLeftTop);
        int[] location = new int[2];
        view.getLocationOnScreen (location);
        int left = location[0] - fatherLeftTop[0];
        int top = location[1] - fatherLeftTop[1];
        int right = left + view.getMeasuredWidth ();
        int bottom = top + view.getMeasuredHeight ();
        Rect R = new Rect (left,top,right,bottom);
        if (view.isClickable () && R.contains (x, y)) { return true; }
        return false;
    }

}
