package com.huntkey.software.sceo.ui.adapter.home;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.OutputNoScanItem;

import java.util.List;

import io.realm.Realm;

/**
 * Created by chenl on 2017/4/28.
 */

public class OutputNoScanSecondAdapter extends BaseQuickAdapter<OutputNoScanItem, BaseViewHolder> {

    public OutputNoScanSecondAdapter(int layoutResId, List<OutputNoScanItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final OutputNoScanItem item) {
        helper.setText(R.id.item_onss_o, item.getNum());
        helper.setText(R.id.item_onss_part, "料号 " + item.getTskp_part());
        helper.setText(R.id.item_onss_qty, "本次发料量 " + item.getTskp_qty());
        helper.setText(R.id.item_onss_loc, "出库仓 " + item.getTskp_loc());
        helper.setText(R.id.item_onss_qtyo, "出库量 " + item.getTskp_qty_o());
        helper.setText(R.id.item_onss_lot, "批次 " + item.getTskp_lot());
        helper.setText(R.id.item_onss_ref, "储位 " + item.getTskp_ref());

        helper.setBackgroundRes(R.id.item_onss_layout, R.color.error_stroke_color);

        helper.addOnClickListener(R.id.item_onss_del);

        helper.setOnCheckedChangeListener(R.id.item_onss_cb, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    helper.setBackgroundRes(R.id.item_onss_layout, R.color.white);
                    item.setFlag(true);
                } else {
                    helper.setBackgroundRes(R.id.item_onss_layout, R.color.error_stroke_color);
                    item.setFlag(false);
                }
            }
        });
    }
}
