package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2018/2/8.
 */

public class StorageQueryItem implements Serializable {

    private String in_part;
    private String pt_desc;
    private String in_site;
    private String in_qty_oh;
    private String in_qty_avail;
    private String in_qty_pick;
    private int num;

    public String getIn_part() {
        return in_part;
    }

    public void setIn_part(String in_part) {
        this.in_part = in_part;
    }

    public String getPt_desc() {
        return pt_desc;
    }

    public void setPt_desc(String pt_desc) {
        this.pt_desc = pt_desc;
    }

    public String getIn_site() {
        return in_site;
    }

    public void setIn_site(String in_site) {
        this.in_site = in_site;
    }

    public String getIn_qty_oh() {
        return in_qty_oh;
    }

    public void setIn_qty_oh(String in_qty_oh) {
        this.in_qty_oh = in_qty_oh;
    }

    public String getIn_qty_avail() {
        return in_qty_avail;
    }

    public void setIn_qty_avail(String in_qty_avail) {
        this.in_qty_avail = in_qty_avail;
    }

    public String getIn_qty_pick() {
        return in_qty_pick;
    }

    public void setIn_qty_pick(String in_qty_pick) {
        this.in_qty_pick = in_qty_pick;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
