package com.huntkey.software.sceo.widget.gesturelock;

import com.huntkey.software.sceo.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
/**
 * 手势密码图案提示
 * @author chenliang3
 *
 */
public class LockIndicator extends View {

	private int numRow = 3;//行
	private int numColum = 3;//列
	private int patternWidth = 40;
	private int patternHeight = 40;
	private int f = 5;
	private int g = 5;
	private int strokeWidth = 3;
	private Paint paint = null;
	private Drawable patternNormal = null;
	private Drawable patternPressed = null;
	private String lockPassStr;//手势密码
	
	public LockIndicator(Context context) {
		super(context);
	}
	
	public LockIndicator(Context context, AttributeSet attrs) {
		super(context, attrs, 0);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(strokeWidth);
		paint.setStyle(Paint.Style.STROKE);
		patternNormal = getResources().getDrawable(R.drawable.lock_pattern_node_normal);
		patternPressed = getResources().getDrawable(R.drawable.lock_pattern_node_pressed);
		if (patternPressed != null) {
			patternWidth = patternPressed.getIntrinsicWidth();
			patternHeight = patternPressed.getIntrinsicHeight();
			this.f = patternWidth / 4;
			this.g = patternHeight / 4;
			patternPressed.setBounds(0, 0, patternWidth, patternHeight);
			patternNormal.setBounds(0, 0, patternWidth, patternHeight);
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if ((patternPressed == null) || (patternNormal == null)) {
			return;
		}
		//绘制3*3的图标
		for (int i = 0; i < numRow; i++) {
			for (int j = 0; j < numColum; j++) {
				paint.setColor(-16777216);
				int i1 = j * patternHeight + j * this.g;
				int i2 = i * patternWidth + i * this.f;
				canvas.save();
				canvas.translate(i1, i2);
				String curNum = String.valueOf(numColum * i + (j + 1));
				if (!TextUtils.isEmpty(lockPassStr)) {
					if (lockPassStr.indexOf(curNum) == -1) {
						//未选中
						patternNormal.draw(canvas);
					}else {
						//被选中
						patternPressed.draw(canvas);
					}
				}else {
					//重置状态
					patternNormal.draw(canvas);
				}
				canvas.restore();
			}
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		if (patternPressed != null) {
			setMeasuredDimension(numColum * patternHeight + this.g
					* (-1 + numColum), numRow * patternWidth + this.f
					* (-1 + numRow));
		}
	}
	
	/**
	 * 请求重新绘制
	 */
	public void setPath(String paramString){
		lockPassStr = paramString;
		invalidate();
	}

}
