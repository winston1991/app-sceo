package com.huntkey.software.sceo.ui.adapter.home;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.PersonalPrintDetailItem;
import com.huntkey.software.sceo.entity.PersonalPrintItem;

import java.util.List;

/**
 * Created by chenl on 2017/3/29.
 */

public class PersonalPrintDetailAdapter extends BaseItemDraggableAdapter<PersonalPrintDetailItem, BaseViewHolder> {

    public PersonalPrintDetailAdapter(int layoutResId, List<PersonalPrintDetailItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final PersonalPrintDetailItem item) {
        helper.setText(R.id.item_pp_status, item.getBcls_status());
        helper.setText(R.id.item_pp_textview, item.getBcls_lot());

//        if ("N".equals(item.getBcls_status())){
//            helper.setVisible(R.id.item_pp_checkbox, false);
//        }else {
            helper.setVisible(R.id.item_pp_checkbox, true);
            helper.setOnCheckedChangeListener(R.id.item_pp_checkbox, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setChecked(isChecked);
                }
            });
            helper.setChecked(R.id.item_pp_checkbox, item.isChecked());
//        }

    }
}
