package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/4/28.
 */

public class OutputPrintData extends BaseData {

    private List<OutputPrintItem> data;

    public List<OutputPrintItem> getData() {
        return data;
    }

    public void setData(List<OutputPrintItem> data) {
        this.data = data;
    }
}
