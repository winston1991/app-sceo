package com.huntkey.software.sceo.entity;

/**
 * Created by chenl on 2017/4/20.
 */

public class PersonalJobItem {

    private String tskm_dept;
    private String tskm_type;
    private String tskm_ref_nbr;
    private String tskm_nbr_group;
    private String tskm_plan_date;
    private String tskm_plan_time;
    private String tskm_id;
    private String tskm_union;//0未合并 1已合并
    private String lstm_type;
    private String tskm_char;
    private String tskm_char_name;
    private boolean checked = false;
    private String group = "0";

    public String getTskm_dept() {
        return tskm_dept;
    }

    public void setTskm_dept(String tskm_dept) {
        this.tskm_dept = tskm_dept;
    }

    public String getTskm_type() {
        return tskm_type;
    }

    public void setTskm_type(String tskm_type) {
        this.tskm_type = tskm_type;
    }

    public String getTskm_ref_nbr() {
        return tskm_ref_nbr;
    }

    public void setTskm_ref_nbr(String tskm_ref_nbr) {
        this.tskm_ref_nbr = tskm_ref_nbr;
    }

    public String getTskm_nbr_group() {
        return tskm_nbr_group;
    }

    public void setTskm_nbr_group(String tskm_nbr_group) {
        this.tskm_nbr_group = tskm_nbr_group;
    }

    public String getTskm_plan_date() {
        return tskm_plan_date;
    }

    public void setTskm_plan_date(String tskm_plan_date) {
        this.tskm_plan_date = tskm_plan_date;
    }

    public String getTskm_plan_time() {
        return tskm_plan_time;
    }

    public void setTskm_plan_time(String tskm_plan_time) {
        this.tskm_plan_time = tskm_plan_time;
    }

    public String getTskm_id() {
        return tskm_id;
    }

    public void setTskm_id(String tskm_id) {
        this.tskm_id = tskm_id;
    }

    public String getTskm_union() {
        return tskm_union;
    }

    public void setTskm_union(String tskm_union) {
        this.tskm_union = tskm_union;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getLstm_type() {
        return lstm_type;
    }

    public void setLstm_type(String lstm_type) {
        this.lstm_type = lstm_type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTskm_char() {
        return tskm_char;
    }

    public void setTskm_char(String tskm_char) {
        this.tskm_char = tskm_char;
    }

    public String getTskm_char_name() {
        return tskm_char_name;
    }

    public void setTskm_char_name(String tskm_char_name) {
        this.tskm_char_name = tskm_char_name;
    }
}
