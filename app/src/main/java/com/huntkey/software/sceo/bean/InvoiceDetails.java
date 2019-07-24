package com.huntkey.software.sceo.bean;

import java.io.Serializable;

public class InvoiceDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int mid;//单据id
	private int wfid;//流程
	private int nid;//节点id
	private int aid;//当前审核id
	private int lid;//控制id
	private int snid;//控制id
	private int wfna_id;//流程节点id
	private String wfna_nbr;//单号
	private String wfm_name;//名称
	private String wfna_apply_dept;//部门
	private String wfna_addtime;//时间
	private String wfna_summary;//摘要
	private String color_n1;//字体颜色
	private String bcolor_n1;//背景颜色
	private String showname_n1;//名字
	private String showname_count1;//该流程审核人数
	private String color_n2;
	private String bcolor_n2;
	private String showname_n2;
	private String showname_count2;
	private String color_n3;
	private String bcolor_n3;
	private String showname_n3;
	private String showname_count3;
	private String color_n4;
	private String bcolor_n4;
	private String showname_n4;
	private String showname_count4;
	private String weburl;
	private String color_nbr = "#a0a0a0";//单号的颜色
	
	public int getMid() {
		return mid;
	}
	public void setMid(int mid) {
		this.mid = mid;
	}
	public int getWfid() {
		return wfid;
	}
	public void setWfid(int wfid) {
		this.wfid = wfid;
	}
	public int getNid() {
		return nid;
	}
	public void setNid(int nid) {
		this.nid = nid;
	}
	public int getAid() {
		return aid;
	}
	public void setAid(int aid) {
		this.aid = aid;
	}
	public int getLid() {
		return lid;
	}
	public void setLid(int lid) {
		this.lid = lid;
	}
	public int getSnid() {
		return snid;
	}
	public void setSnid(int snid) {
		this.snid = snid;
	}
	public int getWfna_id() {
		return wfna_id;
	}
	public void setWfna_id(int wfna_id) {
		this.wfna_id = wfna_id;
	}
	public String getWfna_nbr() {
		return wfna_nbr;
	}
	public void setWfna_nbr(String wfna_nbr) {
		this.wfna_nbr = wfna_nbr;
	}
	public String getWfm_name() {
		return wfm_name;
	}
	public void setWfm_name(String wfm_name) {
		this.wfm_name = wfm_name;
	}
	public String getWfna_apply_dept() {
		return wfna_apply_dept;
	}
	public void setWfna_apply_dept(String wfna_apply_dept) {
		this.wfna_apply_dept = wfna_apply_dept;
	}
	public String getWfna_addtime() {
		return wfna_addtime;
	}
	public void setWfna_addtime(String wfna_addtime) {
		this.wfna_addtime = wfna_addtime;
	}
	public String getWfna_summary() {
		return wfna_summary;
	}
	public void setWfna_summary(String wfna_summary) {
		this.wfna_summary = wfna_summary;
	}
	public String getColor_n1() {
		return color_n1;
	}
	public void setColor_n1(String color_n1) {
		this.color_n1 = color_n1;
	}
	public String getBcolor_n1() {
		return bcolor_n1;
	}
	public void setBcolor_n1(String bcolor_n1) {
		this.bcolor_n1 = bcolor_n1;
	}
	public String getShowname_n1() {
		return showname_n1;
	}
	public void setShowname_n1(String showname_n1) {
		this.showname_n1 = showname_n1;
	}
	public String getColor_n2() {
		return color_n2;
	}
	public void setColor_n2(String color_n2) {
		this.color_n2 = color_n2;
	}
	public String getBcolor_n2() {
		return bcolor_n2;
	}
	public void setBcolor_n2(String bcolor_n2) {
		this.bcolor_n2 = bcolor_n2;
	}
	public String getShowname_n2() {
		return showname_n2;
	}
	public void setShowname_n2(String showname_n2) {
		this.showname_n2 = showname_n2;
	}
	public String getColor_n3() {
		return color_n3;
	}
	public void setColor_n3(String color_n3) {
		this.color_n3 = color_n3;
	}
	public String getBcolor_n3() {
		return bcolor_n3;
	}
	public void setBcolor_n3(String bcolor_n3) {
		this.bcolor_n3 = bcolor_n3;
	}
	public String getShowname_n3() {
		return showname_n3;
	}
	public void setShowname_n3(String showname_n3) {
		this.showname_n3 = showname_n3;
	}
	public String getColor_n4() {
		return color_n4;
	}
	public void setColor_n4(String color_n4) {
		this.color_n4 = color_n4;
	}
	public String getBcolor_n4() {
		return bcolor_n4;
	}
	public void setBcolor_n4(String bcolor_n4) {
		this.bcolor_n4 = bcolor_n4;
	}
	public String getShowname_n4() {
		return showname_n4;
	}
	public void setShowname_n4(String showname_n4) {
		this.showname_n4 = showname_n4;
	}
	public String getWeburl() {
		return weburl;
	}
	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}
	public String getColor_nbr() {
		return color_nbr;
	}
	public void setColor_nbr(String color_nbr) {
		this.color_nbr = color_nbr;
	}
	public String getShowname_count1() {
		return showname_count1;
	}
	public void setShowname_count1(String showname_count1) {
		this.showname_count1 = showname_count1;
	}
	public String getShowname_count2() {
		return showname_count2;
	}
	public void setShowname_count2(String showname_count2) {
		this.showname_count2 = showname_count2;
	}
	public String getShowname_count3() {
		return showname_count3;
	}
	public void setShowname_count3(String showname_count3) {
		this.showname_count3 = showname_count3;
	}
	public String getShowname_count4() {
		return showname_count4;
	}
	public void setShowname_count4(String showname_count4) {
		this.showname_count4 = showname_count4;
	}
	
	
}
