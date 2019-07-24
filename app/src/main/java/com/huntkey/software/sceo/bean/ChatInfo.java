package com.huntkey.software.sceo.bean;

import java.util.List;

public class ChatInfo {

	private PageData page;
	private List<ChatDetails> taskinfo;
	
	public PageData getPage() {
		return page;
	}
	public void setPage(PageData page) {
		this.page = page;
	}
	public List<ChatDetails> getTaskinfo() {
		return taskinfo;
	}
	public void setTaskinfo(List<ChatDetails> taskinfo) {
		this.taskinfo = taskinfo;
	}
	
	
}
