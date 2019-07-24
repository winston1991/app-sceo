package com.huntkey.software.sceo.entity;

/**
 * Created by chenl on 2018/7/18.
 */

public class FormMenuItem {

    private String kmac_class;
    private String kmam_code;
    private String kmam_desc;
    private String kmam_rmks;
    private int issys;

    public String getKmac_class() {
        return kmac_class;
    }

    public void setKmac_class(String kmac_class) {
        this.kmac_class = kmac_class;
    }

    public String getKmam_code() {
        return kmam_code;
    }

    public void setKmam_code(String kmam_code) {
        this.kmam_code = kmam_code;
    }

    public String getKmam_desc() {
        return kmam_desc;
    }

    public void setKmam_desc(String kmam_desc) {
        this.kmam_desc = kmam_desc;
    }

    public String getKmam_rmks() {
        return kmam_rmks;
    }

    public void setKmam_rmks(String kmam_rmks) {
        this.kmam_rmks = kmam_rmks;
    }

    public int getIssys() {
        return issys;
    }

    public void setIssys(int issys) {
        this.issys = issys;
    }
}
