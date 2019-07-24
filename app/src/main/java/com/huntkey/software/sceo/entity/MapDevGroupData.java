package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/9/12.
 */

public class MapDevGroupData extends BaseData {

    private List<MapDevGroupItem> data;

    public List<MapDevGroupItem> getData() {
        return data;
    }

    public void setData(List<MapDevGroupItem> data) {
        this.data = data;
    }
}
