package com.huntkey.software.sceo.widget;

import com.huntkey.software.sceo.R;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

/**
 * Created by chenl
 */
public class LoginClearEditText extends EditText implements OnFocusChangeListener, TextWatcher{

	private Drawable mClearDrawable;//删除按钮
	
	public LoginClearEditText(Context context) {
		this(context, null);
	}
	
	public LoginClearEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}
	
	public LoginClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			mClearDrawable = getResources()
					.getDrawable(R.drawable.ic_login_clear);
		}
		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
		setClearIconVisible(false);
		setOnFocusChangeListener(this);
		addTextChangedListener(this);
	}
	
	/**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向没有考虑
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getCompoundDrawables()[2] != null) {
			if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() 
                        - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) 
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) { 
                    this.setText(""); 
                } 
            }
		}
		
		return super.onTouchEvent(event);
	}

	/**
	 * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		}else {
			setClearIconVisible(false);
		}
	}
	/**
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible){
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0], 
				getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}
	
	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

    @Override 
    public void onTextChanged(CharSequence s, int start, int count, 
            int after) { 
        setClearIconVisible(s.length() > 0);
    }

	@Override
	public void afterTextChanged(Editable arg0) {
		
	}
	
	
}
