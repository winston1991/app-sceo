package com.huntkey.software.sceo.entity;

/**
 * Created by chenl on 2018/2/8.
 */

public class StorageQueryDItem {

    private String lstm_type;//单据名称
    private String tskm_nbr_group;//单据单号
    private String tskd_line;//项次
    private String tskl_loc;//仓库
    private String tskl_lot;//批次
    private String tskl_ref;//储位
    private String tskl_req_qty;//数量
    private int num;

    public String getLstm_type() {
        return lstm_type;
    }

    public void setLstm_type(String lstm_type) {
        this.lstm_type = lstm_type;
    }

    public String getTskm_nbr_group() {
        return tskm_nbr_group;
    }

    public void setTskm_nbr_group(String tskm_nbr_group) {
        this.tskm_nbr_group = tskm_nbr_group;
    }

    public String getTskd_line() {
        return tskd_line;
    }

    public void setTskd_line(String tskd_line) {
        this.tskd_line = tskd_line;
    }

    public String getTskl_loc() {
        return tskl_loc;
    }

    public void setTskl_loc(String tskl_loc) {
        this.tskl_loc = tskl_loc;
    }

    public String getTskl_lot() {
        return tskl_lot;
    }

    public void setTskl_lot(String tskl_lot) {
        this.tskl_lot = tskl_lot;
    }

    public String getTskl_ref() {
        return tskl_ref;
    }

    public void setTskl_ref(String tskl_ref) {
        this.tskl_ref = tskl_ref;
    }

    public String getTskl_req_qty() {
        return tskl_req_qty;
    }

    public void setTskl_req_qty(String tskl_req_qty) {
        this.tskl_req_qty = tskl_req_qty;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
