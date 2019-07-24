package com.huntkey.software.sceo.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenl on 2017/12/19.
 */

public class OutputNoScanItem extends RealmObject {

    @PrimaryKey
    private String tskp_id;
    private String tskp_ref;
    private String tskp_tskm_nbr;
    private String tskm_ref_nbr;
    private String tskp_part;
    private String tskp_lot;
    private String tskp_loc;
    private String tskp_qty;
    private String tskp_qty_o;
    private String tskp_qty_check;
    private String num;
    private boolean flag = false;

    public String getTskp_ref() {
        return tskp_ref;
    }

    public void setTskp_ref(String tskp_ref) {
        this.tskp_ref = tskp_ref;
    }

    public String getTskp_tskm_nbr() {
        return tskp_tskm_nbr;
    }

    public void setTskp_tskm_nbr(String tskp_tskm_nbr) {
        this.tskp_tskm_nbr = tskp_tskm_nbr;
    }

    public String getTskm_ref_nbr() {
        return tskm_ref_nbr;
    }

    public void setTskm_ref_nbr(String tskm_ref_nbr) {
        this.tskm_ref_nbr = tskm_ref_nbr;
    }

    public String getTskp_part() {
        return tskp_part;
    }

    public void setTskp_part(String tskp_part) {
        this.tskp_part = tskp_part;
    }

    public String getTskp_loc() {
        return tskp_loc;
    }

    public void setTskp_loc(String tskp_loc) {
        this.tskp_loc = tskp_loc;
    }

    public String getTskp_qty() {
        return tskp_qty;
    }

    public void setTskp_qty(String tskp_qty) {
        this.tskp_qty = tskp_qty;
    }

    public String getTskp_id() {
        return tskp_id;
    }

    public void setTskp_id(String tskp_id) {
        this.tskp_id = tskp_id;
    }

    public String getTskp_qty_o() {
        return tskp_qty_o;
    }

    public void setTskp_qty_o(String tskp_qty_o) {
        this.tskp_qty_o = tskp_qty_o;
    }

    public String getTskp_qty_check() {
        return tskp_qty_check;
    }

    public void setTskp_qty_check(String tskp_qty_check) {
        this.tskp_qty_check = tskp_qty_check;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTskp_lot() {
        return tskp_lot;
    }

    public void setTskp_lot(String tskp_lot) {
        this.tskp_lot = tskp_lot;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
