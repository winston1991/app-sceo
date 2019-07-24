package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.bean.PageData;

/**
 * Created by chenl on 2017/4/12.
 */

public class PersonalPrintData extends BaseData {

    private PersonalPrintResult data;

    public PersonalPrintResult getData() {
        return data;
    }

    public void setData(PersonalPrintResult data) {
        this.data = data;
    }
}
