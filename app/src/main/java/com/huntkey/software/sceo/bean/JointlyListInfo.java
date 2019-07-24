package com.huntkey.software.sceo.bean;

import java.util.List;

public class JointlyListInfo {

	private int uk_type;
	private int uk_seq;
	private List<JointlyListDetails> list;
	
	public int getUk_type() {
		return uk_type;
	}
	public void setUk_type(int uk_type) {
		this.uk_type = uk_type;
	}
	public int getUk_seq() {
		return uk_seq;
	}
	public void setUk_seq(int uk_seq) {
		this.uk_seq = uk_seq;
	}
	public List<JointlyListDetails> getList() {
		return list;
	}
	public void setList(List<JointlyListDetails> list) {
		this.list = list;
	}
	
	
}
