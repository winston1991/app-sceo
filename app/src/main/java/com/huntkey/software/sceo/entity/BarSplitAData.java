package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/5/16.
 */

public class BarSplitAData extends BaseData {

    private List<BarSplitAItem> data;

    public List<BarSplitAItem> getData() {
        return data;
    }

    public void setData(List<BarSplitAItem> data) {
        this.data = data;
    }
}
