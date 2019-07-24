package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.io.Serializable;

/**
 * Created by chenl on 2017/4/24.
 */

public class AssignedStorageData extends BaseData {

    private AssignedStorageResult data;

    public AssignedStorageResult getData() {
        return data;
    }

    public void setData(AssignedStorageResult data) {
        this.data = data;
    }
}
