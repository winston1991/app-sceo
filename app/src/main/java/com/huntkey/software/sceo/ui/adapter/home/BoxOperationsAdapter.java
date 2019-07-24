package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.BoxOperationsItem;

import java.util.List;

/**
 * Created by chenl on 2017/5/9.
 */

public class BoxOperationsAdapter extends BaseQuickAdapter<BoxOperationsItem, BaseViewHolder> {

    public BoxOperationsAdapter(int layoutResId, List<BoxOperationsItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BoxOperationsItem item) {
        helper.setText(R.id.item_bop_sn, "批次：" + item.getSn());
        helper.setText(R.id.item_bop_deposit, "存放储位：" + item.getStorage());

        helper.addOnClickListener(R.id.item_bop_del);
    }
}
