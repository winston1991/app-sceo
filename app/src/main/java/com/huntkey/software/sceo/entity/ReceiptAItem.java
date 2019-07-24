package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2017/7/10.
 */

public class ReceiptAItem implements Serializable {

    private String lstm_nbr;
    private String lstm_id;
    private boolean choosed;

    public String getLstm_nbr() {
        return lstm_nbr;
    }

    public void setLstm_nbr(String lstm_nbr) {
        this.lstm_nbr = lstm_nbr;
    }

    public String getLstm_id() {
        return lstm_id;
    }

    public void setLstm_id(String lstm_id) {
        this.lstm_id = lstm_id;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }
}
