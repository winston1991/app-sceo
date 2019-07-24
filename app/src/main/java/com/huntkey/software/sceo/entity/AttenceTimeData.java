package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

/**
 * Created by chenl on 2017/9/21.
 */

public class AttenceTimeData extends BaseData {

    private AttenceTimeItem data;

    public AttenceTimeItem getData() {
        return data;
    }

    public void setData(AttenceTimeItem data) {
        this.data = data;
    }
}
