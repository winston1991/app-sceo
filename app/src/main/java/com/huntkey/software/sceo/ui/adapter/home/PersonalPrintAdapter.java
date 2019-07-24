package com.huntkey.software.sceo.ui.adapter.home;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.PersonalPrintItem;
import com.huntkey.software.sceo.entity.PersonalPrintToogleEvent;
import com.huntkey.software.sceo.utils.EventBusUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenl on 2017/3/29.
 */

public class PersonalPrintAdapter extends BaseItemDraggableAdapter<PersonalPrintItem, BaseViewHolder> {

    public PersonalPrintAdapter(int layoutResId, List<PersonalPrintItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final PersonalPrintItem item) {
        helper.setText(R.id.item_pp_status, item.getBcls_status());
        helper.setText(R.id.item_pp_textview, item.getBclm_lot());

        if ("Y".equals(item.getBcls_status())){
            helper.setVisible(R.id.item_pp_checkbox, false);
        }else {
            helper.setVisible(R.id.item_pp_checkbox, true);
            helper.setOnCheckedChangeListener(R.id.item_pp_checkbox, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.setChecked(isChecked);
                    PersonalPrintToogleEvent event = new PersonalPrintToogleEvent();
                    event.setToogle(true);
                    EventBusUtil.getInstence().post(event);
                }
            });
            helper.setChecked(R.id.item_pp_checkbox, item.isChecked());
        }
    }
}
