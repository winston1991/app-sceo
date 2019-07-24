package com.huntkey.software.sceo.ui.activity.business;

import java.util.Calendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.BaseData;
import com.huntkey.software.sceo.bean.BusinessDurationData;
import com.huntkey.software.sceo.bean.BusinessDurationInfo;
import com.huntkey.software.sceo.bean.BusinessInitData;
import com.huntkey.software.sceo.bean.BusinessInitInfo;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.VehicleDialogAdapter;
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
 * 出差申请
 * @author chenliang3
 *
 */
public class BusinessActivity extends BaseActivity {

	@ViewInject(R.id.business_title)
	BackTitle title;
	@ViewInject(R.id.business_vehicle_layout)
	RelativeLayout vehicleLayout;//交通工具btn
	@ViewInject(R.id.business_vehicle_tv)
	TextView vehicleTv;//交通工具
	@ViewInject(R.id.business_local_et)
	EditText localEt;//出差地点
	@ViewInject(R.id.business_num_et)
	EditText numberEt;//同行人数
	@ViewInject(R.id.business_travel_charge_et)
	EditText chargeEt;//预计差旅费
	@ViewInject(R.id.business_money_et)
	EditText moneyEt;//预计活动费
	@ViewInject(R.id.business_date_start_layout)
	RelativeLayout dateStartLayout;//开始日期btn
	@ViewInject(R.id.business_date_start)
	TextView dateStartTv;//开始日期
	@ViewInject(R.id.business_time_start_layout)
	RelativeLayout timeStartLayout;//开始时间btn
	@ViewInject(R.id.business_time_start)
	TextView timeStartTv;//开始时间
	@ViewInject(R.id.business_date_end_layout)
	RelativeLayout dateEndLayout;//结束日期btn
	@ViewInject(R.id.business_date_end)
	TextView dateEndTv;//结束日期
	@ViewInject(R.id.business_time_end_layout)
	RelativeLayout timeEndLayout;//结束时间btn
	@ViewInject(R.id.business_time_end)
	TextView timeEndTv;//结束时间
	@ViewInject(R.id.business_normal_duration_tv)
	TextView zbgsTv;//正班工时
	@ViewInject(R.id.business_add_duration_layout)
	RelativeLayout jbgsLayout;
	@ViewInject(R.id.business_add_duration_tv)
	TextView jbgsTv;//加班工时
	@ViewInject(R.id.business_normal_day_tv)
	TextView zbtsTv;//正班天数
	@ViewInject(R.id.business_reason)
	EditText reasonEt;//出差目的
	@ViewInject(R.id.business_plan)
	EditText planEt;//出差计划
	@ViewInject(R.id.business_info)
	TextView infoEt;//累计出差信息
	
	private static final String DATEPICKER_TAG = "datepicker";
	private static final String TIMEPICKER_TAG = "timepicker";
	private static final int INIT_REQUEST = 0;//getDurationData()的默认请求
	private static final int STARTDATE_REQUEST = 1;//点击开始时间发出的请求
	private static final int ENDDATE_REQUEST = 2;//点击结束时间发出的请求
	private static final int STARTTIME_REQUEST = 3;//点击开始时间发出的请求
	private static final int ENDTIME_REQUEST = 4;//点击结束时间发出的请求
	
	private String vehicleType;//交通方式
	private String startDate;//开始日期
	private String startTime = "";//开始时间
	private String endDate;//结束日期
	private String endTime = "";//结束时间
	
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
	
