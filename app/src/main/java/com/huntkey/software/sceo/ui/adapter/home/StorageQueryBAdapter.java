package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.StorageQueryItem;

import java.util.List;

/**
 * Created by chenl on 2018/2/9.
 */

public class StorageQueryBAdapter extends BaseQuickAdapter<StorageQueryItem, BaseViewHolder> {

    public StorageQueryBAdapter(int layoutResId, List<StorageQueryItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StorageQueryItem item) {
        helper.setText(R.id.item_stoq_o, item.getNum() + "");
        helper.setText(R.id.item_stoq_site, "仓库 " + item.getIn_site());
        helper.setText(R.id.item_stoq_oh, "有效库存 " + item.getIn_qty_oh());
        helper.setText(R.id.item_stoq_avail, "可用库存 " + item.getIn_qty_avail());
        helper.setText(R.id.item_stoq_pick, "捡料数量 " + item.getIn_qty_pick());
    }
}
