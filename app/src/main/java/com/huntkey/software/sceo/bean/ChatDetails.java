package com.huntkey.software.sceo.bean;

import java.io.Serializable;

import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "chatdetails")
public class ChatDetails implements Serializable{

	/**
	 * 事务详情
	 */
	private static final long serialVersionUID = 1L;//serialVersionUID用来作为Java对象序列化中的版本标示之用
	@Column(column = "falg")
	private int flag;//flag=1表示是PC端发布的，有url，flag=0表示是移动端发布的
	@Column(column = "meflag")
	private int meflag;//meflag=0表示是接收的，mefalg=1表示我发的
	@Id
	private int id;
	@Column(column = "emp_id")
	private String emp_id;//工号
	@Column(column = "emp_name")
	private String emp_name;
	@Column(column = "creattime")
	private String creattime;
	@Column(column = "content")
	private String content;
	@Column(column = "photo")
	private String photo;
	@Column(column = "weburl")
	private String weburl;//PC端发布的点击时跳转到webView显示，移动端发布的无点击事件，webUrl若无则为空
	@Column(column = "subtaskid")
	private int subtaskid;//当前消息的id
	@Column(column = "taskid")
	private int taskid;
	@Column(column = "msgresult")
	private int msgresult = 0;//消息发送结果反馈，0为onLoading，显示progressBar,1为onSuccess，不显示progressBar
	@Column(column = "isReSendShow")
	private int isReSendShow = 0;//是否显示重新发送按钮，0不显示，1显示
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public int getMeflag() {
		return meflag;
	}
	public void setMeflag(int meflag) {
		this.meflag = meflag;
	}
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
	public String getCreattime() {
		return creattime;
	}
	public void setCreattime(String creattime) {
		this.creattime = creattime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getWeburl() {
		return weburl;
	}
	public void setWeburl(String weburl) {
		this.weburl = weburl;
	}
	public int getSubtaskid() {
		return subtaskid;
	}
	public void setSubtaskid(int subtaskid) {
		this.subtaskid = subtaskid;
	}
	public int getTaskid() {
		return taskid;
	}
	public void setTaskid(int taskid) {
		this.taskid = taskid;
	}
	public int getMsgresult() {
		return msgresult;
	}
	public void setMsgresult(int msgresult) {
		this.msgresult = msgresult;
	}
	public int getIsReSendShow() {
		return isReSendShow;
	}
	public void setIsReSendShow(int isReSendShow) {
		this.isReSendShow = isReSendShow;
	}
	
}
