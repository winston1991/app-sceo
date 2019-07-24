package com.huntkey.software.sceo.ui.activity.chat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.widget.crop.CropImageView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CorpActivity extends BaseActivity {

	@ViewInject(R.id.cropImageView)
	CropImageView mCropView;
	@ViewInject(R.id.buttonRotateImage)
	Button rotateBtn;
	@ViewInject(R.id.buttonDone)
	Button doneBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_corp);
		ViewUtils.inject(this);
		
		initView();
		mCropView.setImageBitmap(((SceoApplication) getApplication()).cropped);
	}

	private void initView() {
		//自定义充满宽度 - 比例7：5
        mCropView.setInitialFrameScale(1.0f);
        mCropView.setCustomRatio(7, 5);
        mCropView.setCropMode(CropImageView.CropMode.RATIO_FREE);
        
        rotateBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCropView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
			}
		});
        doneBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				((SceoApplication) getApplication()).cropped = mCropView.getCroppedBitmap();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                CorpActivity.this.finish();
			}
		});
	}
	
}
