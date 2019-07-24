package com.huntkey.software.sceo.entity;

import java.io.Serializable;

/**
 * Created by chenl on 2017/5/18.
 */

public class InputPickingNumItem implements Serializable {

    private String lad_warehouse_by;
    private String emp_name;
    private boolean choosed;

    public String getLad_warehouse_by() {
        return lad_warehouse_by;
    }

    public void setLad_warehouse_by(String lad_warehouse_by) {
        this.lad_warehouse_by = lad_warehouse_by;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public boolean isChoosed() {
        return choosed;
    }

    public void setChoosed(boolean choosed) {
        this.choosed = choosed;
    }
}
