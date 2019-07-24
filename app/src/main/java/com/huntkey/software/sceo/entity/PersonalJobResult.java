package com.huntkey.software.sceo.entity;

import com.huntkey.software.sceo.bean.PageData;

import java.util.List;

/**
 * Created by chenl on 2017/4/20.
 */

public class PersonalJobResult {

    private PageData page;
    private List<PersonalJobItem> results;

    public List<PersonalJobItem> getResults() {
        return results;
    }

    public void setResults(List<PersonalJobItem> results) {
        this.results = results;
    }

    public PageData getPage() {
        return page;
    }

    public void setPage(PageData page) {
        this.page = page;
    }
}
