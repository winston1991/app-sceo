package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/4/28.
 */

public class StorageLocationData extends BaseData {

    private List<StorageLocationItem> data;

    public List<StorageLocationItem> getData() {
        return data;
    }

    public void setData(List<StorageLocationItem> data) {
        this.data = data;
    }
}
