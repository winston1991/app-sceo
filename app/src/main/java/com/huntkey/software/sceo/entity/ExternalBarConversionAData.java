package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by chenl on 2017/4/10.
 */

public class ExternalBarConversionAData extends BaseData implements Serializable {

    private List<ExternalBarConversionAItem> data;

    public List<ExternalBarConversionAItem> getData() {
        return data;
    }

    public void setData(List<ExternalBarConversionAItem> data) {
        this.data = data;
    }
}
