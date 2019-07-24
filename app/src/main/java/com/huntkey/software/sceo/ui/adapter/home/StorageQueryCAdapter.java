package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.StorageQueryCItem;

import java.util.List;

/**
 * Created by chenl on 2018/2/9.
 */

public class StorageQueryCAdapter extends BaseQuickAdapter<StorageQueryCItem, BaseViewHolder> {

    public StorageQueryCAdapter(int layoutResId, List<StorageQueryCItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StorageQueryCItem item) {
        helper.setText(R.id.item_stoqc_o, item.getNum() + "");
        helper.setText(R.id.item_stoqc_site, "仓库 " + item.getLd_loc());
        helper.setText(R.id.item_stoqc_oh, "储位 " + item.getLd_ref());
        helper.setText(R.id.item_stoqc_lot, "批次 " + item.getLd_lot());
        helper.setText(R.id.item_stoqc_avail, "库存 " + item.getLd_qty_oh());
        helper.setText(R.id.item_stoqc_pick, "捡料量 " + item.getLd_qty_pick());
        helper.setText(R.id.item_stoqc_frezz, "冻结量 " + item.getLd_qty_frz());
    }
}
