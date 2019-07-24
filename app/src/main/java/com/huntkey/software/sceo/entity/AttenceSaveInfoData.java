package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

/**
 * Created by chenl on 2017/9/21.
 */

public class AttenceSaveInfoData extends BaseData {

    private AttenceSaveInfoItem data;

    public AttenceSaveInfoItem getData() {
        return data;
    }

    public void setData(AttenceSaveInfoItem data) {
        this.data = data;
    }
}
