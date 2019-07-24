package com.huntkey.software.sceo.ui.activity.invoicedetails;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.JointlyListData;
import com.huntkey.software.sceo.bean.JointlyListDetails;
import com.huntkey.software.sceo.bean.JointlyListInfo;
import com.huntkey.software.sceo.bean.SearchResultData;
import com.huntkey.software.sceo.bean.SearchResultDetails;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.InvoiceJointlyAdapter;
import com.huntkey.software.sceo.ui.adapter.InvoiceJointlyAdapter.JointlyViewHolder;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
import com.huntkey.software.sceo.widget.ClearEditText;
import com.huntkey.software.sceo.widget.ErrorView;
import com.huntkey.software.sceo.widget.ErrorView.RetryListener;
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
 * 待审单据联名
 * @author chenliang3
 *
 */
public class InvoiceJointlyActivity extends BaseActivity {

	@ViewInject(R.id.invoice_jointly_title)
	BackTitle title;
	@ViewInject(R.id.invoice_jointly_lLayout)
	LinearLayout layout;
	@ViewInject(R.id.invoice_jointly_clear_edittext)
	ClearEditText cEditText;
	@ViewInject(R.id.invoice_jointly_listView)
	ListView listView;
	@ViewInject(R.id.invoice_jointly_error)
	ErrorView errorView;
	@ViewInject(R.id.invoice_jointly_todo)
	TextView todoBtn;
	
