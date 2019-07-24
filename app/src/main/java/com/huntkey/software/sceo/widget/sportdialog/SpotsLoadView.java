package com.huntkey.software.sceo.widget.sportdialog;

import com.huntkey.software.sceo.R;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SpotsLoadView extends RelativeLayout {

	private static final int DELAY = 150;
    private static final int DURATION = 1500;

    private int size;
    private AnimatedView[] spots;
    private AnimatorPlayer animator;
    
	private TextView title;
	private ProgressLayout progress;
	private RelativeLayout layout;
	
	public SpotsLoadView(Context context) {
		this(context, null);
	}
	
	public SpotsLoadView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.sd_style);
	}
	
	public SpotsLoadView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs, defStyle, 0);
	}
	
	public SpotsLoadView(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
		super(context, attrs);
		init(attrs, defStyle, defStyleRes);
	}
	
	private void init(AttributeSet attrs, int defStyle, int defStyleRes){
		this.setClickable(false);
		TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.Dialog, defStyle, defStyleRes);
		
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_spots_load, this, true);
		
		layout = (RelativeLayout) findViewById(R.id.spots_layout);
		title = (TextView) findViewById(R.id.spots_load_title);
		progress = (ProgressLayout) findViewById(R.id.spots_load_progress);
		size = progress.getSpotsCount();
		
		String titleStr;
		int titleColor;
		boolean isShowTitle;
		int spotCount;
		int backgroundColor;
		try {
			titleStr = typedArray.getString(R.styleable.Dialog_DialogTitleText);
			titleColor = typedArray.getResourceId(R.styleable.Dialog_DialogTitleColor, getResources().getColor(R.color.text_color_normal));
			isShowTitle = typedArray.getBoolean(R.styleable.Dialog_isShowDialogTitle, true);
			spotCount = typedArray.getInt(R.styleable.Dialog_DialogSpotCount, 5);
			backgroundColor = typedArray.getResourceId(R.styleable.Dialog_DialogBackgroundColor, getResources().getColor(R.color.white));
			
			if (titleStr != null) {
				title.setText(titleStr);
			}
			if (!isShowTitle) {
				title.setVisibility(View.GONE);
			}
			title.setTextColor(titleColor);
			progress.setSpotsCount(spotCount);
			layout.setBackgroundColor(backgroundColor);
		} finally {
			typedArray.recycle();
		}
		
		spots = new AnimatedView[size];
        int size = getContext().getResources().getDimensionPixelSize(R.dimen.margin_5);
        int progressWidth = getContext().getResources().getDimensionPixelSize(R.dimen.margin_280);
        for (int i = 0; i < spots.length; i++) {
            AnimatedView v = new AnimatedView(getContext());
            v.setBackgroundResource(R.drawable.spot);
            v.setTarget(progressWidth);
            v.setXFactor(-1f);
            progress.addView(v, size, size);
            spots[i] = v;
        }
        
        animator = new AnimatorPlayer(createAnimations());
        animator.play();
	}
	
	public void setSpotsVisibility(int visibility) {
		if (visibility != View.VISIBLE && animator != null) {
			animator.stop();
		}else if (visibility == View.VISIBLE && animator != null) {
			animator.play();
		}
		this.setVisibility(visibility);
	}
	
    private Animator[] createAnimations() {
        Animator[] animators = new Animator[size];
        for (int i = 0; i < spots.length; i++) {
            Animator move = ObjectAnimator.ofFloat(spots[i], "xFactor", 0, 1);
            move.setDuration(DURATION);
            move.setInterpolator(new HesitateInterpolator());
            move.setStartDelay(DELAY * i);
            animators[i] = move;
        }
        return animators;
    }
    
}
