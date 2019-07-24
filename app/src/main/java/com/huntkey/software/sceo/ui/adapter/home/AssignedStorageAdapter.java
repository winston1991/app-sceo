package com.huntkey.software.sceo.ui.adapter.home;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.AssignedStorageItem;

import java.util.List;

/**
 * Created by chenl on 2017/4/24.
 */

public class AssignedStorageAdapter extends BaseQuickAdapter<AssignedStorageItem, BaseViewHolder> {

    public AssignedStorageAdapter(int layoutResId, List<AssignedStorageItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AssignedStorageItem item) {
        helper.setText(R.id.item_ass_sn, "条码：" + item.getTskp_lot());
        helper.setText(R.id.item_ass_distribution, "分配储位：" + item.getTskp_ref_default());
        helper.setText(R.id.item_ass_deposit, "存放储位：" + item.getTskp_ref());

        helper.addOnClickListener(R.id.item_ass_del);
    }

}
