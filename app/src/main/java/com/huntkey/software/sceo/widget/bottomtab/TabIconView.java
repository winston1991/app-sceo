package com.huntkey.software.sceo.widget.bottomtab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.ScreenUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by chenl on 2017/3/30.
 */

public class TabIconView extends ImageView {

    private Paint mPaint;
    private Bitmap mSelectedIcon;
    private Bitmap mNormalIcon;
    private Rect mSelectedRect;
    private Rect mNormalRect;
    private int mSelectedAlpha = 0;

    public TabIconView(Context context) {
        super(context);
    }

    public TabIconView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabIconView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

//    public final void init(int normal, int selected) {
//        this.mNormalIcon = createBitmap(normal);
//        this.mSelectedIcon = createBitmap(selected);
//        this.mNormalRect = new Rect(0, 0, this.mNormalIcon.getWidth(), this.mNormalIcon.getHeight());
//        this.mSelectedRect = new Rect(0, 0, this.mSelectedIcon.getWidth(),
//                this.mSelectedIcon.getHeight());
//        this.mPaint = new Paint(1);
//    }
    private int tmp;
    public final void init(String iconUrl) {
        if (ScreenUtil.getScreenWidthPix(getContext()) < 600){
            tmp = 2;
        }else {
            tmp = 1;
        }
        Glide.with(getContext())
                .load(iconUrl)
                .asBitmap()
                .placeholder(R.drawable.ic_icon_placeholder)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        mSelectedIcon = resource;
                        mNormalIcon = grey(resource);
                        mNormalRect = new Rect(0, 0, mNormalIcon.getWidth()/tmp,
                                mNormalIcon.getHeight()/tmp);
                        mSelectedRect = new Rect(0, 0, mSelectedIcon.getWidth()/tmp,
                                mSelectedIcon.getHeight()/tmp);
                        mPaint = new Paint(1);
                    }
                });
    }

    /**
     * bitmap 置灰
     * @param bitmap
     * @return
     */
    public static final Bitmap grey(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap faceIconGreyBitmap = Bitmap
                .createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(faceIconGreyBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
                colorMatrix);
        paint.setColorFilter(colorMatrixFilter);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return faceIconGreyBitmap;
    }

//    private Bitmap createBitmap(int resId) {
//        return BitmapFactory.decodeResource(getResources(), resId);
//    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mPaint == null) {
            return;
        }
        this.mPaint.setAlpha(255 - this.mSelectedAlpha);
        canvas.drawBitmap(this.mNormalIcon, null, this.mNormalRect, this.mPaint);
        this.mPaint.setAlpha(this.mSelectedAlpha);
        canvas.drawBitmap(this.mSelectedIcon, null, this.mSelectedRect, this.mPaint);
    }

    public final void changeSelectedAlpha(int alpha) {
        this.mSelectedAlpha = alpha;
        invalidate();
    }

    public final void transformPage(float offset) {
        changeSelectedAlpha((int) (255 * (1 - offset)));
    }
}
