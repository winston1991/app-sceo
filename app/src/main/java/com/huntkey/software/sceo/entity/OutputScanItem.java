package com.huntkey.software.sceo.entity;

/**
 * Created by chenl on 2017/4/28.
 */

public class OutputScanItem {

    private String tskp_ref;
    private String tskp_tskm_nbr;
    private String tskm_ref_nbr;
    private String tskp_part;
    private String tskp_lot;
    private String tskp_op_code;
    private String tskp_qty;
    private String tskp_qty_check;//父页面为已扫量 子页面为待扫量
    private String tskp_qty_o;//捡料数
    private String ld_qty;//库存数
    private String tskp_id;
    private String tskp_status;
    private String tskp_op_code_desc = "";
    private String num;

    public String getTskp_ref() {
        return tskp_ref;
    }

    public void setTskp_ref(String tskp_ref) {
        this.tskp_ref = tskp_ref;
    }

    public String getTskp_tskm_nbr() {
        return tskp_tskm_nbr;
    }

    public void setTskp_tskm_nbr(String tskp_tskm_nbr) {
        this.tskp_tskm_nbr = tskp_tskm_nbr;
    }

    public String getTskm_ref_nbr() {
        return tskm_ref_nbr;
    }

    public void setTskm_ref_nbr(String tskm_ref_nbr) {
        this.tskm_ref_nbr = tskm_ref_nbr;
    }

    public String getTskp_part() {
        return tskp_part;
    }

    public void setTskp_part(String tskp_part) {
        this.tskp_part = tskp_part;
    }

    public String getTskp_lot() {
        return tskp_lot;
    }

    public void setTskp_lot(String tskp_lot) {
        this.tskp_lot = tskp_lot;
    }

    public String getTskp_op_code() {
        return tskp_op_code;
    }

    public void setTskp_op_code(String tskp_op_code) {
        this.tskp_op_code = tskp_op_code;
    }

    public String getTskp_qty() {
        return tskp_qty;
    }

    public void setTskp_qty(String tskp_qty) {
        this.tskp_qty = tskp_qty;
    }

    public String getTskp_id() {
        return tskp_id;
    }

    public void setTskp_id(String tskp_id) {
        this.tskp_id = tskp_id;
    }

    public String getTskp_status() {
        return tskp_status;
    }

    public void setTskp_status(String tskp_status) {
        this.tskp_status = tskp_status;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTskp_op_code_desc() {
        return tskp_op_code_desc;
    }

    public void setTskp_op_code_desc(String tskp_op_code_desc) {
        this.tskp_op_code_desc = tskp_op_code_desc;
    }

    public String getLd_qty() {
        return ld_qty;
    }

    public void setLd_qty(String ld_qty) {
        this.ld_qty = ld_qty;
    }

    public String getTskp_qty_o() {
        return tskp_qty_o;
    }

    public void setTskp_qty_o(String tskp_qty_o) {
        this.tskp_qty_o = tskp_qty_o;
    }

    public String getTskp_qty_check() {
        return tskp_qty_check;
    }

    public void setTskp_qty_check(String tskp_qty_check) {
        this.tskp_qty_check = tskp_qty_check;
    }
}
