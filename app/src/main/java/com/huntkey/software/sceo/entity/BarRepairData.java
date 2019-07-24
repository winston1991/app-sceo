package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/4/14.
 */

public class BarRepairData extends BaseData {

    private List<BarRepairItem> data;

    public List<BarRepairItem> getData() {
        return data;
    }

    public void setData(List<BarRepairItem> data) {
        this.data = data;
    }
}
