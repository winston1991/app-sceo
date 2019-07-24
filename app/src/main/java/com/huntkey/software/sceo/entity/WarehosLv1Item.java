package com.huntkey.software.sceo.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.huntkey.software.sceo.ui.adapter.home.WarehousingAdapter;

/**
 * Created by chenl on 2017/5/16.
 */

public class WarehosLv1Item implements MultiItemEntity {
    @Override
    public int getItemType() {
        return WarehousingAdapter.TYPE_LEVEL_1;
    }

    private String id;
    private String lid;//批次表id
    private String no;
    private String part;
    private String sn;
    private String lot;
    private String line;
    private String count;//数量
    private String storage = "";//被分配的储位
    private String tskd_qty;
    private String tskl_lot_ref;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getLid() {
        return lid;
    }

    public void setLid(String lid) {
        this.lid = lid;
    }

    public String getTskd_qty() {
        return tskd_qty;
    }

    public void setTskd_qty(String tskd_qty) {
        this.tskd_qty = tskd_qty;
    }

    public String getTskl_lot_ref() {
        return tskl_lot_ref;
    }

    public void setTskl_lot_ref(String tskl_lot_ref) {
        this.tskl_lot_ref = tskl_lot_ref;
    }
}
