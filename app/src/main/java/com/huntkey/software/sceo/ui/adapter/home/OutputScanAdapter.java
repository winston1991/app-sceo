package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.OutputScanItem;

import java.util.List;

/**
 * Created by chenl on 2017/4/28.
 */

public class OutputScanAdapter extends BaseQuickAdapter<OutputScanItem, BaseViewHolder> {

    public OutputScanAdapter(int layoutResId, List<OutputScanItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OutputScanItem item) {
        helper.setText(R.id.item_ops_o, item.getNum());
        helper.setText(R.id.item_ops_sn, "批次 " + item.getTskp_lot());
        helper.setText(R.id.item_ops_qty, "已扫 " + item.getTskp_qty_check());
        helper.setText(R.id.item_ops_part, "料号 " + item.getTskp_part());
        helper.setText(R.id.item_ops_num, "库存数 " + item.getTskp_qty());
        helper.setText(R.id.item_ops_pick, "捡料数 " + item.getTskp_qty_o());
        helper.setText(R.id.item_ops_type, "作业方式 " + item.getTskp_op_code_desc());

        helper.addOnClickListener(R.id.item_ops_del);
    }
}
