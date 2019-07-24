package com.huntkey.software.sceo.bean;

import java.io.Serializable;
import java.util.List;

public class FormSecDetails implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String ppif_id;//ppiID
	private String ppif_ct;//指标导航数
	private String ppif_name;//ppi名称
	private String updatetime;//更新时间
	private String hasright;//权限
	private String st;//1方案，3部门展开,0无下级
	private String lvl;//部门展开
	private String cc_level;//部门编码-from:老孟
	private String cc_code;//部门编码-from:罗志胜
	private String cc_full;//部门名称
	private String unit_name;//单位
	private String ppid_yesterday;//昨日
	private String ruler_year;//今年年规划值
	private String ffl_per_year;//今年完成率
	private String ruler_sum;//今年上月累计规划值
	private String tb_ruler_sum;//今年上月累计规划值同比
	private String p_ruler_sum;//-----------
	private String ffl_sum;//今年上月累计完成值
	private String tb_ffl_sum;//今年上月累计完成值同比
	private String p_ffl_sum;//去年同期累计完成值
	private String precent;//累计完成率
	private String ruler_cur;//今年当前月规划值
	private String p_ruler_cur;//去年同期月规划值------------
	private String tb_ruler_cur;//今年当前月规划值同比
	private String ruler_sum_cur;//今年当前月累计规划值
	private String p_ruler_sum_cur;//去年同期月累计规划值-------------
	private String tb_ruler_sum_cur;//今年当前月累计规划值同比
	private String ffl_cur;//今年当前月完成值
	private String hb_ffl_cur;//上月环比

	private String p_ffl_cur;//去年同期月完成值
	private String tb_ffl_cur;//今年当前月完成值同比率
	private String ffl_sum_cur;//今年当前月累计完成值
	private String p_ffl_sum_cur;//去年同期月累计完成值
	private String tb_ffl_sum_cur;//今年当前月累计完成值同比
	private String p_ffl_avg;//平均上年月完成值
	private String max;//月最大值
	private String min;//月最小值
	private String max_sum;//累计月最大值
	private String min_sum;//累计月最小值
	private List<FormSecCDetails> detail_c;
	private String ppid_week;//当周
	private String curprecent;//当月完成率
	private String unit_digit;//小数点后面的位数
	private String ruler_name;//标签内容
	private String fyear;//当前财年
	private String ffl_virtual;//统计值当前财月的值，如果为空字符串则不加月化虚线，如果非空则增加一条月化虚线(连接当前财月的上一个月和当前财月)
	private String chat_flag;//0：未发起事务--不显示图标 1：已结束的事务--显示图标 2：未加入事务--显示图标 3：无未读消息--显示图标 4：有未读消息--显示图标及红点
	private String tmrm_id;//事务id(未使用)
	
	public String getPpif_id() {
		return ppif_id;
	}
	public void setPpif_id(String ppif_id) {
		this.ppif_id = ppif_id;
	}
	public String getPpif_ct() {
		return ppif_ct;
	}
	public void setPpif_ct(String ppif_ct) {
		this.ppif_ct = ppif_ct;
	}
	public String getPpif_name() {
		return ppif_name;
	}
	public void setPpif_name(String ppif_name) {
		this.ppif_name = ppif_name;
	}
	public String getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}
	public String getHasright() {
		return hasright;
	}
	public void setHasright(String hasright) {
		this.hasright = hasright;
	}
	public String getSt() {
		return st;
	}
	public void setSt(String st) {
		this.st = st;
	}
	public String getLvl() {
		return lvl;
	}
	public void setLvl(String lvl) {
		this.lvl = lvl;
	}
	public String getCc_level() {
		return cc_level;
	}
	public void setCc_level(String cc_level) {
		this.cc_level = cc_level;
	}
	public String getCc_full() {
		return cc_full;
	}
	public void setCc_full(String cc_full) {
		this.cc_full = cc_full;
	}
	public String getUnit_name() {
		return unit_name;
	}
	public void setUnit_name(String unit_name) {
		this.unit_name = unit_name;
	}
	public String getPpid_yesterday() {
		return ppid_yesterday;
	}
	public void setPpid_yesterday(String ppid_yesterday) {
		this.ppid_yesterday = ppid_yesterday;
	}
	public String getRuler_year() {
		return ruler_year;
	}
	public void setRuler_year(String ruler_year) {
		this.ruler_year = ruler_year;
	}
	public String getFfl_per_year() {
		return ffl_per_year;
	}
	public void setFfl_per_year(String ffl_per_year) {
		this.ffl_per_year = ffl_per_year;
	}
	public String getRuler_sum() {
		return ruler_sum;
	}
	public void setRuler_sum(String ruler_sum) {
		this.ruler_sum = ruler_sum;
	}
	public String getTb_ruler_sum() {
		return tb_ruler_sum;
	}
	public void setTb_ruler_sum(String tb_ruler_sum) {
		this.tb_ruler_sum = tb_ruler_sum;
	}
	public String getFfl_sum() {
		return ffl_sum;
	}
	public void setFfl_sum(String ffl_sum) {
		this.ffl_sum = ffl_sum;
	}
	public String getTb_ffl_sum() {
		return tb_ffl_sum;
	}
	public void setTb_ffl_sum(String tb_ffl_sum) {
		this.tb_ffl_sum = tb_ffl_sum;
	}
	public String getP_ffl_sum() {
		return p_ffl_sum;
	}
	public void setP_ffl_sum(String p_ffl_sum) {
		this.p_ffl_sum = p_ffl_sum;
	}
	public String getPrecent() {
		return precent;
	}
	public void setPrecent(String precent) {
		this.precent = precent;
	}
	public String getRuler_cur() {
		return ruler_cur;
	}
	public void setRuler_cur(String ruler_cur) {
		this.ruler_cur = ruler_cur;
	}
	public String getP_ruler_cur() {
		return p_ruler_cur;
	}
	public void setP_ruler_cur(String p_ruler_cur) {
		this.p_ruler_cur = p_ruler_cur;
	}
	public String getTb_ruler_cur() {
		return tb_ruler_cur;
	}
	public void setTb_ruler_cur(String tb_ruler_cur) {
		this.tb_ruler_cur = tb_ruler_cur;
	}
	public String getRuler_sum_cur() {
		return ruler_sum_cur;
	}
	public void setRuler_sum_cur(String ruler_sum_cur) {
		this.ruler_sum_cur = ruler_sum_cur;
	}
	public String getP_ruler_sum_cur() {
		return p_ruler_sum_cur;
	}
	public void setP_ruler_sum_cur(String p_ruler_sum_cur) {
		this.p_ruler_sum_cur = p_ruler_sum_cur;
	}
	public String getTb_ruler_sum_cur() {
		return tb_ruler_sum_cur;
	}
	public void setTb_ruler_sum_cur(String tb_ruler_sum_cur) {
		this.tb_ruler_sum_cur = tb_ruler_sum_cur;
	}
	public String getFfl_cur() {
		return ffl_cur;
	}
	public void setFfl_cur(String ffl_cur) {
		this.ffl_cur = ffl_cur;
	}

	public String getHb_ffl_cur() {
		return hb_ffl_cur;
	}
	public void setHb_ffl_cur(String hb_ffl_cur) {
		this.hb_ffl_cur = hb_ffl_cur;
	}

	public String getP_ffl_cur() {
		return p_ffl_cur;
	}
	public void setP_ffl_cur(String p_ffl_cur) {
		this.p_ffl_cur = p_ffl_cur;
	}
	public String getTb_ffl_cur() {
		return tb_ffl_cur;
	}
	public void setTb_ffl_cur(String tb_ffl_cur) {
		this.tb_ffl_cur = tb_ffl_cur;
	}
	public String getFfl_sum_cur() {
		return ffl_sum_cur;
	}
	public void setFfl_sum_cur(String ffl_sum_cur) {
		this.ffl_sum_cur = ffl_sum_cur;
	}
	public String getP_ffl_sum_cur() {
		return p_ffl_sum_cur;
	}
	public void setP_ffl_sum_cur(String p_ffl_sum_cur) {
		this.p_ffl_sum_cur = p_ffl_sum_cur;
	}
	public String getTb_ffl_sum_cur() {
		return tb_ffl_sum_cur;
	}
	public void setTb_ffl_sum_cur(String tb_ffl_sum_cur) {
		this.tb_ffl_sum_cur = tb_ffl_sum_cur;
	}
	public String getP_ffl_avg() {
		return p_ffl_avg;
	}
	public void setP_ffl_avg(String p_ffl_avg) {
		this.p_ffl_avg = p_ffl_avg;
	}
	public String getMax() {
		return max;
	}
	public void setMax(String max) {
		this.max = max;
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		this.min = min;
	}
	public String getMax_sum() {
		return max_sum;
	}
	public void setMax_sum(String max_sum) {
		this.max_sum = max_sum;
	}
	public String getMin_sum() {
		return min_sum;
	}
	public void setMin_sum(String min_sum) {
		this.min_sum = min_sum;
	}
	public List<FormSecCDetails> getDetail_c() {
		return detail_c;
	}
	public void setDetail_c(List<FormSecCDetails> detail_c) {
		this.detail_c = detail_c;
	}
	public String getPpid_week() {
		return ppid_week;
	}
	public void setPpid_week(String ppid_week) {
		this.ppid_week = ppid_week;
	}
	public String getCurprecent() {
		return curprecent;
	}
	public void setCurprecent(String curprecent) {
		this.curprecent = curprecent;
	}
	public String getP_ruler_sum() {
		return p_ruler_sum;
	}
	public void setP_ruler_sum(String p_ruler_sum) {
		this.p_ruler_sum = p_ruler_sum;
	}
	public String getUnit_digit() {
		return unit_digit;
	}
	public void setUnit_digit(String unit_digit) {
		this.unit_digit = unit_digit;
	}
	public String getRuler_name() {
		return ruler_name;
	}
	public void setRuler_name(String ruler_name) {
		this.ruler_name = ruler_name;
	}
	public String getFyear() {
		return fyear;
	}
	public void setFyear(String fyear) {
		this.fyear = fyear;
	}
	public String getCc_code() {
		return cc_code;
	}
	public void setCc_code(String cc_code) {
		this.cc_code = cc_code;
	}
	public String getFfl_virtual() {
		return ffl_virtual;
	}
	public void setFfl_virtual(String ffl_virtual) {
		this.ffl_virtual = ffl_virtual;
	}
	public String getChat_flag() {
		return chat_flag;
	}
	public void setChat_flag(String chat_flag) {
		this.chat_flag = chat_flag;
	}
	public String getTmrm_id() {
		return tmrm_id;
	}
	public void setTmrm_id(String tmrm_id) {
		this.tmrm_id = tmrm_id;
	}
	
}
