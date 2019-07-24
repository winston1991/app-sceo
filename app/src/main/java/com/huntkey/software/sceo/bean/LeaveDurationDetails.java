package com.huntkey.software.sceo.bean;

public class LeaveDurationDetails {

	private String qj_day;//请假天数-产假才显示
	private String zb_hour;//正班工时
	private String jb_hour;//加班工时
	private String zb_day;//正班天数
	private String bgdate_affect;
	private String eddate_affect;
	private String bgtime;//开始时间
	private String edtime;//结束时间
	private String timepart;//班制信息
	
	public String getQj_day() {
		return qj_day;
	}
	public void setQj_day(String qj_day) {
		this.qj_day = qj_day;
	}
	public String getZb_hour() {
		return zb_hour;
	}
	public void setZb_hour(String zb_hour) {
		this.zb_hour = zb_hour;
	}
	public String getJb_hour() {
		return jb_hour;
	}
	public void setJb_hour(String jb_hour) {
		this.jb_hour = jb_hour;
	}
	public String getZb_day() {
		return zb_day;
	}
	public void setZb_day(String zb_day) {
		this.zb_day = zb_day;
	}
	public String getBgdate_affect() {
		return bgdate_affect;
	}
	public void setBgdate_affect(String bgdate_affect) {
		this.bgdate_affect = bgdate_affect;
	}
	public String getEddate_affect() {
		return eddate_affect;
	}
	public void setEddate_affect(String eddate_affect) {
		this.eddate_affect = eddate_affect;
	}
	public String getBgtime() {
		return bgtime;
	}
	public void setBgtime(String bgtime) {
		this.bgtime = bgtime;
	}
	public String getEdtime() {
		return edtime;
	}
	public void setEdtime(String edtime) {
		this.edtime = edtime;
	}
	public String getTimepart() {
		return timepart;
	}
	public void setTimepart(String timepart) {
		this.timepart = timepart;
	}
	
}
