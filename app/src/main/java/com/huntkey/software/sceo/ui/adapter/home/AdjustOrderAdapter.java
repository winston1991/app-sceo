package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.AdjustOrderItem;

import java.util.List;

/**
 * Created by chenl on 2017/5/8.
 */

public class AdjustOrderAdapter extends BaseQuickAdapter<AdjustOrderItem, BaseViewHolder> {

    public AdjustOrderAdapter(int layoutResId, List<AdjustOrderItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AdjustOrderItem item) {
        helper.setText(R.id.item_ado_lot, "料号：" + item.getLd_part());
        helper.setText(R.id.item_ado_sn, "批次：" + item.getLd_lot());

        helper.addOnClickListener(R.id.item_ado_del);
    }
}
