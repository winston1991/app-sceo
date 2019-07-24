package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2018/2/8.
 */

public class StorageQueryData extends BaseData  {

    private List<StorageQueryItem> data;

    public List<StorageQueryItem> getData() {
        return data;
    }

    public void setData(List<StorageQueryItem> data) {
        this.data = data;
    }
}
