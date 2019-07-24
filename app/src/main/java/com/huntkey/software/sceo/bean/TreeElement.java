package com.huntkey.software.sceo.bean;

public class TreeElement {

	String cc_code = null;// 当前节点id(以工号作为id)
	int flag = -1;//判断当前item是部门还是员工,flag=0表示部门,flag=1表示员工
    String title = null;// 当前节点文字
    String photo = null;//节点头像
    String emp_sex = null;//性别
    String phone = null;//手机号码
    private String cc_level = null;//当前界面层级
    
    boolean hasParent = false;// 当前节点是否有父节点
    boolean hasChild = false;// 当前节点是否有子节点
    boolean childShow = false;// 如果子节点，子节点当前是否已显示
    String parentId = null;// 父节点id
    int level = -1;// 当前界面层级
    boolean fold = false;// 是否处于展开状态
    
    private boolean ex = false;//判断是否是第一次加载
    
	public String getCc_code() {
		return cc_code;
	}
	public void setCc_code(String cc_code) {
		this.cc_code = cc_code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
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
	public String getEmp_sex() {
		return emp_sex;
	}
	public void setEmp_sex(String emp_sex) {
		this.emp_sex = emp_sex;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public boolean isEx() {
		return ex;
	}
	public void setEx(boolean ex) {
		this.ex = ex;
	}
	public String getCc_level() {
		return cc_level;
	}
	public void setCc_level(String cc_level) {
		this.cc_level = cc_level;
	}
    
}
