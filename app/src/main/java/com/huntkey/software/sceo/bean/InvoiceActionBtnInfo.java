package com.huntkey.software.sceo.bean;

import java.util.List;

public class InvoiceActionBtnInfo {

	private List<InvoiceActionBtnDetails> btnlist;
	private int stepid;
	private String empname;
	private String color;

	public List<InvoiceActionBtnDetails> getBtnlist() {
		return btnlist;
	}

	public void setBtnlist(List<InvoiceActionBtnDetails> btnlist) {
		this.btnlist = btnlist;
	}

	public int getStepid() {
		return stepid;
	}

	public void setStepid(int stepid) {
		this.stepid = stepid;
	}

	public String getEmpname() {
		return empname;
	}

	public void setEmpname(String empname) {
		this.empname = empname;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	
}
