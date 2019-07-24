package com.huntkey.software.sceo.bean.eventbus;

public class EventBusAffairsNumAndIdAndTask {

	private int aEmpCount;//参与人数
	private int taskid;//item条目的id
	private String lasttask;//最后一条回话

	public int getaEmpCount() {
		return aEmpCount;
	}

	public void setaEmpCount(int aEmpCount) {
		this.aEmpCount = aEmpCount;
	}

	public int getTaskid() {
		return taskid;
	}

	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}

	public String getLasttask() {
		return lasttask;
	}

	public void setLasttask(String lasttask) {
		this.lasttask = lasttask;
	}
	
}
