package com.huntkey.software.sceo.bean;

public class FormWeekDetails {

	private String cc_desc;//部门名称
	private String ffl;//统计值
	private int fweek;//当前周的前一周
	private String p_ffl;//同比值
	private String ppid_time;//根据该值判断当前数据是否为周数据(FW开头的即为周数据)
	private String ruler;//规划值
	private String ruler_name;//规划值名称
	
	public String getCc_desc() {
		return cc_desc;
	}
	public void setCc_desc(String cc_desc) {
		this.cc_desc = cc_desc;
	}
	public String getFfl() {
		return ffl;
	}
	public void setFfl(String ffl) {
		this.ffl = ffl;
	}
	public int getFweek() {
		return fweek;
	}
	public void setFweek(int fweek) {
		this.fweek = fweek;
	}
	public String getP_ffl() {
		return p_ffl;
	}
	public void setP_ffl(String p_ffl) {
		this.p_ffl = p_ffl;
	}
	public String getPpid_time() {
		return ppid_time;
	}
	public void setPpid_time(String ppid_time) {
		this.ppid_time = ppid_time;
	}
	public String getRuler() {
		return ruler;
	}
	public void setRuler(String ruler) {
		this.ruler = ruler;
	}
	public String getRuler_name() {
		return ruler_name;
	}
	public void setRuler_name(String ruler_name) {
		this.ruler_name = ruler_name;
	}
	
}
