package com.huntkey.software.sceo.ui.activity.photoview;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.widget.SmoothImageView;

/**
 * 头像点击放大
 * @author chenliang3
 *
 */
public class SpaceImageDetailActivity extends BaseActivity{

	private String imageUrl;
	private int locationX;
	private int locationY;
	private int width;
	private int height;
	private SmoothImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		imageUrl = getIntent().getStringExtra("imageUrl");
		locationX = getIntent().getIntExtra("locationX", 0);
		locationY = getIntent().getIntExtra("locationY", 0);
		width = getIntent().getIntExtra("width", 0);
		height = getIntent().getIntExtra("height", 0);
		
		imageView = new SmoothImageView(SpaceImageDetailActivity.this);
		imageView.setOriginalInfo(width, height, locationX, locationY);
		imageView.transformIn();
		imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		imageView.setScaleType(ScaleType.FIT_CENTER);
		
		setContentView(imageView);
		Glide
				.with(SpaceImageDetailActivity.this)
				.load(imageUrl)
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(imageView);
		
		imageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
						
						@Override
						public void onTransformComplete(int mode) {
							if (mode == 2) {
								finish();
							}
						}
					});
				imageView.transformOut();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		 imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
			
			@Override
			public void onTransformComplete(int mode) {
				if (mode == 2) {
					finish();
				}
			}
		});
		imageView.transformOut();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (isFinishing()) {
			overridePendingTransition(0, 0);
		}
	}
	
}
