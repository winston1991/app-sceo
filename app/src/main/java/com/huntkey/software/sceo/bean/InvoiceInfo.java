package com.huntkey.software.sceo.bean;

import java.util.List;

public class InvoiceInfo {

	private PageData page;
	private List<InvoiceDetails> list;
	
	public PageData getPage() {
		return page;
	}
	public void setPage(PageData page) {
		this.page = page;
	}
	public List<InvoiceDetails> getList() {
		return list;
	}
	public void setList(List<InvoiceDetails> list) {
		this.list = list;
	}
	
	
}
