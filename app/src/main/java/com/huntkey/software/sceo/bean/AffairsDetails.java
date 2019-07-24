package com.huntkey.software.sceo.bean;

public class AffairsDetails {

	private int empcount;//人数
	private int taskid;//item条目的id
	private String taskname;
	private int unreadno;//未读数
	private String emp_photo;//若有多张照片，使用";"分割
	private String emp_name;
	private String tasktime;//时间
	private String last_rep_emp;//最后一个回复人
	private String lasttask;//最后一条会话
	
	public int getEmpcount() {
		return empcount;
	}
	public void setEmpcount(int empcount) {
		this.empcount = empcount;
	}
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public int getUnreadno() {
		return unreadno;
	}
	public void setUnreadno(int unreadno) {
		this.unreadno = unreadno;
	}
	public String getEmp_photo() {
		return emp_photo;
	}
	public void setEmp_photo(String emp_photo) {
		this.emp_photo = emp_photo;
	}
	public String getEmp_name() {
		return emp_name;
	}
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	public String getTasktime() {
		return tasktime;
	}
	public void setTasktime(String tasktime) {
		this.tasktime = tasktime;
	}
	public String getLasttask() {
		return lasttask;
	}
	public void setLasttask(String lasttask) {
		this.lasttask = lasttask;
	}
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}
	public String getLast_rep_emp() {
		return last_rep_emp;
	}
	public void setLast_rep_emp(String last_rep_emp) {
		this.last_rep_emp = last_rep_emp;
	}
	
	
}
