package com.huntkey.software.sceo.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.ui.adapter.home.ReceiptCAdapter;

/**
 * Created by chenl on 2017/5/18.
 */

public class ReceiptCLv1Item implements MultiItemEntity {

    @Override
    public int getItemType() {
        return ReceiptCAdapter.TYPE_LEVEL_1;
    }

    private String no;//小序号
    private String line;//大序号
    private String ld_part;
    private String ld_qty;
    private String ld_lot;
    private String ld_sn;
    private boolean flag;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getLd_part() {
        return ld_part;
    }

    public void setLd_part(String ld_part) {
        this.ld_part = ld_part;
    }

    public String getLd_qty() {
        return ld_qty;
    }

    public void setLd_qty(String ld_qty) {
        this.ld_qty = ld_qty;
    }

    public String getLd_lot() {
        return ld_lot;
    }

    public void setLd_lot(String ld_lot) {
        this.ld_lot = ld_lot;
    }

    public String getLd_sn() {
        return ld_sn;
    }

    public void setLd_sn(String ld_sn) {
        this.ld_sn = ld_sn;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
