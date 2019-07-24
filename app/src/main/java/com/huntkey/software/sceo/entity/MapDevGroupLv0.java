package com.huntkey.software.sceo.entity;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by chenl on 2017/9/12.
 */

public class MapDevGroupLv0 extends AbstractExpandableItem<MapDevGroupLv1> implements MultiItemEntity {

    public MapDevGroupLv0(String adeg_code, String adeg_name, String adeg_level) {
        this.adeg_code = adeg_code;
        this.adeg_name = adeg_name;
        this.adeg_level = adeg_level;
    }

    private String adeg_code;
    private String adeg_name;
    private String adeg_level;

    public String getAdeg_code() {
        return adeg_code;
    }

    public void setAdeg_code(String adeg_code) {
        this.adeg_code = adeg_code;
    }

    public String getAdeg_name() {
        return adeg_name;
    }

    public void setAdeg_name(String adeg_name) {
        this.adeg_name = adeg_name;
    }

    public String getAdeg_level() {
        return adeg_level;
    }

    public void setAdeg_level(String adeg_level) {
        this.adeg_level = adeg_level;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return 0;
    }
}
