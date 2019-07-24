package com.huntkey.software.sceo.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.ui.adapter.home.ReceiptCAdapter;

/**
 * Created by chenl on 2017/7/10.
 */

public class ReceiptCLv0Item extends AbstractExpandableItem<ReceiptCLv1Item> implements MultiItemEntity {

    private String num;//序号
    private String lstd_part;
    private String lstd_qty;
    private String pointNum = "0";//已扫量

    public String getLstd_part() {
        return lstd_part;
    }

    public void setLstd_part(String lstd_part) {
        this.lstd_part = lstd_part;
    }

    public String getLstd_qty() {
        return lstd_qty;
    }

    public void setLstd_qty(String lstd_qty) {
        this.lstd_qty = lstd_qty;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getPointNum() {
        return pointNum;
    }

    public void setPointNum(String pointNum) {
        this.pointNum = pointNum;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return ReceiptCAdapter.TYPE_LEVEL_0;
    }
}
