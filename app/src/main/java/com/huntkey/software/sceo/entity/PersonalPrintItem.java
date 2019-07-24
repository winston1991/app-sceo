package com.huntkey.software.sceo.entity;

/**
 * Created by chenl on 2017/3/29.
 */

public class PersonalPrintItem {

    private String bclm_lot;
    private String bclm_id;
    private String bcls_status;
    private boolean checked = false;

    public String getBclm_lot() {
        return bclm_lot;
    }

    public void setBclm_lot(String bclm_lot) {
        this.bclm_lot = bclm_lot;
    }

    public String getBclm_id() {
        return bclm_id;
    }

    public void setBclm_id(String bclm_id) {
        this.bclm_id = bclm_id;
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
}
