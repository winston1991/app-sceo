package com.huntkey.software.sceo.entity;

import java.util.List;

/**
 * Created by chenl on 2017/4/7.
 */

public class HomeMenuData {

    private List<HomeMenuItem> Rows;
    private int Total;
    private int Rights;//软键盘使用权限 >0有权限，=0无权限

    public List<HomeMenuItem> getRows() {
        return Rows;
    }

    public void setRows(List<HomeMenuItem> rows) {
        Rows = rows;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }

    public int getRights() {
        return Rights;
    }

    public void setRights(int rights) {
        Rights = rights;
    }
}
