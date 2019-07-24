package com.huntkey.software.sceo.bean;

import java.util.List;

public class FormWeekData {

	private List<FormWeekDetails> Rows;
	private int Total;
	
	public List<FormWeekDetails> getRows() {
		return Rows;
	}
	public void setRows(List<FormWeekDetails> rows) {
		Rows = rows;
	}
	public int getTotal() {
		return Total;
	}
	public void setTotal(int total) {
		Total = total;
	}
	
}
