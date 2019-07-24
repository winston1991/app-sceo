package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

/**
 * Created by chenl on 2017/12/14.
 */

public class OutputPickData extends BaseData {

    private OutputPickItem data;

    public OutputPickItem getData() {
        return data;
    }

    public void setData(OutputPickItem data) {
        this.data = data;
    }
}
