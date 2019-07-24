package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2017/8/23.
 */

public class OrderInStoreItem implements Serializable {

    private String wo_nbr;
    private String wo_id;
    private String wo_type;
    private String wo_dept;
    private String wo_part;
    private String wo_um;
    private String wo_loc;
    private String wo_lot;
    private String wo_ref;
    private String wo_qty;

    public String getWo_nbr() {
        return wo_nbr;
    }

    public void setWo_nbr(String wo_nbr) {
        this.wo_nbr = wo_nbr;
    }

    public String getWo_id() {
        return wo_id;
    }

    public void setWo_id(String wo_id) {
        this.wo_id = wo_id;
    }

    public String getWo_type() {
        return wo_type;
    }

    public void setWo_type(String wo_type) {
        this.wo_type = wo_type;
    }

    public String getWo_dept() {
        return wo_dept;
    }

    public void setWo_dept(String wo_dept) {
        this.wo_dept = wo_dept;
    }

    public String getWo_part() {
        return wo_part;
    }

    public void setWo_part(String wo_part) {
        this.wo_part = wo_part;
    }

    public String getWo_um() {
        return wo_um;
    }

    public void setWo_um(String wo_um) {
        this.wo_um = wo_um;
    }

    public String getWo_loc() {
        return wo_loc;
    }

    public void setWo_loc(String wo_loc) {
        this.wo_loc = wo_loc;
    }

    public String getWo_lot() {
        return wo_lot;
    }

    public void setWo_lot(String wo_lot) {
        this.wo_lot = wo_lot;
    }

    public String getWo_ref() {
        return wo_ref;
    }

    public void setWo_ref(String wo_ref) {
        this.wo_ref = wo_ref;
    }

    public String getWo_qty() {
        return wo_qty;
    }

    public void setWo_qty(String wo_qty) {
        this.wo_qty = wo_qty;
    }
}
