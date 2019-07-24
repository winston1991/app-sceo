package com.huntkey.software.sceo.ui.activity.invoicedetails;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.InvoiceBtnReturnData;
import com.huntkey.software.sceo.bean.InvoiceActionBtnData;
import com.huntkey.software.sceo.bean.InvoiceActionBtnDetails;
import com.huntkey.software.sceo.bean.InvoiceBtnReturnDetails;
import com.huntkey.software.sceo.bean.InvoiceDetails;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.WebViewActivity;
import com.huntkey.software.sceo.utils.SceoUtil;
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

import es.dmoral.toasty.Toasty;

/**
 * 待审单据详情
 * @author chenliang3
 *
 */
public class InvoiceDetailsActivity extends BaseActivity {

	@ViewInject(R.id.invoice_details_title)
	BackTitle title;
	@ViewInject(R.id.invoice_details_number)
	TextView invoiceNumber;
	@ViewInject(R.id.invoice_details_name)
	TextView invoiceName;
	@ViewInject(R.id.invoice_details_depart)
	TextView invoiceDepart;
	@ViewInject(R.id.invoice_details_time)
	TextView invoiceTime;
	@ViewInject(R.id.invoice_details_digest)
	TextView invoiceDigest;//摘要
	@ViewInject(R.id.invoice_details_n1)
	TextView invoiceN1;//申请人
	@ViewInject(R.id.invoice_details_n2)
	TextView invoiceN2;//审核人
	@ViewInject(R.id.invoice_details_n3)
	TextView invoiceN3;//复核人
	@ViewInject(R.id.invoice_details_n4)
	TextView invoiceN4;//批准人
	@ViewInject(R.id.invoice_details_c1)
	TextView invoiceC1;//申请人审核人数
	@ViewInject(R.id.invoice_details_c2)
	TextView invoiceC2;//审核人审核人数
	@ViewInject(R.id.invoice_details_c3)
	TextView invoiceC3;//复核人审核人数
	@ViewInject(R.id.invoice_details_c4)
	TextView invoiceC4;//批准人审核人数
	@ViewInject(R.id.invoice_details_explain)
	EditText invoiceExplain;//说明
	@ViewInject(R.id.invoice_details_btn_pass)
	Button btnPass;//通过
	@ViewInject(R.id.invoice_details_btn_return)
	Button btnReturn;//返回
	@ViewInject(R.id.invoice_details_btn_backout)
	Button btnBackout;//撤销
	@ViewInject(R.id.invoice_details_btn_jointly)
	Button btnJointly;//联名
	@ViewInject(R.id.invoice_details_btn_detail)
	Button btnDetail;//知会
	@ViewInject(R.id.invoice_details_layout_d)
	RelativeLayout layoutDetail;
	@ViewInject(R.id.invoice_details_layout4)
	LinearLayout invoiceFlowLayout;
	
