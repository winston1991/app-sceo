package com.huntkey.software.sceo.bean;

import java.util.List;

public class FormYearData {

	private List<FormYearDetails> Rows;
	private int Total;
	
	public List<FormYearDetails> getRows() {
		return Rows;
	}
	public void setRows(List<FormYearDetails> rows) {
		Rows = rows;
	}
	public int getTotal() {
		return Total;
	}
	public void setTotal(int total) {
		Total = total;
	}
	
}
