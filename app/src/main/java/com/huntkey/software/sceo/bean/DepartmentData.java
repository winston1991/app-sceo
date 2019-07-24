package com.huntkey.software.sceo.bean;

import java.util.List;

public class DepartmentData extends BaseData {

	private List<DepartmentDetails> data;

	public List<DepartmentDetails> getData() {
		return data;
	}

	public void setData(List<DepartmentDetails> data) {
		this.data = data;
	}
	
}
