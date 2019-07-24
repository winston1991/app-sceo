package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

/**
 * Created by chenl on 2017/4/13.
 */

public class PersonalPrintDetailData extends BaseData {

    private PersonalPrintDetailResult data;

    public PersonalPrintDetailResult getData() {
        return data;
    }

    public void setData(PersonalPrintDetailResult data) {
        this.data = data;
    }
}
