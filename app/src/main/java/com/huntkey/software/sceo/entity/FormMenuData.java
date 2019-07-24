package com.huntkey.software.sceo.entity;

import java.util.List;

/**
 * Created by chenl on 2018/7/18.
 */

public class FormMenuData {

    private List<FormMenuItem> Rows;
    private int Total;

    public List<FormMenuItem> getRows() {
        return Rows;
    }

    public void setRows(List<FormMenuItem> rows) {
        Rows = rows;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }
}
