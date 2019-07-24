package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/9/20.
 */

public class AttenceInfoData extends BaseData {

    private List<AttenceInfoItem> data;

    public List<AttenceInfoItem> getData() {
        return data;
    }

    public void setData(List<AttenceInfoItem> data) {
        this.data = data;
    }
}
