package com.huntkey.software.sceo.bean;

import java.io.Serializable;

public class FormSecCDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ruler;//月规划值
	private String p_ruler;//上年同期规划值
	private String tb_ruler;//月规划值同比率
	private String ruler_sum;//月累计规划值
	private String p_ruler_sum;//上年同期累计规划值
	private String tb_ruler_sum;//月累计规划值同比
	private String ffl;//月完成值
	private String p_ffl;//上年同期完成值
	private String tb_ffl;//月完成值同比
	private String ffl_sum;//月累计完成值
	private String p_ffl_sum;//上年同期累计完成值
	private String tb_ffl_sum;//月累计完成值同比
	
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
	public String getTb_ruler() {
		return tb_ruler;
	}
	public void setTb_ruler(String tb_ruler) {
		this.tb_ruler = tb_ruler;
	}
	public String getRuler_sum() {
		return ruler_sum;
	}
	public void setRuler_sum(String ruler_sum) {
		this.ruler_sum = ruler_sum;
	}
	public String getP_ruler_sum() {
		return p_ruler_sum;
	}
	public void setP_ruler_sum(String p_ruler_sum) {
		this.p_ruler_sum = p_ruler_sum;
	}
	public String getTb_ruler_sum() {
		return tb_ruler_sum;
	}
	public void setTb_ruler_sum(String tb_ruler_sum) {
		this.tb_ruler_sum = tb_ruler_sum;
	}
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
	public String getTb_ffl() {
		return tb_ffl;
	}
	public void setTb_ffl(String tb_ffl) {
		this.tb_ffl = tb_ffl;
	}
	public String getFfl_sum() {
		return ffl_sum;
	}
	public void setFfl_sum(String ffl_sum) {
		this.ffl_sum = ffl_sum;
	}
	public String getP_ffl_sum() {
		return p_ffl_sum;
	}
	public void setP_ffl_sum(String p_ffl_sum) {
		this.p_ffl_sum = p_ffl_sum;
	}
	public String getTb_ffl_sum() {
		return tb_ffl_sum;
	}
	public void setTb_ffl_sum(String tb_ffl_sum) {
		this.tb_ffl_sum = tb_ffl_sum;
	}
	
}
