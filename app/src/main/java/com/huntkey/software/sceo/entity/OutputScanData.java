package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/4/28.
 */

public class OutputScanData extends BaseData {

    private List<OutputScanItem> data;

    public List<OutputScanItem> getData() {
        return data;
    }

    public void setData(List<OutputScanItem> data) {
        this.data = data;
    }
}
