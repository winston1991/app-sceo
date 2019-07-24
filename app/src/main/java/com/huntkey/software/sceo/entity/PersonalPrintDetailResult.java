package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.PageData;

import java.util.List;

/**
 * Created by chenl on 2017/4/13.
 */

public class PersonalPrintDetailResult {

    private PageData page;
    private List<PersonalPrintDetailItem> results;

    public PageData getPage() {
        return page;
    }

    public void setPage(PageData page) {
        this.page = page;
    }

    public List<PersonalPrintDetailItem> getResults() {
        return results;
    }

    public void setResults(List<PersonalPrintDetailItem> results) {
        this.results = results;
    }
}
