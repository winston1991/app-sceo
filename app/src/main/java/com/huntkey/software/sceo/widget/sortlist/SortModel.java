package com.huntkey.software.sceo.widget.sortlist;

import java.io.Serializable;

import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "sortmodel")
public class SortModel implements Serializable{

	/**
	 * 常用联系人
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private int id;
	@Column(column = "personalId")
	private String personalId;
	
	@Column(column = "emp_id")
	private String emp_id;//id
	@Column(column = "emp_name")
	private String emp_name;   //显示的数据
	@Column(column = "sortLetters")
	private String sortLetters;  //显示数据拼音的首字母
	@Column(column = "emp_sex")
	private String emp_sex;//性别
	@Column(column = "emp_photo")
	private String emp_photo;  //联系人头像
	@Column(column = "emp_cellphone")
	private String emp_cellphone;//联系人电话号码
	@Column(column = "smsflag")
	private int smsflag;//是否可以发短信，0不能发，1可以发
	
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public String getEmp_name() {
		return emp_name;
	}
	public void setEmp_name(String emp_name) {
		this.emp_name = emp_name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getEmp_sex() {
		return emp_sex;
	}
	public void setEmp_sex(String emp_sex) {
		this.emp_sex = emp_sex;
	}
	public String getEmp_photo() {
		return emp_photo;
	}
	public void setEmp_photo(String emp_photo) {
		this.emp_photo = emp_photo;
	}
	public String getEmp_cellphone() {
		return emp_cellphone;
	}
	public void setEmp_cellphone(String emp_cellphone) {
		this.emp_cellphone = emp_cellphone;
	}
	public int getSmsflag() {
		return smsflag;
	}
	public void setSmsflag(int smsflag) {
		this.smsflag = smsflag;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPersonalId() {
		return personalId;
	}
	public void setPersonalId(String personalId) {
		this.personalId = personalId;
	}
	
}
