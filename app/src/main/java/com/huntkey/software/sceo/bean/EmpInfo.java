package com.huntkey.software.sceo.bean;

public class EmpInfo {

	private String usertype;//暂时先不管（为了后续扩展）
	private String usercode;//暂时先不管（为了后续扩展）
	private String sessionkey;
	private String emp_id;//以员工号为id
	private String emp_name;
	private String emp_photo;
	private String apptimeout;
	private String upper_code;//起始部门id
	private String upper_desc;//起始部门名称
	private String include;//是否包含下级
	private String appflag = "0";//用于判断app是否绑定手机（为1绑定手机跳转到MainActivity，为0没有绑定跳转到HomeActivity）
	
	public String getUsertype() {
		return usertype;
	}
	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}
	public String getUsercode() {
		return usercode;
	}
	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}
	public String getSessionkey() {
		return sessionkey;
	}
	public void setSessionkey(String sessionkey) {
		this.sessionkey = sessionkey;
	}
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
	public String getEmp_photo() {
		return emp_photo;
	}
	public void setEmp_photo(String emp_photo) {
		this.emp_photo = emp_photo;
	}
	public String getApptimeout() {
		return apptimeout;
	}
	public void setApptimeout(String apptimeout) {
		this.apptimeout = apptimeout;
	}
	public String getUpper_code() {
		if (upper_code == null) {
			upper_code = "";
		}
		return upper_code;
	}
	public void setUpper_code(String upper_code) {
		this.upper_code = upper_code;
	}
	public String getUpper_desc() {
		if (upper_desc == null) {
			upper_desc = "";
		}
		return upper_desc;
	}
	public void setUpper_desc(String upper_desc) {
		this.upper_desc = upper_desc;
	}
	public String getInclude() {
		if (include == null) {
			include = "";
		}
		return include;
	}
	public void setInclude(String include) {
		this.include = include;
	}


	public String getAppflag() {
		return appflag;
	}

	public void setAppflag(String appflag) {
		this.appflag = appflag;
	}
}
