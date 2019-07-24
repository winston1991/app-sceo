package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2017/4/14.
 */

public class BarRepairItem implements Serializable {

    private String bcls_lot;
    private String bcls_qty;
    private String ismod;

    public String getBcls_lot() {
        return bcls_lot;
    }

    public void setBcls_lot(String bcls_lot) {
        this.bcls_lot = bcls_lot;
    }

    public String getBcls_qty() {
        return bcls_qty;
    }

    public void setBcls_qty(String bcls_qty) {
        this.bcls_qty = bcls_qty;
    }

    public String getIsmod() {
        return ismod;
    }

    public void setIsmod(String ismod) {
        this.ismod = ismod;
    }
}
