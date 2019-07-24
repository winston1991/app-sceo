package com.huntkey.software.sceo.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "urldetails")
public class UrlDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(column = "acctid")
	private int acctid;
	@Column(column = "srvname")
	private String srvname;
	@Column(column = "srvurl")
	private String srvurl;
	@Column(column = "srvid")
	private int srvid;
	@Id
	private int id;
	
	public int getAcctid() {
		return acctid;
	}
	public void setAcctid(int acctid) {
		this.acctid = acctid;
	}
	public String getSrvname() {
		return srvname;
	}
	public void setSrvname(String srvname) {
		this.srvname = srvname;
	}
	public String getSrvurl() {
		return srvurl;
	}
	public void setSrvurl(String srvurl) {
		this.srvurl = srvurl;
	}
	public int getSrvid() {
		return srvid;
	}
	public void setSrvid(int srvid) {
		this.srvid = srvid;
	}
	
}
