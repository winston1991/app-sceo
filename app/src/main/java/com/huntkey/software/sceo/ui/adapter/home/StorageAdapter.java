package com.huntkey.software.sceo.ui.adapter.home;

import android.graphics.Color;
import android.graphics.PointF;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.StorageItem;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

/**
 * Created by chenl on 2017/3/28.
 */

public class StorageAdapter extends BaseQuickAdapter<StorageItem, BaseViewHolder> {

    public StorageAdapter(int layoutResId, List<StorageItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StorageItem item) {
        helper.setText(R.id.item_storagehome_tv, item.getTitle());
//        helper.setImageResource(R.id.item_storagehome_iv, item.getImg());
        Glide.with(mContext)
                .load(item.getImgUrl())
                .crossFade()
                .centerCrop()
                .placeholder(R.drawable.ic_plane)
                .into((ImageView) helper.getView(R.id.item_storagehome_iv));
    }
}
