package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/5/18.
 */

public class InputPickingNumData extends BaseData {

    private List<InputPickingNumItem> data;

    public List<InputPickingNumItem> getData() {
        return data;
    }

    public void setData(List<InputPickingNumItem> data) {
        this.data = data;
    }
}
