package com.huntkey.software.sceo.bean;

import java.util.List;

public class FormWeekAndQuarterXData {

	private List<FormWeekAndQuarterXDetails> Rows;
	private int Total;
	
	public List<FormWeekAndQuarterXDetails> getRows() {
		return Rows;
	}
	public void setRows(List<FormWeekAndQuarterXDetails> rows) {
		Rows = rows;
	}
	public int getTotal() {
		return Total;
	}
	public void setTotal(int total) {
		Total = total;
	}
	
}
