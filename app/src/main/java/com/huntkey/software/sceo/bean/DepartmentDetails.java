package com.huntkey.software.sceo.bean;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "departmentdetails")
public class DepartmentDetails implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	private int id;
	@Column(column = "cc_code")
	private String cc_code = null;//部门编码
	@Column(column = "cc_desc")
	private String cc_desc = null;//部门名称
	@Column(column = "cc_level")
	private String cc_level = null;//部门层级
	@Column(column = "childCount")
	private int childCount = 0;//子节点个数 为0表示没有子节点
	
	@Column(column = "hasParent")
	private boolean hasParent = false;//当前节点是否有父节点
	@Column(column = "hasChild")
	private boolean hasChild = false;//当前节点是否有子节点
	@Column(column = "childShow")
	private boolean childShow = false;//如果有子节点，子节点当前是否已显示
	@Column(column = "parentId")
	private String parentId = null;//父节点id
	@Column(column = "level")
	private int level = -1;//当前界面层级
	@Column(column = "fold")
	private boolean fold = false;//是否处于展开状态
	
	public String getCc_code() {
		return cc_code;
	}
	public void setCc_code(String cc_code) {
		this.cc_code = cc_code;
	}
	public String getCc_desc() {
		return cc_desc;
	}
	public void setCc_desc(String cc_desc) {
		this.cc_desc = cc_desc;
	}
	public String getCc_level() {
		return cc_level;
	}
	public void setCc_level(String cc_level) {
		this.cc_level = cc_level;
	}
	public int getChildCount() {
		return childCount;
	}
	public void setChildCount(int childCount) {
		this.childCount = childCount;
	}
	public boolean isHasParent() {
		return hasParent;
	}
	public void setHasParent(boolean hasParent) {
		this.hasParent = hasParent;
	}
	public boolean isHasChild() {
		return hasChild;
	}
	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}
	public boolean isChildShow() {
		return childShow;
	}
	public void setChildShow(boolean childShow) {
		this.childShow = childShow;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public boolean isFold() {
		return fold;
	}
	public void setFold(boolean fold) {
		this.fold = fold;
	}
	
}
