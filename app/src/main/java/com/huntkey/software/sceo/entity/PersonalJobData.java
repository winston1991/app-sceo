package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

/**
 * Created by chenl on 2017/4/20.
 */

public class PersonalJobData extends BaseData {

    private PersonalJobResult data;

    public PersonalJobResult getData() {
        return data;
    }

    public void setData(PersonalJobResult data) {
        this.data = data;
    }
}
