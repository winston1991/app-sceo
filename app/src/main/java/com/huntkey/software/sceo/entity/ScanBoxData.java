package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/5/9.
 */

public class ScanBoxData extends BaseData {

    private List<ScanBoxItem> data;

    public List<ScanBoxItem> getData() {
        return data;
    }

    public void setData(List<ScanBoxItem> data) {
        this.data = data;
    }
}
