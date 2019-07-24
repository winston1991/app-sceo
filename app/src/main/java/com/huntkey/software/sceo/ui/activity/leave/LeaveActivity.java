package com.huntkey.software.sceo.ui.activity.leave;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.bean.LeaveDurationData;
import com.huntkey.software.sceo.bean.LeaveDurationDetails;
import com.huntkey.software.sceo.bean.LeaveInitData;
import com.huntkey.software.sceo.bean.LeaveInitInfo;
import com.huntkey.software.sceo.bean.LeaveInitType;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.TimeUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog;
import com.huntkey.software.sceo.widget.sweetalertdialog.SweetAlertDialog.OnSweetClickListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.sleepbot.datetimepicker.time.TimePickerDialog.OnTimeSetListener;

import es.dmoral.toasty.Toasty;

/**
 * 请假申请
 * @author chenliang3
 *
 */
public class LeaveActivity extends BaseActivity{

	@ViewInject(R.id.leave_title)
	BackTitle title;//标题
	@ViewInject(R.id.leave_type_layout)
	RelativeLayout typeLayout;
	@ViewInject(R.id.leave_type_tv)
	TextView typeTv;//请假类型
	@ViewInject(R.id.leave_agency_layout)
	RelativeLayout agencyLayout;
	@ViewInject(R.id.leave_agency_tv)
	TextView agencyTv;//代理人
	@ViewInject(R.id.leave_date_start_layout)
	RelativeLayout dateStartLayout;
	@ViewInject(R.id.leave_date_start)
	TextView dateStartTv;//起始日期
	@ViewInject(R.id.leave_time_start_layout)
	RelativeLayout timeStartLayout;
	@ViewInject(R.id.leave_time_start)
	TextView timeStartTv;//起始时间
	@ViewInject(R.id.leave_date_end_layout)
	RelativeLayout dateEndLayout;
	@ViewInject(R.id.leave_date_end)
	TextView dateEndTv;//结束日期
	@ViewInject(R.id.leave_time_end_layout)
	RelativeLayout timeEndLayout;
	@ViewInject(R.id.leave_time_end)
	TextView timeEndTv;//结束时间
	@ViewInject(R.id.leave_normal_duration_layout)
	RelativeLayout normalDurationLayout;
	@ViewInject(R.id.leave_normal_duration_tv)
	TextView normalDurationTv;//正班工时
	@ViewInject(R.id.leave_add_duration_layout)
	RelativeLayout addDurationLayout;
	@ViewInject(R.id.leave_add_duration_tv)
	TextView addDurationTv;//加班工时
	@ViewInject(R.id.leave_normal_day_layout)
	RelativeLayout normalDayLayout;
	@ViewInject(R.id.leave_normal_day_tv)
	TextView normalDayTv;//正班天数
	@ViewInject(R.id.leave_maternity_day_layout)
	RelativeLayout maternityDayLayout;
	@ViewInject(R.id.leave_maternity_day_tv)
	TextView maternityDayTv;//请假天数
	@ViewInject(R.id.leave_reason)
	EditText reasonEt;//请假事由
	@ViewInject(R.id.leave_annual_info)
	TextView annualInfoTv;//年假信息
	@ViewInject(R.id.leave_shift_info)
	TextView shiftInfoTv;//班制信息
	
	private LoadingDialog loadingDialog;
	private LeaveInitData data;
	private LeaveInitInfo info;
	private LeaveDurationDetails details;//正班工时、加班工时、请假天数等
	private float workingHour;//正班工时
	private List<LeaveInitType> leaveTypes = new ArrayList<>();
	private List<String> adltCodes = new ArrayList<>();
	private String typeCode;//请假类型
	private String agencyCode;//代理人id
	private String startDate;//开始日期
	private String startTime = "";//开始时间
	private String endDate;//结束日期
	private String endTime = "";//结束时间
	
	private int REQUEST_CODE_TYPE = 0x21;//请假类型请求码
	private int REQUEST_CODE_AGENCY = 0x22;//代理人请求码
	private static final String DATEPICKER_TAG = "datepicker";
	private static final String TIMEPICKER_TAG = "timepicker";
	
	private Calendar calendar;
	private int tmpStartYear;
	private int tmpStartMonth;
	private int tmpStartDay;
	private int tmpStartHour;
	private int tmpStartMinute;
	private int tmpEndYear;
	private int tmpEndMonth;
	private int tmpEndDay;
	private int tmpEndHour;
	private int tmpEndMinute;
	
