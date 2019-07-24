package com.huntkey.software.sceo.bean;

public class QueryDetails {

	private int group;//按组分开，一组只能选一个
	private int key;//id
	private String value;
	private int checked;//1为默认选中，0为默认未选中
	
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getChecked() {
		return checked;
	}
	public void setChecked(int checked) {
		this.checked = checked;
	}
	
}
