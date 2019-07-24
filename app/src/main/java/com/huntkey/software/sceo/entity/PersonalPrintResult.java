package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.PageData;

import java.util.List;

/**
 * Created by chenl on 2017/4/12.
 */

public class PersonalPrintResult {

    private List<PersonalPrintItem> results;
    private PageData page;

    public List<PersonalPrintItem> getResults() {
        return results;
    }

    public void setResults(List<PersonalPrintItem> results) {
        this.results = results;
    }

    public PageData getPage() {
        return page;
    }

    public void setPage(PageData page) {
        this.page = page;
    }
}