	private int aid;
	private int wfid;
	private int mid;
	private LoadingDialog loadingDialog;
	private List<SearchResultDetails> searchDetails;
	private List<JointlyListDetails> jointlyDetails;//原有的
	private List<JointlyListDetails> jointlyAdds = new ArrayList<JointlyListDetails>();//新增的,传给后台时，只传新增的
	private JointlyListInfo jointlyInfo;
	private InvoiceJointlyAdapter adapter;
	private JointlyViewHolder viewHolder;
	private String input;
	private int union_type;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice_jointly);
		ViewUtils.inject(this);
		
		aid = getIntent().getIntExtra("aid", 0);
		wfid = getIntent().getIntExtra("wfid", 0);
		mid = getIntent().getIntExtra("mid", 0);
		union_type = getIntent().getIntExtra("union_type", 0);
		loadingDialog = new LoadingDialog(InvoiceJointlyActivity.this);
		
		initTitleAndErrorView();
		initJointlyList();
		initSearch();
	}

	private void initJointlyList() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(InvoiceJointlyActivity.this));
		params.addBodyParameter("aid", aid+"");
		params.addBodyParameter("wfid", wfid+"");
		params.addBodyParameter("union_type", union_type+"");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA01AB&pcmd=GetUnionTaskList",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						JointlyListData data = SceoUtil.parseJson(responseInfo.result, JointlyListData.class);
						if (data.getStatus() == 0) {
							jointlyInfo = data.getData();
							jointlyDetails = data.getData().getList();
							
							for (int i = 0; i < jointlyDetails.size(); i++) {
								showCheckImage(jointlyDetails.get(i));
							}
						}else if (data.getStatus() == 88) {
							Toasty.error(InvoiceJointlyActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(InvoiceJointlyActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(InvoiceJointlyActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	//即时显示选中用户
	private void showCheckImage(final JointlyListDetails details){
		LinearLayout.LayoutParams menuLinerLayoutParames = new LinearLayout.LayoutParams(
                108, 208, 1);
        final View view = LayoutInflater.from(this).inflate(
                R.layout.invoice_jointly_head_item, null);
        ImageView images = (ImageView) view.findViewById(R.id.invoice_jointly_head_avatar);
        TextView texts = (TextView) view.findViewById(R.id.invoice_jointly_head_name);
        menuLinerLayoutParames.setMargins(5, 0, 5, 0);
        
        //设置id，方便后面删除
        view.setTag(details.getEmp_id());
        
        view.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				layout.removeView(view);
				if (jointlyAdds.contains(details) ) {
					jointlyAdds.remove(details);
					if (adapter != null) {	
						adapter = new InvoiceJointlyAdapter(InvoiceJointlyActivity.this, searchDetails, 
								jointlyDetails, jointlyAdds);
						listView.setAdapter(adapter);
					}
				}else if (jointlyDetails.contains(details)) {
					jointlyDetails.remove(details);
					setDelete(details);
				}
				
				return true;
			}
		});
        
		Glide
				.with(InvoiceJointlyActivity.this)
				.load(details.getPhoto())
				.centerCrop()
				.placeholder(R.drawable.ic_avatar)
				.crossFade()
				.into(images);
        texts.setText(details.getEmp_name());
        
        layout.addView(view, menuLinerLayoutParames);
	}
	
	private void deleteImage(JointlyListDetails details){
		View view = (View) layout.findViewWithTag(details.getEmp_id());
		layout.removeView(view);
	}
	
	private void initSearch() {
		todoBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				input = cEditText.getText().toString().trim();
				if (input != null && !"".equals(input)) {
					doSearchNetWork(input);
				}
			}
		});
	}
	
	private void doSearchNetWork(String input){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(InvoiceJointlyActivity.this));
		params.addBodyParameter("deptid", "");
		params.addBodyParameter("key", input);
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA04AC&pcmd=getSearchResult&charset=utf8",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						errorView.setVisibility(View.VISIBLE);
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						errorView.setVisibility(View.GONE);
						loadingDialog.dismiss();
						
						SearchResultData data = SceoUtil.parseJson(responseInfo.result, SearchResultData.class);
						if (data.getStatus() == 0) {
							searchDetails = data.getData().getColleague();
							adapter = new InvoiceJointlyAdapter(InvoiceJointlyActivity.this, searchDetails, 
									jointlyDetails, jointlyAdds);
							listView.setAdapter(adapter);
							
							JointlyListDetails jld = new JointlyListDetails();
							listView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {
									if (view.getTag() instanceof JointlyViewHolder) {					
										viewHolder = (JointlyViewHolder) view.getTag();
										
										//自动触发checkbox的checked事件
										viewHolder.checkBox.toggle();
										
										SearchResultDetails srd = searchDetails.get(position);
										JointlyListDetails jld = new JointlyListDetails();
										jld.setEmp_id(srd.getEmp_id());
										jld.setEmp_name(srd.getEmp_name());
										jld.setPhoto(srd.getPhoto());
										jld.setWfna_id("0");
								        
										if (viewHolder.checkBox.isChecked()) {
											showCheckImage(jld);
											jointlyAdds.add(jld);
											
											adapter.updateAdds(jointlyDetails, jointlyAdds);
											adapter.notifyDataSetChanged();
											listView.setAdapter(adapter);
										}else {
											deleteImage(jld);
											jointlyAdds.remove(jld);
										}
									}
								}
							});
						}else if (data.getStatus() == 88) {
							Toasty.error(InvoiceJointlyActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(InvoiceJointlyActivity.this);
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(InvoiceJointlyActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}

	private void initTitleAndErrorView() {
		if (union_type == 0) {			
			title.setBackTitle("联名");
		}else {
			title.setBackTitle("知会");
		}
		title.setBackTitleColor(getResources().getColor(R.color.white));
		
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		title.setConfirmBtnVisible();
		title.setConfirmClicklis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//确定
				if (jointlyAdds.size() > 0) {					
					setConfirm();
				}else {
					finish();
				}
			}
		});
		
		errorView.setOnRetryListener(new RetryListener() {
			
			@Override
			public void onRetry() {
				doSearchNetWork(input);
			}
		});
	}
	
	private void setConfirm(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(InvoiceJointlyActivity.this));
		params.addBodyParameter("aid", aid+"");
		params.addBodyParameter("wfid", wfid+"");
		params.addBodyParameter("mid", mid+"");
		params.addBodyParameter("uk_emp_ids", getEmpIds(jointlyAdds));
		params.addBodyParameter("uk_type", getUkTypes(jointlyInfo));
		params.addBodyParameter("uk_seq", getUkSeqs(jointlyInfo));
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA01AB&pcmd=SubmitUnionTaskList",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						if (union_type == 0) {							
							Toasty.success(InvoiceJointlyActivity.this, "联名设置成功", Toast.LENGTH_SHORT, true).show();
						}else {
							Toasty.success(InvoiceJointlyActivity.this, "知会设置成功", Toast.LENGTH_SHORT, true).show();
						}
						InvoiceJointlyActivity.this.finish();
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
	private String getEmpIds(List<JointlyListDetails> details){
		String tmpString = null;
		for (int i = 0; i < details.size(); i++) {
			String s = details.get(i).getEmp_id() + ",";
			tmpString += s;
		}
		tmpString = tmpString.replace("null", "");
		tmpString = tmpString.substring(0, tmpString.length() - 1);
		return tmpString;
	}
	
	private String getUkTypes(JointlyListInfo jointlyInfo){
		String tmpString = null;
		for (int i = 0; i < jointlyAdds.size(); i++) {
			String s = jointlyInfo.getUk_type() + ",";
			tmpString += s;
		}
		tmpString = tmpString.replace("null", "");
		tmpString = tmpString.substring(0, tmpString.length() - 1);
		return tmpString;
	}
	
	private String getUkSeqs(JointlyListInfo jointlyInfo){
		String tmpString = null;
		for (int i = 0; i < jointlyAdds.size(); i++) {
			if (union_type == 0) {				
				String s = jointlyInfo.getUk_seq() + ",";
				tmpString += s;
			}else {
				tmpString += "13" + ",";
			}
		}
		tmpString = tmpString.replace("null", "");
		tmpString = tmpString.substring(0, tmpString.length() - 1);
		return tmpString;
	}
	
	private void setDelete(JointlyListDetails details){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(InvoiceJointlyActivity.this));
		params.addBodyParameter("wfna_id", details.getWfna_id());
		params.addBodyParameter("wfid", wfid+"");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA01AB&pcmd=DeleteUnionTask",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						if (adapter != null) {	
							adapter = new InvoiceJointlyAdapter(InvoiceJointlyActivity.this, searchDetails, 
									jointlyDetails, jointlyAdds);
							listView.setAdapter(adapter);
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadingDialog.show();
					}
				});
	}
	
}
