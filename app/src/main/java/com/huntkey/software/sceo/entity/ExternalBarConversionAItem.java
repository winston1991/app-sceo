package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2017/4/10.
 */

public class ExternalBarConversionAItem implements Serializable {

    private String lstm_nbr;//收货单号
    private String lstm_addr;//供应商
    private String lstd_line;//项次
    private String lstd_part;//料号

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
}
