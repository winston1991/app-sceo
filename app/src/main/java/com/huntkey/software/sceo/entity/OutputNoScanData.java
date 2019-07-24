package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/12/19.
 */

public class OutputNoScanData extends BaseData {

    private List<OutputNoScanItem> data;

    public List<OutputNoScanItem> getData() {
        return data;
    }

    public void setData(List<OutputNoScanItem> data) {
        this.data = data;
    }
}
