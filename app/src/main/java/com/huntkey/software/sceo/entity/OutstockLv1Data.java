package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/5/18.
 */

public class OutstockLv1Data extends BaseData {

    private List<OutstockLv1Item> data;

    public List<OutstockLv1Item> getData() {
        return data;
    }

    public void setData(List<OutstockLv1Item> data) {
        this.data = data;
    }
}
