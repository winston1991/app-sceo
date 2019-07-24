package com.huntkey.software.sceo.widget;

import com.huntkey.software.sceo.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 即时快报的title
 *
 * @author chenliang3
 */
public class MainTitle_v2 extends RelativeLayout {

    private LinearLayout back;
    private TextView main_title;
    private TextView main_time;
    private RelativeLayout main_setting;
    private TextView main_depart;

    private Activity activity;

    public MainTitle_v2(Context context) {
        super(context);
    }

    public MainTitle_v2(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_main_title_v2, this);

        activity = (Activity) context;
        back = findViewById(R.id.back);
        main_title = findViewById(R.id.main_title_v2_txt);
        main_time = findViewById(R.id.main_title_v2_time);
        main_setting = findViewById(R.id.main_title_v2_setting);
        main_depart = findViewById(R.id.main_title_v2_depart);

        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });

//		initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MainTitle);
        int resourceId = -1;

        resourceId = typedArray.getResourceId(R.styleable.MainTitle_main_title_txt, getResources().getColor(R.color.white));
        main_title.setTextColor(resourceId);

        typedArray.recycle();
    }

    public void hideBack() {
        back.setVisibility(GONE);
    }

    public void setMainTitle(String title) {
        main_title.setText(title);
    }

    public void setMainTitleColor(int color) {
        main_title.setTextColor(color);
    }

    public void setTitleSettingClickLis(OnClickListener l) {
        main_setting.setOnClickListener(l);
    }

    public void setTitleSettingVisible(int visible) {
        main_setting.setVisibility(visible);
    }

    public void setTitleDepartClickLis(OnClickListener l) {
        main_depart.setOnClickListener(l);
    }

    public void setTitleTime(String time) {
        main_time.setText(time);
    }

    public void setTitleTimeColor(int color) {
        main_time.setTextColor(color);
    }

    public void setTitleDepartText(String department) {
        main_depart.setText(department);
    }

}
