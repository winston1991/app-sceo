package com.huntkey.software.sceo.bean;

public class RemindInfo {

	private String wftask;//待审单据
	private String metask;//待办事务
	private String upgrade;//系统待升级数
	private String abnwork;//考勤异常
	
	public String getWftask() {
		return wftask;
	}
	public void setWftask(String wftask) {
		this.wftask = wftask;
	}
	public String getMetask() {
		return metask;
	}
	public void setMetask(String metask) {
		this.metask = metask;
	}
	public String getUpgrade() {
		return upgrade;
	}
	public void setUpgrade(String upgrade) {
		this.upgrade = upgrade;
	}
	public String getAbnwork() {
		return abnwork;
	}
	public void setAbnwork(String abnwork) {
		this.abnwork = abnwork;
	}
	
	
}