	private LoadingDialog loadingDialog;
	private String[] vehicleArray = {"汽车", "火车", "飞机", "其他"};
	private BusinessInitInfo initInfo;
	private BusinessDurationInfo durationInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business);
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
		loadingDialog = new LoadingDialog(BusinessActivity.this);
		
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
		
		MyOnclickLis myOnclickLis = new MyOnclickLis();
		vehicleLayout.setOnClickListener(myOnclickLis);
		dateStartLayout.setOnClickListener(myOnclickLis);
		timeStartLayout.setOnClickListener(myOnclickLis);
		dateEndLayout.setOnClickListener(myOnclickLis);
		timeEndLayout.setOnClickListener(myOnclickLis);
		
		getInitData();
	}
	
	private class MyOnclickLis implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.business_vehicle_layout://交通工具
				initVehicleDialog();
				break;
			case R.id.business_date_start_layout://起始日期
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
			case R.id.business_time_start_layout://起始时间
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
			case R.id.business_date_end_layout://结束日期
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
			case R.id.business_time_end_layout://结束时间
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

	private void getInitData() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(BusinessActivity.this));
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA10AB&pcmd=GetInitData",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						BusinessInitData data = SceoUtil.parseJson(responseInfo.result, BusinessInitData.class);
						if (data.getStatus() == 0) {
							initInfo = data.getData();
							
							if ("0".equals(initInfo.getIfovleave())) {
								jbgsLayout.setVisibility(View.GONE);
							}else {
								jbgsLayout.setVisibility(View.VISIBLE);
							}
							
							infoEt.setText("年度已累计出差次数："+initInfo.getFyear_times()+"次\n"+
									"年度已累计出差天数："+initInfo.getFyear_days()+"天");
							
							getDurationData(INIT_REQUEST);
						}else {
							Toasty.error(BusinessActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	private void getDurationData(final int flag){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(BusinessActivity.this));
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
						BusinessDurationData data = SceoUtil.parseJson(responseInfo.result, BusinessDurationData.class);
						if (data.getStatus() == 0) {
							durationInfo = data.getData();
							
							zbgsTv.setText(durationInfo.getZb_hour()+"");
							jbgsTv.setText(durationInfo.getJb_hour()+"");
							zbtsTv.setText(durationInfo.getZb_day()+"");
							
							if (flag == INIT_REQUEST) {
								if (!TextUtils.isEmpty(durationInfo.getBgtime())) {
									startTime = durationInfo.getBgtime();
									timeStartTv.setText(startTime);
									tmpStartHour = Integer.parseInt(startTime.substring(0, 2));
									tmpStartMinute = Integer.parseInt(startTime.substring(3, 5));
								}else {
									startTime = "00:00";
									timeStartTv.setText(startTime);
									tmpStartHour = 0;
									tmpStartMinute = 0;
								}
								if (!TextUtils.isEmpty(durationInfo.getEdtime())) {
									endTime = durationInfo.getEdtime();
									timeEndTv.setText(endTime);
									tmpEndHour = Integer.parseInt(endTime.substring(0, 2));
									tmpEndMinute = Integer.parseInt(endTime.substring(3, 5));
								}else {
									endTime = "00:00";
									timeEndTv.setText(endTime);
									tmpEndHour = 0;
									tmpEndMinute = 0;
								}
							}else if (flag == STARTDATE_REQUEST) {
								if (!TextUtils.isEmpty(durationInfo.getBgtime())) {
									startTime = durationInfo.getBgtime();
									timeStartTv.setText(startTime);
									tmpStartHour = Integer.parseInt(startTime.substring(0, 2));
									tmpStartMinute = Integer.parseInt(startTime.substring(3, 5));
								}else {
									startTime = "00:00";
									timeStartTv.setText(startTime);
									tmpStartHour = 0;
									tmpStartMinute = 0;
								}
							}else if (flag == ENDDATE_REQUEST) {
								if (!TextUtils.isEmpty(durationInfo.getEdtime())) {
									endTime = durationInfo.getEdtime();
									timeEndTv.setText(endTime);
									tmpEndHour = Integer.parseInt(endTime.substring(0, 2));
									tmpEndMinute = Integer.parseInt(endTime.substring(3, 5));
								}else {
									endTime = "00:00";
									timeEndTv.setText(endTime);
									tmpEndHour = 0;
									tmpEndMinute = 0;
								}
							}else if (flag == STARTTIME_REQUEST) {
								
							}else if (flag == STARTTIME_REQUEST) {
								
							}
						}else {
							Toasty.error(BusinessActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
				});
	}
	
	private void doPost(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(BusinessActivity.this));
		params.addBodyParameter("cc_code", initInfo.getCc_code());
		params.addBodyParameter("reason", reasonEt.getText().toString().trim());
		params.addBodyParameter("bgdate", startDate + " " + startTime);
		params.addBodyParameter("eddate", endDate + " " + endTime);
		params.addBodyParameter("day", durationInfo.getQj_day()+"");
		params.addBodyParameter("zb_hour", durationInfo.getZb_hour()+"");
		params.addBodyParameter("jb_hour", durationInfo.getJb_hour()+"");
		params.addBodyParameter("bgdate_affect", durationInfo.getBgdate_affect());
		params.addBodyParameter("eddate_affect", durationInfo.getEddate_affect());
		params.addBodyParameter("lvrq_bt_cost", chargeEt.getText().toString().trim());
		params.addBodyParameter("lvrq_content", planEt.getText().toString().trim());
		params.addBodyParameter("lvrq_cost", moneyEt.getText().toString().trim());
		params.addBodyParameter("lvrq_person_num", numberEt.getText().toString().trim());
		params.addBodyParameter("lvrq_place", localEt.getText().toString().trim());
		params.addBodyParameter("lvrq_vehicle", vehicleType);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA10AB&pcmd=Submit&charset=utf8",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
						Toasty.error(BusinessActivity.this, "提交失败", Toast.LENGTH_SHORT, true).show();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						BaseData data = SceoUtil.parseJson(responseInfo.result, BaseData.class);
						if (data.getStatus() == 0) {
							Toasty.success(BusinessActivity.this, "提交成功", Toast.LENGTH_SHORT, true).show();
							finish();
						}else if (data.getStatus() == 2) {
							Toasty.error(BusinessActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}else if (data.getStatus() == 1) {
							Toasty.error(BusinessActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(BusinessActivity.this);
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

	private void initVehicleDialog() {
		LayoutInflater mInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = mInflater.inflate(R.layout.dialog_vehicle, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(BusinessActivity.this);
		builder.setView(view);
		final AlertDialog dialog = builder.show();
		
		ListView vehicleListView = (ListView) dialog.getWindow().findViewById(R.id.vehicle_listview);
		VehicleDialogAdapter adapter = new VehicleDialogAdapter(BusinessActivity.this, vehicleArray);
		vehicleListView.setAdapter(adapter);
		
		vehicleListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				dialog.dismiss();
				vehicleType = vehicleArray[position];
				vehicleTv.setText(vehicleArray[position]);
			}
		});
	}

	private void initTitle() {
		title.setBackTitle("出差申请");
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
				
				//-------------------交通工具--------------------
				if (TextUtils.isEmpty(vehicleTv.getText().toString().trim())) {
					Toasty.warning(BusinessActivity.this, "请选择交通工具", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------出差地点--------------------
				if (TextUtils.isEmpty(localEt.getText().toString().trim())) {
					Toasty.warning(BusinessActivity.this, "请输入出差地点", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------同行人数--------------------
				if (TextUtils.isEmpty(numberEt.getText().toString().trim())) {
					Toasty.warning(BusinessActivity.this, "请输入同行人数", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------预计差旅费--------------------
				if (TextUtils.isEmpty(chargeEt.getText().toString().trim())) {
					Toasty.warning(BusinessActivity.this, "请输入预计差旅费", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------预计活动费--------------------
				if (TextUtils.isEmpty(moneyEt.getText().toString().trim())) {
					Toasty.warning(BusinessActivity.this, "请输入预计活动费", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------时间--------------------
				if (TimeUtil.isBigger(endDate+" "+endTime, startDate +" "+startTime)) {
					Toasty.warning(BusinessActivity.this, "结束时间不能比开始时间早", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------开始时间不能小于月报锁定时间--------------------
				String lockTime = initInfo.getApproval_date();
				if (TimeUtil.isBigger2(startDate, lockTime)) {
					Toasty.warning(BusinessActivity.this, "请假开始时间不能小于等于月报锁定时间", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------出差日期区间--------------------
				if ("0".equals(initInfo.getIfovleave())) {
					if (durationInfo.getZb_hour() <= 0) {
						Toasty.warning(BusinessActivity.this, "请选择正确的请假日期区间", Toast.LENGTH_SHORT, true).show();
						return;
					}
				}else {
					if (durationInfo.getZb_hour() + durationInfo.getJb_hour() <= 0) {
						Toasty.warning(BusinessActivity.this, "请选择正确的请假日期区间", Toast.LENGTH_SHORT, true).show();
						return;
					}
				}
				//-------------------出差目的--------------------
				if (TextUtils.isEmpty(reasonEt.getText().toString().trim())) {
					Toasty.warning(BusinessActivity.this, "请输入出差目的", Toast.LENGTH_SHORT, true).show();
					return;
				}
				//-------------------出差计划--------------------
				if (TextUtils.isEmpty(planEt.getText().toString().trim())) {
					Toasty.warning(BusinessActivity.this, "请输入出差计划", Toast.LENGTH_SHORT, true).show();
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
			if (!TextUtils.isEmpty(localEt.getText().toString().trim())
					|| !TextUtils.isEmpty(numberEt.getText().toString().trim())
					|| !TextUtils.isEmpty(chargeEt.getText().toString().trim())
					|| !TextUtils.isEmpty(moneyEt.getText().toString().trim())
					|| !TextUtils.isEmpty(reasonEt.getText().toString().trim())
					|| !TextUtils.isEmpty(planEt.getText().toString().trim())) {
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
