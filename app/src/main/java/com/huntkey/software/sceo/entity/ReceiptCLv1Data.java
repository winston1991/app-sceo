package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/5/18.
 */

public class ReceiptCLv1Data extends BaseData {

    private List<ReceiptCLv1Item> data;

    public List<ReceiptCLv1Item> getData() {
        return data;
    }

    public void setData(List<ReceiptCLv1Item> data) {
        this.data = data;
    }
}
