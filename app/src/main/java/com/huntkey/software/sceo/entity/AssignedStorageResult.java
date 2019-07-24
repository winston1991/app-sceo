package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.PageData;

import java.util.List;

/**
 * Created by chenl on 2017/4/24.
 */

public class AssignedStorageResult {

    private PageData page;
    private List<AssignedStorageItem> results;

    public PageData getPage() {
        return page;
    }

    public void setPage(PageData page) {
        this.page = page;
    }

    public List<AssignedStorageItem> getResults() {
        return results;
    }

    public void setResults(List<AssignedStorageItem> results) {
        this.results = results;
    }
}
