package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.OutputNoScanItem;

import java.util.List;

/**
 * Created by chenl on 2017/4/28.
 */

public class OutputNoScanAdapter extends BaseQuickAdapter<OutputNoScanItem, BaseViewHolder> {

    public OutputNoScanAdapter(int layoutResId, List<OutputNoScanItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OutputNoScanItem item) {
        helper.setText(R.id.item_opos_o, item.getNum());
        helper.setText(R.id.item_opos_part, "料号 " + item.getTskp_part());
        helper.setText(R.id.item_opos_type, "仓库 " + item.getTskp_loc());
        helper.setText(R.id.item_opos_num, "需求量 " + item.getTskp_qty());
        helper.setText(R.id.item_opos_pick, "本次发料量 " + item.getTskp_qty_o());

        if (item.isFlag()) {
            helper.setBackgroundRes(R.id.item_opos_layout, R.color.white);
        }else {
            helper.setBackgroundRes(R.id.item_opos_layout, R.color.error_stroke_color);
        }
    }
}
