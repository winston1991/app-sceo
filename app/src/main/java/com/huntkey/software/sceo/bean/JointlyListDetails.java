package com.huntkey.software.sceo.bean;

import java.io.Serializable;

public class JointlyListDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String emp_id;
	private String emp_name;
	private String wfna_id;
	private String photo;
	
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
	public String getWfna_id() {
		return wfna_id;
	}
	public void setWfna_id(String wfna_id) {
		this.wfna_id = wfna_id;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	
}
