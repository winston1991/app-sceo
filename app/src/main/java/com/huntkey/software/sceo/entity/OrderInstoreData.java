package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/8/23.
 */

public class OrderInstoreData extends BaseData {

    private List<OrderInStoreItem> data;

    public List<OrderInStoreItem> getData() {
        return data;
    }

    public void setData(List<OrderInStoreItem> data) {
        this.data = data;
    }
}
