package com.huntkey.software.sceo.bean;

public class FormQuarterDetails {

	private String ffl;//今年当季统计值
	private String p_ffl;//去年当季统计值 and 今年当季同比值
	private String pp_ffl;//前年当季统计值 and 去年当季同比值
	private String ppp_ffl;//前年同比值
	private String ruler;//今年规划值
	private String p_ruler;//去年规划值
	private String pp_ruler;//前年规划值
	private String fq;//当前季度
	private String ppid_time;//当前数据所属季度
	private String ffl_virtual;//统计值当前财季的值，如果为空字符串则不加季化虚线，如果非空则增加一条季化虚线(连接当前财季的上一季度和当前财季)
	
	public String getFfl() {
		return ffl;
	}
	public void setFfl(String ffl) {
		this.ffl = ffl;
	}
	public String getP_ffl() {
		return p_ffl;
	}
	public void setP_ffl(String p_ffl) {
		this.p_ffl = p_ffl;
	}
	public String getPp_ffl() {
		return pp_ffl;
	}
	public void setPp_ffl(String pp_ffl) {
		this.pp_ffl = pp_ffl;
	}
	public String getPpp_ffl() {
		return ppp_ffl;
	}
	public void setPpp_ffl(String ppp_ffl) {
		this.ppp_ffl = ppp_ffl;
	}
	public String getRuler() {
		return ruler;
	}
	public void setRuler(String ruler) {
		this.ruler = ruler;
	}
	public String getP_ruler() {
		return p_ruler;
	}
	public void setP_ruler(String p_ruler) {
		this.p_ruler = p_ruler;
	}
	public String getPp_ruler() {
		return pp_ruler;
	}
	public void setPp_ruler(String pp_ruler) {
		this.pp_ruler = pp_ruler;
	}
	public String getFq() {
		return fq;
	}
	public void setFq(String fq) {
		this.fq = fq;
	}
	public String getPpid_time() {
		return ppid_time;
	}
	public void setPpid_time(String ppid_time) {
		this.ppid_time = ppid_time;
	}
	public String getFfl_virtual() {
		return ffl_virtual;
	}
	public void setFfl_virtual(String ffl_virtual) {
		this.ffl_virtual = ffl_virtual;
	}
	
}
