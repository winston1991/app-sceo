package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/7/10.
 */

public class ReceiptCLv0Data extends BaseData {

    private List<ReceiptCLv0Item> data;

    public List<ReceiptCLv0Item> getData() {
        return data;
    }

    public void setData(List<ReceiptCLv0Item> data) {
        this.data = data;
    }
}
