package com.huntkey.software.sceo.bean;

public class BusinessInitInfo {

	private String emp_id;
	private String emp_name;
	private String cc_code;
	private String cc_desc;
	private String bz_desc;
	private String ifovleave;//为0时不显示加班工时
	private String bsns_ppi_right;//为0时，业务费相关信息隐藏，只显示业务费个人已报额
	private String travel_ppi_right;//为0时，差旅相关信息隐藏，只显示差旅费个人已报额
	private String approval_date;
	private String fyear_times;
	private String fyear_days;
	
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
	public String getIfovleave() {
		return ifovleave;
	}
	public void setIfovleave(String ifovleave) {
		this.ifovleave = ifovleave;
	}
	public String getBsns_ppi_right() {
		return bsns_ppi_right;
	}
	public void setBsns_ppi_right(String bsns_ppi_right) {
		this.bsns_ppi_right = bsns_ppi_right;
	}
	public String getTravel_ppi_right() {
		return travel_ppi_right;
	}
	public void setTravel_ppi_right(String travel_ppi_right) {
		this.travel_ppi_right = travel_ppi_right;
	}
	public String getApproval_date() {
		return approval_date;
	}
	public void setApproval_date(String approval_date) {
		this.approval_date = approval_date;
	}
	public String getFyear_times() {
		return fyear_times;
	}
	public void setFyear_times(String fyear_times) {
		this.fyear_times = fyear_times;
	}
	public String getFyear_days() {
		return fyear_days;
	}
	public void setFyear_days(String fyear_days) {
		this.fyear_days = fyear_days;
	}
	
	
}
