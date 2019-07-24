package com.huntkey.software.sceo.ui.adapter.home;

import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.WarehosLv0Item;
import com.huntkey.software.sceo.entity.WarehosLv1Item;

import java.util.List;

/**
 * Created by chenl on 2017/5/16.
 */

public class WarehousingAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public WarehousingAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_warehos_lv0);
        addItemType(TYPE_LEVEL_1, R.layout.item_warehos_lv1);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()){
            case TYPE_LEVEL_0:
                final WarehosLv0Item lv0 = (WarehosLv0Item) item;
                helper.setText(R.id.item_warh_num, lv0.getTskd_line())
                        .setText(R.id.item_warh_part, "料号：" + lv0.getTskd_part())
                        .setText(R.id.item_warh_plan, "任务量：" + lv0.getTskd_qty())
                        .setText(R.id.item_warh_actual, "点数量：" + lv0.getPointNum())
                        .setImageResource(R.id.item_warh_iv, lv0.isExpanded() ? R.drawable.ic_arrow_down_gray : R.drawable.ic_arrow_right);
                if (!lv0.getTskd_qty().equals(lv0.getPointNum())){
                    helper.setBackgroundRes(R.id.item_warh_layout, R.color.error_stroke_color);
                }else {
                    helper.setBackgroundRes(R.id.item_warh_layout, R.color.white);
                }
                helper.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = helper.getAdapterPosition();
                        if (lv0.isExpanded()) {
                            collapse(pos);
                        } else {
                            expand(pos);
                        }
                    }
                });
                break;
            case TYPE_LEVEL_1:
                final WarehosLv1Item lv1 = (WarehosLv1Item) item;
                helper.setText(R.id.item_whos_num, lv1.getNo())
                        .setText(R.id.item_whos_sn, "批次：" + lv1.getSn())
                        .setText(R.id.item_whos_number, "数量：" + lv1.getCount());
                if (!TextUtils.isEmpty(lv1.getStorage())){
                    helper.setVisible(R.id.item_whos_storage, true);
                    helper.setText(R.id.item_whos_storage, "储位：" + lv1.getStorage());
                }else {
                    helper.setVisible(R.id.item_whos_storage, false);
                }
                helper.addOnClickListener(R.id.item_whos_del);
                break;
        }
    }
}
