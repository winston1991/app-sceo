package com.huntkey.software.sceo.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by chenl on 2017/4/7.
 */

public class HomeMenuItem extends RealmObject {

    @PrimaryKey
    private int modt_id;
    private String modt_code;
    private String modt_name;
    private int modt_parent_id;
    private int modt_group;
    private String modt_url;
    private String modt_taskbar;
    private String modt_icon_path;
    private String modt_win;
    private int modt_warn;
    private String authority;
    private String modt_syscode;
    private int level;

    public int getModt_id() {
        return modt_id;
    }

    public void setModt_id(int modt_id) {
        this.modt_id = modt_id;
    }

    public String getModt_code() {
        return modt_code;
    }

    public void setModt_code(String modt_code) {
        this.modt_code = modt_code;
    }

    public String getModt_name() {
        return modt_name;
    }

    public void setModt_name(String modt_name) {
        this.modt_name = modt_name;
    }

    public int getModt_parent_id() {
        return modt_parent_id;
    }

    public void setModt_parent_id(int modt_parent_id) {
        this.modt_parent_id = modt_parent_id;
    }

    public int getModt_group() {
        return modt_group;
    }

    public void setModt_group(int modt_group) {
        this.modt_group = modt_group;
    }

    public String getModt_url() {
        return modt_url;
    }

    public void setModt_url(String modt_url) {
        this.modt_url = modt_url;
    }

    public String getModt_taskbar() {
        return modt_taskbar;
    }

    public void setModt_taskbar(String modt_taskbar) {
        this.modt_taskbar = modt_taskbar;
    }

    public String getModt_icon_path() {
        return modt_icon_path;
    }

    public void setModt_icon_path(String modt_icon_path) {
        this.modt_icon_path = modt_icon_path;
    }

    public String getModt_win() {
        return modt_win;
    }

    public void setModt_win(String modt_win) {
        this.modt_win = modt_win;
    }

    public int getModt_warn() {
        return modt_warn;
    }

    public void setModt_warn(int modt_warn) {
        this.modt_warn = modt_warn;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    public String getModt_syscode() {
        return modt_syscode;
    }

    public void setModt_syscode(String modt_syscode) {
        this.modt_syscode = modt_syscode;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
