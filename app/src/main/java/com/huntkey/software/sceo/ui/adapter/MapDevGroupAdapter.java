package com.huntkey.software.sceo.ui.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.entity.MapDevGroupLv0;
import com.huntkey.software.sceo.entity.MapDevGroupLv1;
import com.huntkey.software.sceo.entity.MapDevGroupLv2;
import com.huntkey.software.sceo.entity.MapDevGroupLv3;

import java.util.List;

/**
 * Created by chenl on 2017/9/12.
 */

public class MapDevGroupAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public MapDevGroupAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(0, R.layout.item_mapdevgro_lv0);
        addItemType(1, R.layout.item_mapdevgro_lv1);
        addItemType(2, R.layout.item_mapdevgro_lv2);
        addItemType(3, R.layout.item_mapdevgro_lv3);
    }

    @Override
    protected void convert(final BaseViewHolder holder, MultiItemEntity item) {
        switch (holder.getItemViewType()){
            case 0:
                final MapDevGroupLv0 lv0 = (MapDevGroupLv0) item;
                holder.setText(R.id.item_mapdevgro_tv, lv0.getAdeg_name())
                        .setImageResource(R.id.item_mapdevgro_iv, lv0.isExpanded() ? R.drawable.ic_arrow_down_gray : R.drawable.ic_arrow_right);
                holder.addOnClickListener(R.id.item_mapdevgro_cb);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        if (lv0.isExpanded()) {
                            collapse(pos, false);
                        } else {
                            expand(pos, false);
                        }
                    }
                });
                break;
            case 1:
                final MapDevGroupLv1 lv1 = (MapDevGroupLv1) item;
                holder.setText(R.id.item_mapdevgro_tv, lv1.getAdeg_name())
                        .setImageResource(R.id.item_mapdevgro_iv, lv1.isExpanded() ? R.drawable.ic_arrow_down_gray : R.drawable.ic_arrow_right);
                holder.addOnClickListener(R.id.item_mapdevgro_cb);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        if (lv1.isExpanded()) {
                            collapse(pos, false);
                        } else {
                            expand(pos, false);
                        }
                    }
                });
                break;
            case 2:
                final MapDevGroupLv2 lv2 = (MapDevGroupLv2) item;
                holder.setText(R.id.item_mapdevgro_tv, lv2.getAdeg_name())
                        .setImageResource(R.id.item_mapdevgro_iv, lv2.isExpanded() ? R.drawable.ic_arrow_down_gray : R.drawable.ic_arrow_right);
                holder.addOnClickListener(R.id.item_mapdevgro_cb);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        if (lv2.isExpanded()) {
                            collapse(pos, false);
                        } else {
                            expand(pos, false);
                        }
                    }
                });
                break;
            case 3:
                MapDevGroupLv3 lv3 = (MapDevGroupLv3) item;
                holder.setText(R.id.item_mapdevgro_tv, lv3.getAdeg_name());
                holder.addOnClickListener(R.id.item_mapdevgro_cb);
                break;
        }
    }
}
