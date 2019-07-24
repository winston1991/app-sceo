package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.InputPickingNumItem;

import java.util.List;

/**
 * Created by chenl on 2017/5/18.
 */

public class ChooseStorekeeperAdapter extends BaseQuickAdapter<InputPickingNumItem, BaseViewHolder> {

    public ChooseStorekeeperAdapter(int layoutResId, List<InputPickingNumItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, InputPickingNumItem item) {
        helper.setText(R.id.item_csk_tv, item.getEmp_name());
        helper.setImageResource(R.id.item_csk_rb, item.isChoosed() ? R.drawable.ic_radio_check : R.drawable.ic_radio_uncheck);
    }
}
