package com.huntkey.software.sceo.bean;

import java.io.Serializable;

public class LeaveInitType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String adlt_code;//编号
	private String adlt_name;//名称
	
	public String getAdlt_code() {
		return adlt_code;
	}
	public void setAdlt_code(String adlt_code) {
		this.adlt_code = adlt_code;
	}
	public String getAdlt_name() {
		return adlt_name;
	}
	public void setAdlt_name(String adlt_name) {
		this.adlt_name = adlt_name;
	}
	
	
}
