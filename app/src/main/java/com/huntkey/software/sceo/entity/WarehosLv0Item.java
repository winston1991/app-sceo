package com.huntkey.software.sceo.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.ui.adapter.home.WarehousingAdapter;

/**
 * Created by chenl on 2017/5/16.
 */

public class WarehosLv0Item extends AbstractExpandableItem<WarehosLv1Item> implements MultiItemEntity {
    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return WarehousingAdapter.TYPE_LEVEL_0;
    }

    private String tskm_nbr;
    private String tskm_ref_nbr;
    private String tskd_id;
    private String tskl_id;
    private String tskd_line;
    private String tskd_part;
    private String tskd_qty;
    private String pointNum = "0";//点数量
    private String tskl_lot_ref;

    public String getTskm_nbr() {
        return tskm_nbr;
    }

    public void setTskm_nbr(String tskm_nbr) {
        this.tskm_nbr = tskm_nbr;
    }

    public String getTskm_ref_nbr() {
        return tskm_ref_nbr;
    }

    public void setTskm_ref_nbr(String tskm_ref_nbr) {
        this.tskm_ref_nbr = tskm_ref_nbr;
    }

    public String getTskd_id() {
        return tskd_id;
    }

    public void setTskd_id(String tskd_id) {
        this.tskd_id = tskd_id;
    }

    public String getTskd_line() {
        return tskd_line;
    }

    public void setTskd_line(String tskd_line) {
        this.tskd_line = tskd_line;
    }

    public String getTskd_part() {
        return tskd_part;
    }

    public void setTskd_part(String tskd_part) {
        this.tskd_part = tskd_part;
    }

    public String getTskd_qty() {
        return tskd_qty;
    }

    public void setTskd_qty(String tskd_qty) {
        this.tskd_qty = tskd_qty;
    }

    public String getPointNum() {
        return pointNum;
    }

    public void setPointNum(String pointNum) {
        this.pointNum = pointNum;
    }

    public String getTskl_id() {
        return tskl_id;
    }

    public void setTskl_id(String tskl_id) {
        this.tskl_id = tskl_id;
    }

    public String getTskl_lot_ref() {
        return tskl_lot_ref;
    }

    public void setTskl_lot_ref(String tskl_lot_ref) {
        this.tskl_lot_ref = tskl_lot_ref;
    }
}
