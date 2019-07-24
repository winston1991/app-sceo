package com.huntkey.software.sceo.bean;

import java.util.List;

public class LeaveInitInfo {

	private String emp_id;
	private String emp_name;
	private String cc_code;
	private String cc_desc;//部门
	private String bz_desc;//职位
	private String approval_date;//
	private String begdate;//年假区间开始时间
	private String enddate;//年假区间结束时间
	private String xynj_day;//享有年假天数
	private String xynj_hour;//享有年假时长
	private String yxnj_hour;//已休年假时长
	private String synj_hour;//剩余年假时长
	private String yqj_day;//已请假天数
	private List<LeaveInitType> lerm_types;//请假类型
	private List<String> adlt_codes;//请假类型为该list中的类型的时候显示加班工时，否则不显示；类型为产假时只显示请假天数
	
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
	public String getCc_code() {
		return cc_code;
	}
	public void setCc_code(String cc_code) {
		this.cc_code = cc_code;
	}
	public String getCc_desc() {
		return cc_desc;
	}
	public void setCc_desc(String cc_desc) {
		this.cc_desc = cc_desc;
	}
	public String getBz_desc() {
		return bz_desc;
	}
	public void setBz_desc(String bz_desc) {
		this.bz_desc = bz_desc;
	}
	public String getApproval_date() {
		return approval_date;
	}
	public void setApproval_date(String approval_date) {
		this.approval_date = approval_date;
	}
	public String getBegdate() {
		return begdate;
	}
	public void setBegdate(String begdate) {
		this.begdate = begdate;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getXynj_day() {
		return xynj_day;
	}
	public void setXynj_day(String xynj_day) {
		this.xynj_day = xynj_day;
	}
	public String getXynj_hour() {
		return xynj_hour;
	}
	public void setXynj_hour(String xynj_hour) {
		this.xynj_hour = xynj_hour;
	}
	public String getYxnj_hour() {
		return yxnj_hour;
	}
	public void setYxnj_hour(String yxnj_hour) {
		this.yxnj_hour = yxnj_hour;
	}
	public String getSynj_hour() {
		return synj_hour;
	}
	public void setSynj_hour(String synj_hour) {
		this.synj_hour = synj_hour;
	}
	public String getYqj_day() {
		return yqj_day;
	}
	public void setYqj_day(String yqj_day) {
		this.yqj_day = yqj_day;
	}
	public List<LeaveInitType> getLerm_types() {
		return lerm_types;
	}
	public void setLerm_types(List<LeaveInitType> lerm_types) {
		this.lerm_types = lerm_types;
	}
	public List<String> getAdlt_codes() {
		return adlt_codes;
	}
	public void setAdlt_codes(List<String> adlt_codes) {
		this.adlt_codes = adlt_codes;
	}
	
	
}