	private InvoiceDetails details;
	private LoadingDialog loadingDialog;
	private int RESULT_CODE = 0x24;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice_details);
		ViewUtils.inject(this);
		
		details = (InvoiceDetails) getIntent().getSerializableExtra("details");
		
		initTitle();
		initView();
	}

	private void initView() {
		loadingDialog = new LoadingDialog(InvoiceDetailsActivity.this);
		
		invoiceNumber.setText(details.getWfna_nbr());
		invoiceName.setText(details.getWfm_name());
		invoiceDepart.setText(details.getWfna_apply_dept());
		invoiceTime.setText(details.getWfna_addtime());
		invoiceDigest.setText("内容摘要：\n" + details.getWfna_summary());
		
		invoiceN1.setText(details.getShowname_n1());
		if (details.getColor_n1() != null && !"".equals(details.getColor_n1())) {
			invoiceN1.setTextColor(Color.parseColor(details.getColor_n1()));
		}
		if (details.getBcolor_n1() != null && !"".equals(details.getBcolor_n1())) {
			invoiceN1.setBackgroundColor(Color.parseColor(details.getBcolor_n1()));
		}
		invoiceN2.setText(details.getShowname_n2());
		if (details.getColor_n2() != null && !"".equals(details.getColor_n2())) {
			invoiceN2.setTextColor(Color.parseColor(details.getColor_n2()));
		}
		if (details.getBcolor_n2() != null && !"".equals(details.getBcolor_n2())) {
			invoiceN2.setBackgroundColor(Color.parseColor(details.getBcolor_n2()));
		}
		invoiceN3.setText(details.getShowname_n3());
		if (details.getColor_n3() != null && !"".equals(details.getColor_n3())) {
			invoiceN3.setTextColor(Color.parseColor(details.getColor_n3()));
		}
		if (details.getBcolor_n3() != null && !"".equals(details.getBcolor_n3())) {
			invoiceN3.setBackgroundColor(Color.parseColor(details.getBcolor_n3()));
		}
		invoiceN4.setText(details.getShowname_n4());
		if (details.getColor_n4() != null && !"".equals(details.getColor_n4())) {
			invoiceN4.setTextColor(Color.parseColor(details.getColor_n4()));
		}
		if (details.getBcolor_n4() != null && !"".equals(details.getBcolor_n4())) {
			invoiceN4.setBackgroundColor(Color.parseColor(details.getBcolor_n4()));
		}
		
		showNameCountControl(details.getShowname_count1(), invoiceC1);
		showNameCountControl(details.getShowname_count2(), invoiceC2);
		showNameCountControl(details.getShowname_count3(), invoiceC3);
		showNameCountControl(details.getShowname_count4(), invoiceC4);
		
		layoutDetail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String webUrl = Conf.SERVICE_URL;
				if (details.getWeburl() != null && !"".equals(details.getWeburl())) {
					if (!details.getWeburl().contains("http://")) {
						webUrl = webUrl + details.getWeburl();
					}
					Intent intent = new Intent(InvoiceDetailsActivity.this, WebViewActivity.class);
					intent.putExtra("webUrl", webUrl);
					intent.putExtra("titleName", details.getWfm_name());
					startActivity(intent);					
				}
			}
		});
		
		invoiceFlowLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(InvoiceDetailsActivity.this, InvoiceFlowActivity.class);
				intent.putExtra("title", details.getWfm_name());
				intent.putExtra("mid", details.getMid());
				intent.putExtra("nid", details.getNid());
				intent.putExtra("wfid", details.getWfid());
				intent.putExtra("lid", details.getLid());
				intent.putExtra("count1", details.getShowname_count1());
				intent.putExtra("count2", details.getShowname_count2());
				intent.putExtra("count3", details.getShowname_count3());
				intent.putExtra("count4", details.getShowname_count4());
				intent.putExtra("invoiceNumber", details.getWfna_nbr());
				startActivity(intent);
			}
		});
		
		getActionBtn();
	}
	
	private void showNameCountControl(String count, TextView textView){
		if (count.length() < 10 && !"0".equals(count) && !"1".equals(count)) {
			textView.setText(count);
		}else if ("0".equals(count) || "1".equals(count)) {
			textView.setText("");
		}else {
			textView.setText("*");
		}
	}
	
	private void getActionBtn(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(InvoiceDetailsActivity.this));
		params.addBodyParameter("mid", details.getMid()+"");
		params.addBodyParameter("wfid", details.getWfid()+"");
		params.addBodyParameter("nid", details.getNid()+"");
		params.addBodyParameter("lid", details.getLid()+"");
		params.addBodyParameter("aid", details.getAid()+"");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA01AB&pcmd=GetActionBtn",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						InvoiceActionBtnData data = SceoUtil.parseJson(responseInfo.result, InvoiceActionBtnData.class);
						if (data.getStatus() == 0) {
							List<InvoiceActionBtnDetails> detail = data.getData().getBtnlist();
							for (int i = 0; i < detail.size(); i++) {
								switch (detail.get(i).getAction_code()) {
								case "01"://通过
									if (detail.get(i).getEnable() == 1) {										
										btnPass.setVisibility(View.VISIBLE);
										btnPass.setOnClickListener(new MyOnclickListener(detail.get(i).getAction_code(), detail.get(i).getAid()));
									}
									break;
								case "02"://退回
									if (detail.get(i).getEnable() == 1) {										
										btnReturn.setVisibility(View.VISIBLE);
										btnReturn.setOnClickListener(new MyOnclickListener(detail.get(i).getAction_code(), detail.get(i).getAid()));
									}
									break;
								case "05"://撤销
									if (detail.get(i).getEnable() == 1) {										
										btnBackout.setVisibility(View.VISIBLE);
										btnBackout.setOnClickListener(new MyOnclickListener(detail.get(i).getAction_code(), detail.get(i).getAid()));
									}
									break;
								case "04"://知会
									if (detail.get(i).getEnable() == 1) {
										btnDetail.setVisibility(View.VISIBLE);
										btnDetail.setOnClickListener(new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												Intent intent = new Intent(InvoiceDetailsActivity.this, InvoiceJointlyActivity.class);
												intent.putExtra("aid", details.getAid());
												intent.putExtra("wfid", details.getWfid());
												intent.putExtra("mid", details.getMid());
												intent.putExtra("union_type", 1);
												startActivity(intent);
											}
										});
									}
									break;
								case "06":
									if (detail.get(i).getEnable() == 1) {										
										btnJointly.setVisibility(View.VISIBLE);
										btnJointly.setOnClickListener(new OnClickListener() {
											
											@Override
											public void onClick(View v) {
												Intent intent = new Intent(InvoiceDetailsActivity.this, InvoiceJointlyActivity.class);
												intent.putExtra("aid", details.getAid());
												intent.putExtra("wfid", details.getWfid());
												intent.putExtra("mid", details.getMid());
												intent.putExtra("union_type", 0);
												startActivity(intent);
											}
										});
									}
									break;

								default:
									break;
								}
							}
							
							int stepId = data.getData().getStepid() + 1;
							String empName = data.getData().getEmpname();
							String color = data.getData().getColor();
							
							if (stepId > 0 && empName != null && color != null) {
								switch (stepId) {
								case 1:
									invoiceN1.setText(empName);
									invoiceN1.setTextColor(Color.parseColor(color));
									break;
								case 2:
									invoiceN2.setText(empName);
									invoiceN2.setTextColor(Color.parseColor(color));
									break;
								case 3:
									invoiceN3.setText(empName);
									invoiceN3.setTextColor(Color.parseColor(color));
									break;
								case 4:
									invoiceN4.setText(empName);
									invoiceN4.setTextColor(Color.parseColor(color));
									break;

								default:
									break;
								}
								
								Intent intent = new Intent();
								intent.putExtra("BstepId", stepId);
								intent.putExtra("BempName", empName);
								intent.putExtra("Bcolor", color);
								setResult(RESULT_CODE, intent);
							}
						}else if (data.getStatus() == 88) {
							Toasty.error(InvoiceDetailsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(InvoiceDetailsActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(InvoiceDetailsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
						
						loadingDialog.dismiss();
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	private class MyOnclickListener implements OnClickListener{

		private String action_code;
		private int aid;
		private int type;
		private String title;
		
		public MyOnclickListener(String actionCode, int aid){
			this.action_code = actionCode;
			this.aid = aid;
		}
		
		@Override
		public void onClick(View v) {
			switch (action_code) {
			case "01":
				type = SweetAlertDialog.SUCCESS_TYPE;
				title = "是否确认通过？";
				break;
			case "02":
				type = SweetAlertDialog.WARNING_TYPE;
				title = "是否确认退回？";
				break;
			case "05":
				type = SweetAlertDialog.WARNING_TYPE;
				title = "是否确认撤销？";
				break;

			default:
				break;
			}
			
			new SweetAlertDialog(InvoiceDetailsActivity.this, type)
				.setTitleText(title)
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
						String explain = invoiceExplain.getText().toString().trim();
//						if ("02".equals(action_code)) {
//							if (explain == null || "".equals(explain)) {
//								Toasty.warning(InvoiceDetailsActivity.this, "审核说明不能为空", Toast.LENGTH_SHORT, true).show();
//								return;
//							}
//						}
						RequestParams params = new RequestParams();
						params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(InvoiceDetailsActivity.this));
						params.addBodyParameter("mid", details.getMid()+"");
						params.addBodyParameter("wfid", details.getWfid()+"");
						params.addBodyParameter("nid", details.getNid()+"");
						params.addBodyParameter("lid", details.getLid()+"");
						params.addBodyParameter("aid", aid+"");
						params.addBodyParameter("snid", details.getSnid()+""); 
						params.addBodyParameter("action_code", action_code);
						params.addBodyParameter("aut_desc", explain);
						HttpUtils http = new HttpUtils();
						http.configResponseTextCharset("GB2312");
						http.send(HttpRequest.HttpMethod.POST, 
								Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA01AB&pcmd=Check&charset=utf8",
								params, 
								new RequestCallBack<String>() {
							
							@Override
							public void onFailure(HttpException arg0, String arg1) {
								
							}
							
							@Override
							public void onSuccess(ResponseInfo<String> responseInfo) {
								InvoiceBtnReturnData data = SceoUtil.parseJson(responseInfo.result, InvoiceBtnReturnData.class);
								Toasty.success(InvoiceDetailsActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
								if (data.getStatus() == 0) {		
									InvoiceBtnReturnDetails returnDetails = data.getData();
									
									Intent intent = new Intent();
									intent.putExtra("isChange", true);
									intent.putExtra("stepId", returnDetails.getStepid());
									intent.putExtra("name", returnDetails.getEmpname());
									intent.putExtra("color", returnDetails.getColor());
									setResult(Activity.RESULT_OK, intent);
									
									InvoiceDetailsActivity.this.finish();
								}
								
								loadingDialog.dismiss();
							}
							
							@Override
							public void onStart() {
								super.onStart();
								loadingDialog.show();
							}
						});
					}
				})
				.show();
		}
		
	}

	private void initTitle() {
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InvoiceDetailsActivity.this.finish();
			}
		});
		title.setBackTitle(details.getWfm_name());
		title.setBackTitleColor(getResources().getColor(R.color.white));
        if (details.getWfna_nbr().contains("AD10")){//园区出入单 附件管理
            title.setAccessoryBtnVisible();
            title.setAccessoryBtnClickLis(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(InvoiceDetailsActivity.this, InvoiceAccessoryActivity.class);
					intent.putExtra("mid", String.valueOf(details.getMid()));
					startActivity(intent);
                }
            });
        }
	}
}
