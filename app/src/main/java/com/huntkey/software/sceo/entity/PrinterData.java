package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/4/12.
 */

public class PrinterData extends BaseData {

    private List<PrinterItem> data;

    public List<PrinterItem> getData() {
        return data;
    }

    public void setData(List<PrinterItem> data) {
        this.data = data;
    }
}