	private static final int INIT_REQUEST = 0;//getDurationData()的默认请求
	private static final int STARTDATE_REQUEST = 1;//点击开始时间发出的请求
	private static final int ENDDATE_REQUEST = 2;//点击结束时间发出的请求
	private static final int STARTTIME_REQUEST = 3;//点击开始时间发出的请求
	private static final int ENDTIME_REQUEST = 4;//点击结束时间发出的请求
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leave);
		ViewUtils.inject(this);
		
		initTitle();
		initView();
	}
	
	/**
	 * 只有当onCreate()执行完才能获取控件宽高
	 * onCreate()->OnStart()->OnResume()->onAttachedToWindow()->onWindowFocusChanged()
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		int tvWidth = dateEndTv.getWidth();
		int layoutWidth = dateEndLayout.getWidth();
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins((layoutWidth-tvWidth), 0, 0, 0);
		timeEndTv.setLayoutParams(layoutParams);
	}

	private void initView() {
		loadingDialog = new LoadingDialog(LeaveActivity.this);
		
		//-----------------默认时间处理-------------------
		calendar = Calendar.getInstance();
		
		int _year = calendar.get(Calendar.YEAR);
		int _month = calendar.get(Calendar.MONTH);
		int _day = calendar.get(Calendar.DAY_OF_MONTH);
		String _monthStr = null, _dayStr = null;
		if (String.valueOf(_month+1).length() < 2) {
			_monthStr = "0"+(_month+1);
		}else if(String.valueOf(_month+1).length() > 1){			
			_monthStr = ""+(_month+1);
		}
		if (String.valueOf(_day).length() < 2) {
			_dayStr = "0"+_day;
		}else if (String.valueOf(_day).length() > 1) {
			_dayStr = ""+_day;
		}
		startDate = _year + "-" + _monthStr + "-" + _dayStr;
		endDate = startDate;
		
		tmpStartYear = _year;
		tmpStartMonth = _month;
		tmpStartDay = _day;
		tmpEndYear = _year;
		tmpEndMonth = _month;
		tmpEndDay = _day;
		
		dateStartTv.setText(startDate);
		dateEndTv.setText(endDate);
		
		MyOnclickLis onclickLis = new MyOnclickLis();
		typeLayout.setOnClickListener(onclickLis);
		agencyLayout.setOnClickListener(onclickLis);
		dateStartLayout.setOnClickListener(onclickLis);
		timeStartLayout.setOnClickListener(onclickLis);
		dateEndLayout.setOnClickListener(onclickLis);
		timeEndLayout.setOnClickListener(onclickLis);
		
		getInitData();
	}
	
	private class MyOnclickLis implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.leave_type_layout://请假
				Intent intent = new Intent(LeaveActivity.this, LeaveTypeActivity.class);
				intent.putExtra("leaveTypes", (Serializable)leaveTypes);
				startActivityForResult(intent, REQUEST_CODE_TYPE);
				break;
			case R.id.leave_agency_layout://代理人
				Intent intent2 = new Intent(LeaveActivity.this, LeaveAgencyActivity.class);
				startActivityForResult(intent2, REQUEST_CODE_AGENCY);
				break;
			case R.id.leave_date_start_layout://起始日期
				DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePickerDialog datePickerDialog, int year,
							int month, int day) {
						tmpStartYear = year;
						tmpStartMonth = month;
						tmpStartDay = day;
						
						String _monthStr = null, _dayStr = null;
						if (String.valueOf(month+1).length() < 2) {
							_monthStr = "0" + (month+1);
						}else if (String.valueOf(month+1).length() > 1) {
							_monthStr = "" + (month+1);
						}
						if (String.valueOf(day).length() < 2) {
							_dayStr = "0" + day;
						}else if (String.valueOf(day).length() > 1) {
							_dayStr = "" + day;
						}
						startDate = year + "-" + _monthStr + "-" + _dayStr;
						dateStartTv.setText(startDate);
						
						getDurationData(STARTDATE_REQUEST);
					}
				}, tmpStartYear,tmpStartMonth,tmpStartDay, false);
				datePickerDialog.setVibrate(false);
				datePickerDialog.setYearRange(calendar.get(Calendar.YEAR)-1, calendar.get(Calendar.YEAR) + 10);
				datePickerDialog.setCloseOnSingleTapDay(true);
				datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
				break;
			case R.id.leave_time_start_layout://起始时间
				TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
						tmpStartHour = hourOfDay;
						tmpStartMinute = minute;
						
						if (String.valueOf(minute).length() == 1) {
							startTime = hourOfDay + ":0" + minute;
						}else {							
							startTime = hourOfDay + ":" + minute;
						}
						timeStartTv.setText(startTime);
						
						getDurationData(STARTTIME_REQUEST);
					}
				}, tmpStartHour, tmpStartMinute, true, false);
				timePickerDialog.setVibrate(false);
				timePickerDialog.setCloseOnSingleTapMinute(true);
				timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);
				break;
			case R.id.leave_date_end_layout://结束日期
				DatePickerDialog datePickerDialog2 = DatePickerDialog.newInstance(new OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePickerDialog datePickerDialog, int year,
							int month, int day) {
						tmpEndYear = year;
						tmpEndMonth = month;
						tmpEndDay = day;
						
						String _monthStr = null, _dayStr = null;
						if (String.valueOf(month+1).length() < 2) {
							_monthStr = "0" + (month+1);
						}else if (String.valueOf(month+1).length() > 1) {
							_monthStr = "" + (month+1);
						}
						if (String.valueOf(day).length() < 2) {
							_dayStr = "0" + day;
						}else if (String.valueOf(day).length() > 1) {
							_dayStr = "" + day;
						}
						endDate = year + "-" + _monthStr + "-" + _dayStr;
						dateEndTv.setText(endDate);
						
						getDurationData(ENDDATE_REQUEST);
					}
				}, tmpEndYear,tmpEndMonth,tmpEndDay, false);
				datePickerDialog2.setVibrate(false);
				datePickerDialog2.setYearRange(calendar.get(Calendar.YEAR)-1, calendar.get(Calendar.YEAR) + 10);
				datePickerDialog2.setCloseOnSingleTapDay(true);
				datePickerDialog2.show(getSupportFragmentManager(), DATEPICKER_TAG);
				break;
			case R.id.leave_time_end_layout://结束时间
				TimePickerDialog timePickerDialog2 = TimePickerDialog.newInstance(new OnTimeSetListener() {
					
					@Override
					public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
						tmpEndHour = hourOfDay;
						tmpEndMinute = minute;
						
						if (String.valueOf(minute).length() == 1) {
							endTime = hourOfDay + ":0" + minute;
						}else {							
							endTime = hourOfDay + ":" + minute;
						}
						timeEndTv.setText(endTime);
						
						getDurationData(ENDTIME_REQUEST);
					}
				}, tmpEndHour, tmpEndMinute, true, false);
				timePickerDialog2.setVibrate(false);
				timePickerDialog2.setCloseOnSingleTapMinute(true);
				timePickerDialog2.show(getSupportFragmentManager(), TIMEPICKER_TAG);
				break;

			default:
				break;
			}
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {			
			if (requestCode == REQUEST_CODE_TYPE) {//请假类型
				typeCode = intent.getStringExtra("typeCode");
				typeTv.setText(intent.getStringExtra("typeNmae"));
				
				normalDurationLayout.setVisibility(View.VISIBLE);
				addDurationLayout.setVisibility(View.GONE);
				normalDayLayout.setVisibility(View.VISIBLE);
				maternityDayLayout.setVisibility(View.GONE);
				for (int i = 0; i < adltCodes.size(); i++) {
					if (adltCodes.get(i).equals(typeCode)) {
						normalDurationLayout.setVisibility(View.VISIBLE);
						addDurationLayout.setVisibility(View.VISIBLE);
						normalDayLayout.setVisibility(View.VISIBLE);
						maternityDayLayout.setVisibility(View.GONE);
					}
				}
				if (typeCode.equals("L003")) {
					normalDurationLayout.setVisibility(View.GONE);
					addDurationLayout.setVisibility(View.GONE);
					normalDayLayout.setVisibility(View.GONE);
					maternityDayLayout.setVisibility(View.VISIBLE);
				}
				
//				getDurationData(INIT_REQUEST);//------------------------这里要不要请求？----------------------------
			}else if (requestCode == REQUEST_CODE_AGENCY) {//代理人
				agencyCode = intent.getStringExtra("agencyCode");
				agencyTv.setText(intent.getStringExtra("agencyName"));
			}
		}
	}
	
	private void getInitData(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(LeaveActivity.this));
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA09AB&pcmd=GetInitData",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						data = SceoUtil.parseJson(responseInfo.result, LeaveInitData.class);
						if (data.getStatus() == 0) {
							info = data.getData();
							leaveTypes = info.getLerm_types();
							adltCodes = info.getAdlt_codes();

							if (leaveTypes.size() > 0){
								typeCode = leaveTypes.get(0).getAdlt_code();
								typeTv.setText(leaveTypes.get(0).getAdlt_name());
							}

							//年假信息
							annualInfoTv.setText("年假区间：" + info.getBegdate() + "~" + info.getEnddate() + "\n"
									+ "享有年假天数/时数：" + info.getXynj_day() + "天" + "/" + info.getXynj_hour() + "小时\n"
									+ "已休/剩余年假时数：" + info.getYxnj_hour() + "小时/" + info.getSynj_hour() + "小时"
									+ "\n" + "已请假天数：" + info.getYqj_day() + "天");
							
							getDurationData(INIT_REQUEST);
						}else if (data.getStatus() == 88) {
							Toasty.error(LeaveActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(LeaveActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(LeaveActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	/**
	 * 正班工时、正班天数、加班工时、请假天数
	 */
	private void getDurationData(final int flag){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(LeaveActivity.this));
		params.addBodyParameter("bgdate", startDate + " " + startTime);
		params.addBodyParameter("eddate", endDate + " " + endTime);
		params.addBodyParameter("flag", ""+flag);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA09AB&pcmd=GetDayHour",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						LeaveDurationData data = SceoUtil.parseJson(responseInfo.result, LeaveDurationData.class);
						if (data.getStatus() == 0) {
							details = data.getData();
							
							workingHour = Float.parseFloat(details.getZb_hour());
							
							normalDurationTv.setText(details.getZb_hour());
							addDurationTv.setText(details.getJb_hour());
							normalDayTv.setText(details.getZb_day());
							maternityDayTv.setText(details.getQj_day());
							
							if (flag == INIT_REQUEST) {
								if (!TextUtils.isEmpty(details.getBgtime())) {
									startTime = details.getBgtime();
									timeStartTv.setText(startTime);
									tmpStartHour = Integer.parseInt(startTime.substring(0, 2));
									tmpStartMinute = Integer.parseInt(startTime.substring(3, 5));
								}else {
									startTime = "00:00";
									timeStartTv.setText(startTime);
									tmpStartHour = 0;
									tmpStartMinute = 0;
								}
								if (!TextUtils.isEmpty(details.getEdtime())) {
									endTime = details.getEdtime();
									timeEndTv.setText(endTime);
									tmpEndHour = Integer.parseInt(endTime.substring(0, 2));
									tmpEndMinute = Integer.parseInt(endTime.substring(3, 5));
								}else {
									endTime = "00:00";
									timeEndTv.setText(endTime);
									tmpEndHour = 0;
									tmpEndMinute = 0;
								}
								
								shiftInfoTv.setText("班制信息："+details.getTimepart());
							}else if (flag == STARTDATE_REQUEST) {
								if (!TextUtils.isEmpty(details.getBgtime())) {
									startTime = details.getBgtime();
									timeStartTv.setText(startTime);
									tmpStartHour = Integer.parseInt(startTime.substring(0, 2));
									tmpStartMinute = Integer.parseInt(startTime.substring(3, 5));
								}else {
									startTime = "00:00";
									timeStartTv.setText(startTime);
									tmpStartHour = 0;
									tmpStartMinute = 0;
								}
								
								shiftInfoTv.setText("班制信息："+details.getTimepart());
							}else if (flag == ENDDATE_REQUEST) {
								if (!TextUtils.isEmpty(details.getEdtime())) {
									endTime = details.getEdtime();
									timeEndTv.setText(endTime);
									tmpEndHour = Integer.parseInt(endTime.substring(0, 2));
									tmpEndMinute = Integer.parseInt(endTime.substring(3, 5));
								}else {
									endTime = "00:00";
									timeEndTv.setText(endTime);
									tmpEndHour = 0;
									tmpEndMinute = 0;
								}
								
								shiftInfoTv.setText("班制信息："+details.getTimepart());
							}else if (flag == STARTTIME_REQUEST) {
								
							}else if (flag == STARTTIME_REQUEST) {
								
							}
						}else {
							Toasty.error(LeaveActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
				});
	}
	
	/**
	 * 提交
	 */
	private void doPost(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(LeaveActivity.this));
		params.addBodyParameter("cc_code", info.getCc_code());
		params.addBodyParameter("lerm_type", typeCode);
		params.addBodyParameter("reason", reasonEt.getText().toString().trim());
		params.addBodyParameter("proxy_emp", agencyCode);
		params.addBodyParameter("bgdate", startDate + " " + startTime);
		params.addBodyParameter("eddate", endDate + " " + endTime);
		params.addBodyParameter("day", details.getZb_day());
		params.addBodyParameter("zb_hour", details.getZb_hour());
		params.addBodyParameter("jb_hour", details.getJb_hour());
		params.addBodyParameter("bgdate_affect", details.getBgdate_affect());
		params.addBodyParameter("eddate_affect", details.getEddate_affect());
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA09AB&pcmd=Submit&charset=utf8",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
						Toasty.error(LeaveActivity.this, "网络请求失败", Toast.LENGTH_SHORT, true).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
						if (data.getStatus() == 0) {
							Toasty.success(LeaveActivity.this, "提交成功", Toast.LENGTH_SHORT, true).show();
							finish();
						}else if (data.getStatus() == 2) {
							Toasty.error(LeaveActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}else if (data.getStatus() == 1) {
							Toasty.error(LeaveActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(LeaveActivity.this);
							SceoApplication.getInstance().exit();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}

	private void initTitle() {
		title.setBackTitle("请假申请");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title.setConfirmBtnVisible();
		title.setConfirmText("提交");
		title.setConfirmClicklis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				hideSoftInput(reasonEt);
				
				//-----------------代理人--------------------
				if (TextUtils.isEmpty(agencyTv.getText().toString().trim())) {
					Toasty.warning(LeaveActivity.this, "必须找到职务代理人方可请假", Toast.LENGTH_SHORT, true).show();
					return;
				}
				if ((agencyTv.getText().toString().trim()).equals(SceoUtil.getEmpName(LeaveActivity.this))) {
					Toasty.warning(LeaveActivity.this, "不能将代理人设为自己", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//------------------时间--------------------
				if (TimeUtil.isBigger(endDate+" "+endTime, startDate +" "+startTime)) {
					Toasty.warning(LeaveActivity.this, "结束时间不能比开始时间早", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------请假开始时间不能小于月报锁定时间--------------------
				String lockTime = info.getApproval_date();
				if (TimeUtil.isBigger2(startDate, lockTime)) {
					Toasty.warning(LeaveActivity.this, "请假开始时间不能小于等于月报锁定时间", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------请假日期区间--------------------
				for (int i = 0; i < adltCodes.size(); i++) {
					if (adltCodes.get(i).equals(typeCode)) {//显示正班工时、正班天数、加班工时
						float tmp = Float.parseFloat(details.getZb_hour()) + Float.parseFloat(details.getJb_hour());
						if (tmp <= 0) {
							Toasty.warning(LeaveActivity.this, "请选择正确的请假日期区间", Toast.LENGTH_SHORT, true).show();
							return;
						}
					}else {//显示正班工时、正班天数
						float tmp = Float.parseFloat(details.getZb_hour());
						if (tmp <= 0) {
							Toasty.warning(LeaveActivity.this, "请选择正确的请假日期区间", Toast.LENGTH_SHORT, true).show();
							return;
						}
					}
				}
				//-------------------年假--------------------
				if (typeCode.equals("L006")) {
					if (TimeUtil.isBigger2(startDate, info.getBegdate())) {
						Toasty.warning(LeaveActivity.this, "请假开始时间与年假区间不符合", Toast.LENGTH_SHORT, true).show();
						return;
					}
					if (TimeUtil.isBigger2(info.getEnddate(), endDate)) {
						Toasty.warning(LeaveActivity.this, "请假结束时间与年假区间不符合", Toast.LENGTH_SHORT, true).show();
						return;
					}
					if (Float.parseFloat(info.getXynj_hour()) <= 0) {
						Toasty.warning(LeaveActivity.this, "没有年假", Toast.LENGTH_SHORT, true).show();
						return;
					}
					if (Float.parseFloat(info.getSynj_hour()) <= workingHour) {
						Toasty.warning(LeaveActivity.this, "年假不足", Toast.LENGTH_SHORT, true).show();
						return;
					}
				}
				//-------------------请假事由--------------------
				if (TextUtils.isEmpty(reasonEt.getText().toString().trim())) {
					Toasty.warning(LeaveActivity.this, "请填写请假事由", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//提交
				doPost();
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (!TextUtils.isEmpty(agencyTv.getText().toString().trim())
					|| !TextUtils.isEmpty(reasonEt.getText().toString().trim())) {
				new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
					.setTitleText("确定放弃当前编辑的内容？")
					.setCancelText("取消")
					.setConfirmText("确定")
					.showCancelButton(true)
					.setCancelClickListener(new OnSweetClickListener() {
						
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							sweetAlertDialog.dismiss();
						}
					})
					.setConfirmClickListener(new OnSweetClickListener() {
						
						@Override
						public void onClick(SweetAlertDialog sweetAlertDialog) {
							sweetAlertDialog.dismiss();
							finish();
						}
					})
					.show();
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
