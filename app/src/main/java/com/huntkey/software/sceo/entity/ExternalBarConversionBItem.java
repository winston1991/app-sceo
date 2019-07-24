package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2017/4/10.
 */

public class ExternalBarConversionBItem implements Serializable {

    private String lstm_nbr;
    private String lstm_addr;
    private String lstd_line;
    private String lstd_part;
    private String lstd_dc;
    private String lstd_lot;
    private String lstd_min_pack;
    private String lstd_box;
    private String lstd_qty;

    public String getLstm_nbr() {
        return lstm_nbr;
    }

    public void setLstm_nbr(String lstm_nbr) {
        this.lstm_nbr = lstm_nbr;
    }

    public String getLstm_addr() {
        return lstm_addr;
    }

    public void setLstm_addr(String lstm_addr) {
        this.lstm_addr = lstm_addr;
    }

    public String getLstd_line() {
        return lstd_line;
    }

    public void setLstd_line(String lstd_line) {
        this.lstd_line = lstd_line;
    }

    public String getLstd_part() {
        return lstd_part;
    }

    public void setLstd_part(String lstd_part) {
        this.lstd_part = lstd_part;
    }

    public String getLstd_dc() {
        return lstd_dc;
    }

    public void setLstd_dc(String lstd_dc) {
        this.lstd_dc = lstd_dc;
    }

    public String getLstd_lot() {
        return lstd_lot;
    }

    public void setLstd_lot(String lstd_lot) {
        this.lstd_lot = lstd_lot;
    }

    public String getLstd_min_pack() {
        return lstd_min_pack;
    }

    public void setLstd_min_pack(String lstd_min_pack) {
        this.lstd_min_pack = lstd_min_pack;
    }

    public String getLstd_box() {
        return lstd_box;
    }

    public void setLstd_box(String lstd_box) {
        this.lstd_box = lstd_box;
    }

    public String getLstd_qty() {
        return lstd_qty;
    }

    public void setLstd_qty(String lstd_qty) {
        this.lstd_qty = lstd_qty;
    }
}
