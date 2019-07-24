package com.huntkey.software.sceo.bean;

import java.util.List;

public class AffairsInfo {

	private PageData page;
	private List<AffairsDetails> tasklist;
	
	public PageData getPage() {
		return page;
	}
	public void setPage(PageData page) {
		this.page = page;
	}
	public List<AffairsDetails> getTasklist() {
		return tasklist;
	}
	public void setTasklist(List<AffairsDetails> tasklist) {
		this.tasklist = tasklist;
	}
	
}
