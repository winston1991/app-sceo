package com.huntkey.software.sceo.bean;

import java.util.List;

public class EmpDetailsInfo {

	private String emp_name;
	private String emp_id;
	private int ismyfriend;//0表示非常用联系人，1表示是常用联系人
	private String photo;
	private int leaflag;//1表示已离职
	private List<EmpDetailsWorkInfo> workinfo;
	private List<EmpDetailsTelInfo> telinfo;
	
	public String getEmp_name() {
		return emp_name;
	}
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public int getIsmyfriend() {
		return ismyfriend;
	}
	public void setIsmyfriend(int ismyfriend) {
		this.ismyfriend = ismyfriend;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public List<EmpDetailsWorkInfo> getWorkinfo() {
		return workinfo;
	}
	public void setWorkinfo(List<EmpDetailsWorkInfo> workinfo) {
		this.workinfo = workinfo;
	}
	public List<EmpDetailsTelInfo> getTelinfo() {
		return telinfo;
	}
	public void setTelinfo(List<EmpDetailsTelInfo> telinfo) {
		this.telinfo = telinfo;
	}
	public int getLeaflag() {
		return leaflag;
	}
	public void setLeaflag(int leaflag) {
		this.leaflag = leaflag;
	}
	
}
