package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2017/5/8.
 */

public class AdjustOrderItem implements Serializable {

    private String ld_part;
    private String ld_loc;
    private String ld_site;
    private String ld_lot;
    private String ld_ref;
    private String lot_sn;
    private String newRef = "";//新储位

    public String getLd_part() {
        return ld_part;
    }

    public void setLd_part(String ld_part) {
        this.ld_part = ld_part;
    }

    public String getLd_loc() {
        return ld_loc;
    }

    public void setLd_loc(String ld_loc) {
        this.ld_loc = ld_loc;
    }

    public String getLd_site() {
        return ld_site;
    }

    public void setLd_site(String ld_site) {
        this.ld_site = ld_site;
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

    public String getLot_sn() {
        return lot_sn;
    }

    public void setLot_sn(String lot_sn) {
        this.lot_sn = lot_sn;
    }

    public String getNewRef() {
        return newRef;
    }

    public void setNewRef(String newRef) {
        this.newRef = newRef;
    }
}
