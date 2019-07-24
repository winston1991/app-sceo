package com.huntkey.software.sceo.bean;

public class InvoiceFlowDetails {

	private String wfnd_stepname;//步骤
	private String audit_state;//审核状态 0未审  1已审
	private String wfnd_name;//岗位/部门
	private String audit_type;//审核属性
	private String wfnd_emp_name;//操作人
	private String wfna_phone;//是否手机审单 0pc 1手机
	private String flowstate;//审核结果
	private String audit_time;//操作时间
	private String remark;//审核意见
	private String stepname_show;//是否显示步骤名称
	
	public String getWfnd_stepname() {
		return wfnd_stepname;
	}
	public void setWfnd_stepname(String wfnd_stepname) {
		this.wfnd_stepname = wfnd_stepname;
	}
	public String getAudit_state() {
		return audit_state;
	}
	public void setAudit_state(String audit_state) {
		this.audit_state = audit_state;
	}
	public String getWfnd_name() {
		return wfnd_name;
	}
	public void setWfnd_name(String wfnd_name) {
		this.wfnd_name = wfnd_name;
	}
	public String getAudit_type() {
		return audit_type;
	}
	public void setAudit_type(String audit_type) {
		this.audit_type = audit_type;
	}
	public String getWfnd_emp_name() {
		return wfnd_emp_name;
	}
	public void setWfnd_emp_name(String wfnd_emp_name) {
		this.wfnd_emp_name = wfnd_emp_name;
	}
	public String getWfna_phone() {
		return wfna_phone;
	}
	public void setWfna_phone(String wfna_phone) {
		this.wfna_phone = wfna_phone;
	}
	public String getFlowstate() {
		return flowstate;
	}
	public void setFlowstate(String flowstate) {
		this.flowstate = flowstate;
	}
	public String getAudit_time() {
		return audit_time;
	}
	public void setAudit_time(String audit_time) {
		this.audit_time = audit_time;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getStepname_show() {
		return stepname_show;
	}
	public void setStepname_show(String stepname_show) {
		this.stepname_show = stepname_show;
	}
	
}
