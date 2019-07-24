package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenl on 2017/4/10.
 */

public class ExternalBarConversionBData extends BaseData implements Serializable {

    private List<ExternalBarConversionBItem> data;

    public List<ExternalBarConversionBItem> getData() {
        return data;
    }

    public void setData(List<ExternalBarConversionBItem> data) {
        this.data = data;
    }
}
