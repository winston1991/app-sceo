package com.huntkey.software.sceo.ui.adapter.home;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.OutputPrintItem;
import com.huntkey.software.sceo.entity.ToogleEvent;
import com.huntkey.software.sceo.utils.EventBusUtil;

import java.util.List;

/**
 * Created by chenl on 2017/4/28.
 */

public class OutputPrintAdapter extends BaseQuickAdapter<OutputPrintItem, BaseViewHolder> {

    public OutputPrintAdapter(int layoutResId, List<OutputPrintItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final OutputPrintItem item) {
        helper.setText(R.id.item_pp_status, item.getBcls_status());
        helper.setText(R.id.item_pp_textview, item.getBcls_lot());

        helper.setOnCheckedChangeListener(R.id.item_pp_checkbox, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                ToogleEvent event = new ToogleEvent();
                event.setToogle(true);
                EventBusUtil.getInstence().post(event);
            }
        });
        helper.setChecked(R.id.item_pp_checkbox, item.isChecked());
    }
}
