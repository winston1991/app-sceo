package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.ReceiptAItem;

import java.util.List;

/**
 * Created by chenl on 2017/7/10.
 */

public class ReceiptBAdapter extends BaseQuickAdapter<ReceiptAItem, BaseViewHolder> {

    public ReceiptBAdapter(int layoutResId, List<ReceiptAItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ReceiptAItem item) {
        helper.setText(R.id.item_csk_tv, item.getLstm_nbr());
        helper.setChecked(R.id.item_csk_rb, item.isChoosed());
    }
}
