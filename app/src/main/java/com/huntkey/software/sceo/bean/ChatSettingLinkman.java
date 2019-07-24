package com.huntkey.software.sceo.bean;

import java.io.Serializable;

public class ChatSettingLinkman implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String emp_id;
	private String emp_name;
	private int raiseflag;//是否是事务发起人，1表示是，0表示不是
	private String emp_photo;
	
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getEmp_name() {
		return emp_name;
	}
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	public int getRaiseflag() {
		return raiseflag;
	}
	public void setRaiseflag(int raiseflag) {
		this.raiseflag = raiseflag;
	}
	public String getEmp_photo() {
		return emp_photo;
	}
	public void setEmp_photo(String emp_photo) {
		this.emp_photo = emp_photo;
	}
	
}
