package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2018/2/8.
 */

public class StorageQueryCData extends BaseData  {

    private List<StorageQueryCItem> data;

    public List<StorageQueryCItem> getData() {
        return data;
    }

    public void setData(List<StorageQueryCItem> data) {
        this.data = data;
    }
}
