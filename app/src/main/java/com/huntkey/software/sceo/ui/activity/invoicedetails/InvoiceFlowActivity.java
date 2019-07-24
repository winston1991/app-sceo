package com.huntkey.software.sceo.ui.activity.invoicedetails;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.InvoiceFlowData;
import com.huntkey.software.sceo.bean.InvoiceFlowDetails;
import com.huntkey.software.sceo.ui.LoadingDialog;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.adapter.InvoiceFlowAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.BackTitle;
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
 * 审核流程详情
 * @author chenliang3
 *
 */
public class InvoiceFlowActivity extends BaseActivity {

	@ViewInject(R.id.invoice_flow_title)
	BackTitle title;
	@ViewInject(R.id.invoice_flow_listview)
	ListView listView;
	@ViewInject(R.id.invoice_flow_line)
	View line;
	
	private int mid;
	private int nid;
	private int wfid;
	private int lid;
	private String titleText;
	private String invoiceNumber;
	private String tmpCount1;
	private String tmpCount2;
	private String tmpCount3;
	private String tmpCount4;
	
	private LoadingDialog loadingDialog;
	private List<InvoiceFlowDetails> flowDetails;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invoice_flow);
		ViewUtils.inject(this);
		
		titleText = getIntent().getStringExtra("title");
		invoiceNumber = getIntent().getStringExtra("invoiceNumber");
		mid = getIntent().getIntExtra("mid", -1);
		nid = getIntent().getIntExtra("nid", -1);
		wfid = getIntent().getIntExtra("wfid", -1);
		lid = getIntent().getIntExtra("lid", -1);
		tmpCount1 = getIntent().getStringExtra("count1");
		tmpCount2 = getIntent().getStringExtra("count2");
		tmpCount3 = getIntent().getStringExtra("count3");
		tmpCount4 = getIntent().getStringExtra("count4");
		
		initTitle();
		initView();
	}

	private void initView() {
		loadingDialog = new LoadingDialog(InvoiceFlowActivity.this);
		getData();
		
		View footView = new View(InvoiceFlowActivity.this);
		AbsListView.LayoutParams footParams = new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.margin_20));
		footView.setLayoutParams(footParams);
		listView.addFooterView(footView);
	}
	
	private void getData(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(InvoiceFlowActivity.this));
		params.addBodyParameter("mid", mid+"");
		params.addBodyParameter("wfid", wfid+"");
		params.addBodyParameter("nid", nid+"");
		params.addBodyParameter("lid", lid+"");
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA01AB&pcmd=QueryHis",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String error) {
						loadingDialog.dismiss();
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadingDialog.dismiss();
						
						InvoiceFlowData data = SceoUtil.parseJson(responseInfo.result, InvoiceFlowData.class);
						if (data.getStatus() == 0) {
							flowDetails = data.getData().getList();
							
							line.setVisibility(View.VISIBLE);
							InvoiceFlowAdapter adapter = new InvoiceFlowAdapter(InvoiceFlowActivity.this, flowDetails,
									tmpCount1, tmpCount2, tmpCount3, tmpCount4);
							listView.setAdapter(adapter);
						}else {
							Toasty.error(InvoiceFlowActivity.this, data.getMessage(), Toast.LENGTH_SHORT, true).show();
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
		title.setBackTitle(titleText+"("+invoiceNumber+")");
		title.setBackTitleColor(getResources().getColor(R.color.white));
		title.setBackBtnClickLis(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
