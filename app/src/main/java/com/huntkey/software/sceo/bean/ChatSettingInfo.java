package com.huntkey.software.sceo.bean;

import java.util.List;

public class ChatSettingInfo {

	private ChatSettingDetails tasksetinfo;
	private List<ChatSettingLinkman> emplist;
	
	public ChatSettingDetails getTasksetinfo() {
		return tasksetinfo;
	}
	public void setTasksetinfo(ChatSettingDetails tasksetinfo) {
		this.tasksetinfo = tasksetinfo;
	}
	public List<ChatSettingLinkman> getEmplist() {
		return emplist;
	}
	public void setEmplist(List<ChatSettingLinkman> emplist) {
		this.emplist = emplist;
	}
	
	
}
