package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.StorageLocationItem;

import java.util.List;

/**
 * Created by chenl on 2017/4/27.
 */

public class StorageLocationAdapter extends BaseQuickAdapter<StorageLocationItem, BaseViewHolder> {

    public StorageLocationAdapter(int layoutResId, List<StorageLocationItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StorageLocationItem item) {
        helper.setText(R.id.item_sc_part, item.getTskp_ref());
        helper.setText(R.id.item_sc_num, item.getPick_count() + "(笔)");
        if("1".equals(item.getIsok())) {//已检
            helper.setBackgroundRes(R.id.item_sc_layout, R.color.success_stroke_color);
        }else {//待检
            helper.setBackgroundRes(R.id.item_sc_layout, R.color.error_stroke_color);
        }
    }
}
