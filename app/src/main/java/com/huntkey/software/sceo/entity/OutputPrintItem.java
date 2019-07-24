package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2017/4/28.
 */

public class OutputPrintItem implements Serializable {

    private String bcls_lot;
    private String bcls_part;
    private String bcls_dc;
    private String bcls_vend;
    private String bcls_sn;
    private String bcls_qty;
    private String bcls_adduser;
    private String bcls_vendor_part;
    private String bcls_type;
    private String bcls_id;
    private String bcls_status;
    private boolean checked;
    private String spl_name;//供应商

    public String getBcls_lot() {
        return bcls_lot;
    }

    public void setBcls_lot(String bcls_lot) {
        this.bcls_lot = bcls_lot;
    }

    public String getBcls_part() {
        return bcls_part;
    }

    public void setBcls_part(String bcls_part) {
        this.bcls_part = bcls_part;
    }

    public String getBcls_dc() {
        return bcls_dc;
    }

    public void setBcls_dc(String bcls_dc) {
        this.bcls_dc = bcls_dc;
    }

    public String getBcls_vend() {
        return bcls_vend;
    }

    public void setBcls_vend(String bcls_vend) {
        this.bcls_vend = bcls_vend;
    }

    public String getBcls_sn() {
        return bcls_sn;
    }

    public void setBcls_sn(String bcls_sn) {
        this.bcls_sn = bcls_sn;
    }

    public String getBcls_qty() {
        return bcls_qty;
    }

    public void setBcls_qty(String bcls_qty) {
        this.bcls_qty = bcls_qty;
    }

    public String getBcls_adduser() {
        return bcls_adduser;
    }

    public void setBcls_adduser(String bcls_adduser) {
        this.bcls_adduser = bcls_adduser;
    }

    public String getBcls_vendor_part() {
        return bcls_vendor_part;
    }

    public void setBcls_vendor_part(String bcls_vendor_part) {
        this.bcls_vendor_part = bcls_vendor_part;
    }

    public String getBcls_type() {
        return bcls_type;
    }

    public void setBcls_type(String bcls_type) {
        this.bcls_type = bcls_type;
    }

    public String getBcls_status() {
        return bcls_status;
    }

    public void setBcls_status(String bcls_status) {
        this.bcls_status = bcls_status;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getBcls_id() {
        return bcls_id;
    }

    public void setBcls_id(String bcls_id) {
        this.bcls_id = bcls_id;
    }

    public String getSpl_name() {
        return spl_name;
    }

    public void setSpl_name(String spl_name) {
        this.spl_name = spl_name;
    }
}
