package com.huntkey.software.sceo.bean;

import java.io.Serializable;

public class FormEditDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String kmas_id;//设置id
	private String ppif_id;//指标id
	private String ppit_name;//指标分类
	private String ppif_name;//指标名称
	private String upper_code;//节点
	private String upper_desc;//节点名称
	private String kmas_porder;//排序的序号
	private String chkflag;//是否已选
	
	public String getKmas_id() {
		return kmas_id;
	}
	public void setKmas_id(String kmas_id) {
		this.kmas_id = kmas_id;
	}
	public String getPpif_id() {
		return ppif_id;
	}
	public void setPpif_id(String ppif_id) {
		this.ppif_id = ppif_id;
	}
	public String getPpit_name() {
		return ppit_name;
	}
	public void setPpit_name(String ppit_name) {
		this.ppit_name = ppit_name;
	}
	public String getPpif_name() {
		return ppif_name;
	}
	public void setPpif_name(String ppif_name) {
		this.ppif_name = ppif_name;
	}
	public String getUpper_code() {
		return upper_code;
	}
	public void setUpper_code(String upper_code) {
		this.upper_code = upper_code;
	}
	public String getUpper_desc() {
		return upper_desc;
	}
	public void setUpper_desc(String upper_desc) {
		this.upper_desc = upper_desc;
	}
	public String getKmas_porder() {
		return kmas_porder;
	}
	public void setKmas_porder(String kmas_porder) {
		this.kmas_porder = kmas_porder;
	}
	public String getChkflag() {
		return chkflag;
	}
	public void setChkflag(String chkflag) {
		this.chkflag = chkflag;
	}
	
}
