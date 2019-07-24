package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/5/8.
 */

public class AdjustOrderData extends BaseData {

    private List<AdjustOrderItem> data;

    public List<AdjustOrderItem> getData() {
        return data;
    }

    public void setData(List<AdjustOrderItem> data) {
        this.data = data;
    }
}
