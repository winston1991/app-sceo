package com.huntkey.software.sceo.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.ui.adapter.home.OutstockGoodsAdapter;

/**
 * Created by chenl on 2017/5/18.
 */

public class OutstockLv0Item extends AbstractExpandableItem<OutstockLv1Item> implements MultiItemEntity {
    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return OutstockGoodsAdapter.TYPE_LEVEL_0;
    }

    private String num;//序号
    private String lstm_nbr;
    private String lad_part;
    private String qty_pick;
    private String qty_check;
    private String qty_first;
    private String qty_last;
    private String pointNum = "0";//点数量
    private String isover;

    public String getLstm_nbr() {
        return lstm_nbr;
    }

    public void setLstm_nbr(String lstm_nbr) {
        this.lstm_nbr = lstm_nbr;
    }

    public String getLad_part() {
        return lad_part;
    }

    public void setLad_part(String lad_part) {
        this.lad_part = lad_part;
    }

    public String getQty_pick() {
        return qty_pick;
    }

    public void setQty_pick(String qty_pick) {
        this.qty_pick = qty_pick;
    }

    public String getQty_check() {
        return qty_check;
    }

    public void setQty_check(String qty_check) {
        this.qty_check = qty_check;
    }

    public String getQty_first() {
        return qty_first;
    }

    public void setQty_first(String qty_first) {
        this.qty_first = qty_first;
    }

    public String getQty_last() {
        return qty_last;
    }

    public void setQty_last(String qty_last) {
        this.qty_last = qty_last;
    }

    public String getPointNum() {
        return pointNum;
    }

    public void setPointNum(String pointNum) {
        this.pointNum = pointNum;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getIsover() {
        return isover;
    }

    public void setIsover(String isover) {
        this.isover = isover;
    }
}
