package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.PageData;

import java.util.List;

/**
 * Created by chenl on 2017/5/16.
 */

public class WarehosLv0Result {

    private PageData page;
    private List<WarehosLv0Item> results;

    public PageData getPage() {
        return page;
    }

    public void setPage(PageData page) {
        this.page = page;
    }

    public List<WarehosLv0Item> getResults() {
        return results;
    }

    public void setResults(List<WarehosLv0Item> results) {
        this.results = results;
    }
}
