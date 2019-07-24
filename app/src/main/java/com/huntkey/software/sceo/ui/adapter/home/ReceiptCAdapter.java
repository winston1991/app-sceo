package com.huntkey.software.sceo.ui.adapter.home;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.ReceiptCLv0Item;
import com.huntkey.software.sceo.entity.ReceiptCLv1Item;

import java.util.List;

/**
 * Created by chenl on 2017/7/10.
 */

public class ReceiptCAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ReceiptCAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_receiptc_lv0);
        addItemType(TYPE_LEVEL_1, R.layout.item_receiptc_lv1);
    }

    @Override
    protected void convert(final BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()){
            case TYPE_LEVEL_0:
                final ReceiptCLv0Item lv0 = (ReceiptCLv0Item) item;
                helper.setText(R.id.item_receipt_part, "料号：" + lv0.getLstd_part())
                        .setText(R.id.item_receipt_num, lv0.getNum())
                        .setText(R.id.item_receipt_need, "收货量：" + lv0.getLstd_qty())
                        .setText(R.id.item_receipt_actual, "已扫量：" + lv0.getPointNum())
                        .setImageResource(R.id.item_receipt_iv, lv0.isExpanded() ? R.drawable.ic_arrow_down_gray : R.drawable.ic_arrow_right);
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
                if (!lv0.getLstd_qty().equals(lv0.getPointNum())){
                    helper.setBackgroundRes(R.id.item_receipt_layout, R.color.error_stroke_color);
                }else {
                    helper.setBackgroundRes(R.id.item_receipt_layout, R.color.white);
                }
                break;
            case TYPE_LEVEL_1:
                final ReceiptCLv1Item lv1 = (ReceiptCLv1Item) item;
                helper.setText(R.id.item_receipt_no, lv1.getNo())
                        .setText(R.id.item_receipt_sn, "批次：" + lv1.getLd_sn());
                helper.addOnClickListener(R.id.item_receipt_del);
                if (lv1.isFlag()){
                    helper.setBackgroundRes(R.id.item1_receipt_layout, R.color.warning_stroke_color);
                }else {
                    helper.setBackgroundRes(R.id.item1_receipt_layout, R.color.white);
                }
                break;
        }
    }
}
