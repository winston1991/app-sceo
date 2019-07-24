package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2018/2/8.
 */

public class StorageQueryDData extends BaseData  {

    private List<StorageQueryDItem> data;

    public List<StorageQueryDItem> getData() {
        return data;
    }

    public void setData(List<StorageQueryDItem> data) {
        this.data = data;
    }
}
