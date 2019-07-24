package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/7/10.
 */

public class ReceiptAData extends BaseData {

    private List<ReceiptAItem> data;

    public List<ReceiptAItem> getData() {
        return data;
    }

    public void setData(List<ReceiptAItem> data) {
        this.data = data;
    }
}
