package com.huntkey.software.sceo.bean;

import java.util.List;

public class FormQuarterData {

	private List<FormQuarterDetails> Rows;
	private int Total;
	
	public List<FormQuarterDetails> getRows() {
		return Rows;
	}
	public void setRows(List<FormQuarterDetails> rows) {
		Rows = rows;
	}
	public int getTotal() {
		return Total;
	}
	public void setTotal(int total) {
		Total = total;
	}
	
}
