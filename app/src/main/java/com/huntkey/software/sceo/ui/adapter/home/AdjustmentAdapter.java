package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.AdjustOrderItem;

import java.util.List;

/**
 * Created by chenl on 2017/5/8.
 */

public class AdjustmentAdapter extends BaseQuickAdapter<AdjustOrderItem, BaseViewHolder> {

    public AdjustmentAdapter(int layoutResId, List<AdjustOrderItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AdjustOrderItem item) {
        helper.setText(R.id.item_adm_sn, "条码：" + item.getLot_sn());
        helper.setText(R.id.item_adm_distribution, "源储位：" + item.getLd_ref());
        helper.setText(R.id.item_adm_deposit, "存放储位：" + item.getNewRef());
    }
}
