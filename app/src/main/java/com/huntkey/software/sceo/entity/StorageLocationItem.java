package com.huntkey.software.sceo.entity;

/**
 * Created by chenl on 2017/4/27.
 */

public class StorageLocationItem {

    private String tskp_ref;
    private String tskp_tskm_nbr;
    private String tskm_ref_nbr;
    private String pick_count;
    private String isok;

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

    public String getPick_count() {
        return pick_count;
    }

    public void setPick_count(String pick_count) {
        this.pick_count = pick_count;
    }

    public String getIsok() {
        return isok;
    }

    public void setIsok(String isok) {
        this.isok = isok;
    }
}
