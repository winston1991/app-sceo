package com.huntkey.software.sceo.bean;

public class ChatSettingDetails {

	private String taskname;//事务名称
	private int setflag;//是否可设置,0为不可设置，1为可设置
	private int raiseflag;//是否是发起人,0为不是，1为是
	private int btnflag;//按钮显示，0为不显示按钮，1显示
	private String btnname;//按钮名称
	private int c1;//查看加入事务前的消息
	private int c2;//查看退出事务后的消息
	private int c3;//邀请其他人员加入
	private String c4;//字体
	private int c5;//事务消息免打扰
	private int c6;//刷新时间
	private int c7;//显示参与人姓名
	
	public String getTaskname() {
		return taskname;
	}
	public void setTaskname(String taskname) {
		this.taskname = taskname;
	}
	public int getSetflag() {
		return setflag;
	}
	public void setSetflag(int setflag) {
		this.setflag = setflag;
	}
	public int getRaiseflag() {
		return raiseflag;
	}
	public void setRaiseflag(int raiseflag) {
		this.raiseflag = raiseflag;
	}
	public int getBtnflag() {
		return btnflag;
	}
	public void setBtnflag(int btnflag) {
		this.btnflag = btnflag;
	}
	public String getBtnname() {
		return btnname;
	}
	public void setBtnname(String btnname) {
		this.btnname = btnname;
	}
	public int getC1() {
		return c1;
	}
	public void setC1(int c1) {
		this.c1 = c1;
	}
	public int getC2() {
		return c2;
	}
	public void setC2(int c2) {
		this.c2 = c2;
	}
	public int getC3() {
		return c3;
	}
	public void setC3(int c3) {
		this.c3 = c3;
	}
	public String getC4() {
		return c4;
	}
	public void setC4(String c4) {
		this.c4 = c4;
	}
	public int getC5() {
		return c5;
	}
	public void setC5(int c5) {
		this.c5 = c5;
	}
	public int getC6() {
		return c6;
	}
	public void setC6(int c6) {
		this.c6 = c6;
	}
	public int getC7() {
		return c7;
	}
	public void setC7(int c7) {
		this.c7 = c7;
	}
	
	
}
