package com.huntkey.software.sceo.bean;

public class FormWeekAndQuarterXDetails {

	private String name;//名称
	private int tp;//tp=2为周的x轴数据，tp=3为季度的x轴数据
	private String dates;//周日期
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTp() {
		return tp;
	}
	public void setTp(int tp) {
		this.tp = tp;
	}
	public String getDates() {
		return dates;
	}
	public void setDates(String dates) {
		this.dates = dates;
	}
	
}
