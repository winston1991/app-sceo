package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.StorageQueryCItem;
import com.huntkey.software.sceo.entity.StorageQueryDItem;

import java.util.List;

/**
 * Created by chenl on 2018/2/9.
 */

public class StorageQueryDAdapter extends BaseQuickAdapter<StorageQueryDItem, BaseViewHolder> {

    public StorageQueryDAdapter(int layoutResId, List<StorageQueryDItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, StorageQueryDItem item) {
        helper.setText(R.id.item_stoqd_o, item.getNum() + "");
        helper.setText(R.id.item_stoqd_site, "单据单号 " + item.getTskm_nbr_group());
        helper.setText(R.id.item_stoqd_name, "单据名称 " + item.getLstm_type());
        helper.setText(R.id.item_stoqd_ite, "项次 " + item.getTskd_line());
        helper.setText(R.id.item_stoqd_ref, "仓库 " + item.getTskl_loc());
        helper.setText(R.id.item_stoqd_lot, "批次 " + item.getTskl_lot());
        helper.setText(R.id.item_stoqd_avail, "储位 " + item.getTskl_ref());
        helper.setText(R.id.item_stoqd_pick, "数量 " + item.getTskl_req_qty());
    }
}
