package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.BaseData;

import java.util.List;

/**
 * Created by chenl on 2017/9/21.
 */

public class AttenceCardMachineData extends BaseData {

    private List<AttenceCardMachineItem> data;

    public List<AttenceCardMachineItem> getData() {
        return data;
    }

    public void setData(List<AttenceCardMachineItem> data) {
        this.data = data;
    }
}
