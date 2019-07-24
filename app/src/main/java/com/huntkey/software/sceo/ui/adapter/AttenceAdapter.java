package com.huntkey.software.sceo.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.AttenceInfoItem;

import java.util.List;

/**
 * Created by chenl on 2017/9/20.
 */

public class AttenceAdapter extends BaseQuickAdapter<AttenceInfoItem, BaseViewHolder> {

    public AttenceAdapter(int layoutResId, List<AttenceInfoItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AttenceInfoItem item) {
        helper.setText(R.id.item_attence_time, "打卡时间 " + item.getEmp_time());
        helper.setText(R.id.item_attence_addr, item.getLocation());
    }
}
