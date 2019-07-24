package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2018/2/8.
 */

public class StorageQueryCItem {

    private String ld_part;//料号
    private String pt_desc;//描述
    private String ld_loc;//仓库
    private String ld_lot;//条码
    private String ld_ref;//储位
    private String ld_qty_oh;//库存数
    private String ld_qty_frz;//冻结量
    private String ld_qty_pick;//捡料量
    private int num;

    public String getLd_part() {
        return ld_part;
    }

    public void setLd_part(String ld_part) {
        this.ld_part = ld_part;
    }

    public String getPt_desc() {
        return pt_desc;
    }

    public void setPt_desc(String pt_desc) {
        this.pt_desc = pt_desc;
    }

    public String getLd_loc() {
        return ld_loc;
    }

    public void setLd_loc(String ld_loc) {
        this.ld_loc = ld_loc;
    }

    public String getLd_lot() {
        return ld_lot;
    }

    public void setLd_lot(String ld_lot) {
        this.ld_lot = ld_lot;
    }

    public String getLd_ref() {
        return ld_ref;
    }

    public void setLd_ref(String ld_ref) {
        this.ld_ref = ld_ref;
    }

    public String getLd_qty_oh() {
        return ld_qty_oh;
    }

    public void setLd_qty_oh(String ld_qty_oh) {
        this.ld_qty_oh = ld_qty_oh;
    }

    public String getLd_qty_frz() {
        return ld_qty_frz;
    }

    public void setLd_qty_frz(String ld_qty_frz) {
        this.ld_qty_frz = ld_qty_frz;
    }

    public String getLd_qty_pick() {
        return ld_qty_pick;
    }

    public void setLd_qty_pick(String ld_qty_pick) {
        this.ld_qty_pick = ld_qty_pick;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
