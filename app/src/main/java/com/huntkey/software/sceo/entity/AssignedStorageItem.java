package com.huntkey.software.sceo.entity;

/**
 * Created by chenl on 2017/4/24.
 */

public class AssignedStorageItem {

    private String tskp_ref_default;
    private String tskp_ref;
    private String tskp_id;
    private String tskp_tskd_id;
    private String tskp_sn;
    private String tskp_lot;
    private String tskp_tskm_nbr;
    private String tskm_ref_nbr;

    public String getTskp_ref_default() {
        return tskp_ref_default;
    }

    public void setTskp_ref_default(String tskp_ref_default) {
        this.tskp_ref_default = tskp_ref_default;
    }

    public String getTskp_ref() {
        return tskp_ref;
    }

    public void setTskp_ref(String tskp_ref) {
        this.tskp_ref = tskp_ref;
    }

    public String getTskp_id() {
        return tskp_id;
    }

    public void setTskp_id(String tskp_id) {
        this.tskp_id = tskp_id;
    }

    public String getTskp_tskd_id() {
        return tskp_tskd_id;
    }

    public void setTskp_tskd_id(String tskp_tskd_id) {
        this.tskp_tskd_id = tskp_tskd_id;
    }

    public String getTskp_sn() {
        return tskp_sn;
    }

    public void setTskp_sn(String tskp_sn) {
        this.tskp_sn = tskp_sn;
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

    public String getTskp_lot() {
        return tskp_lot;
    }

    public void setTskp_lot(String tskp_lot) {
        this.tskp_lot = tskp_lot;
    }
}
