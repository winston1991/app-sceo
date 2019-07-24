package com.huntkey.software.sceo.ui.adapter.home;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.OutstockLv0Item;
import com.huntkey.software.sceo.entity.OutstockLv1Item;

import java.util.List;

/**
 * Created by chenl on 2017/5/18.
 */

public class OutstockGoodsAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public OutstockGoodsAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_outstock_lv0);
        addItemType(TYPE_LEVEL_1, R.layout.item_outstock_lv1);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()){
            case TYPE_LEVEL_0:
                final OutstockLv0Item lv0 = (OutstockLv0Item) item;
                helper.setText(R.id.item_outstock_part, "料号：" + lv0.getLad_part())
                        .setText(R.id.item_outstock_need, "需求量：" + lv0.getQty_pick())
//                        .setText(R.id.item_outstock_begin, "期初超发：" + lv0.getQty_first())
//                        .setText(R.id.item_outstock_final, "期末超发：" + lv0.getQty_last())
                        .setText(R.id.item_outstock_num, lv0.getNum())
                        .setImageResource(R.id.item_outstock_iv, lv0.isExpanded() ? R.drawable.ic_arrow_down_gray : R.drawable.ic_arrow_right);
                if ("1".equals(lv0.getIsover())){//超发
                    helper.setText(R.id.item_outstock_actual, "点数量：" + lv0.getQty_check());
                    helper.setText(R.id.item_outstock_flag, "超发仓");
                    helper.setBackgroundRes(R.id.item_outstock_layout, R.color.white);
                }else {
                    helper.setText(R.id.item_outstock_actual, "点数量：" + lv0.getPointNum());
                    helper.setText(R.id.item_outstock_flag, "普通仓");
                    if (!lv0.getQty_pick().equals(lv0.getPointNum())){
                        helper.setBackgroundRes(R.id.item_outstock_layout, R.color.error_stroke_color);
                    }else {
                        helper.setBackgroundRes(R.id.item_outstock_layout, R.color.white);
                    }
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
                final OutstockLv1Item lv1 = (OutstockLv1Item) item;
                helper.setText(R.id.item_outstock_no, lv1.getNo())
                        .setText(R.id.item_outstock_sn, "批次：" + lv1.getLd_sn());
                helper.addOnClickListener(R.id.item_outstock_del);
                break;
        }
    }
}
