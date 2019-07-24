package com.huntkey.software.sceo.ui.adapter.home;

import android.widget.CompoundButton;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.PersonalJobItem;
import com.huntkey.software.sceo.entity.ToogleEvent;
import com.huntkey.software.sceo.utils.EventBusUtil;

import java.util.List;

/**
 * Created by chenl on 2017/4/17.
 */

public class PersonalJobAdapter extends BaseQuickAdapter<PersonalJobItem, BaseViewHolder> {

    public PersonalJobAdapter(int layoutResId, List<PersonalJobItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final PersonalJobItem item) {
        helper.setText(R.id.item_pj_title, item.getTskm_dept());
        helper.setText(R.id.item_pj_content, item.getTskm_type() + "-" + item.getTskm_ref_nbr());
        helper.setText(R.id.item_pj_other, item.getTskm_nbr_group() + " " +
                item.getTskm_plan_date() + " " + item.getTskm_plan_time());

        if ("1".equals(item.getGroup())){   //出库
            helper.setVisible(R.id.item_pj_checkbox, true);
            helper.setVisible(R.id.item_pj_charname, true);
            helper.setText(R.id.item_pj_charname, item.getTskm_char_name());
        }else {    //入库
            helper.setVisible(R.id.item_pj_charname, false);
            if ("32".equals(item.getLstm_type())){
                helper.setVisible(R.id.item_pj_checkbox, true);
            }else {
                helper.setVisible(R.id.item_pj_checkbox, false);
            }
//            helper.setVisible(R.id.item_pj_checkbox, false);//取消入库合并
//            if ("23".equals(item.getLstm_type()) || "24".equals(item.getLstm_type())){//只有制令单才可以合并
//                helper.setVisible(R.id.item_pj_checkbox, true);
//            }else {
//                helper.setVisible(R.id.item_pj_checkbox, false);
//            }
        }

        helper.setOnCheckedChangeListener(R.id.item_pj_checkbox, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                ToogleEvent event = new ToogleEvent();
                event.setToogle(true);
                EventBusUtil.getInstence().post(event);
            }
        });
        helper.setChecked(R.id.item_pj_checkbox, item.isChecked());
    }
}
