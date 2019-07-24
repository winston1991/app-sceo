package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/5/18.
 */

public class OutstockLv0Data extends BaseData {

    private List<OutstockLv0Item> data;

    public List<OutstockLv0Item> getData() {
        return data;
    }

    public void setData(List<OutstockLv0Item> data) {
        this.data = data;
    }
}
