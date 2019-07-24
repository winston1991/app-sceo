package com.huntkey.software.sceo.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.SceoApplication;
import com.huntkey.software.sceo.bean.FormSecData;
import com.huntkey.software.sceo.bean.FormSecDetails;
import com.huntkey.software.sceo.bean.FormSecInfo;
import com.huntkey.software.sceo.ui.activity.forms.ChartActivity;
import com.huntkey.software.sceo.ui.adapter.ChartCAdapter;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import es.dmoral.toasty.Toasty;

/**
 * 节点展开
 * @author chenliang3
 *
 */
public class ChartNodeFragment extends Fragment {

	@ViewInject(R.id.chartC_list_ptrListview)
	PullToRefreshListView listView;
	@ViewInject(R.id.chartC_list_load)
	RelativeLayout loadView;

	private FormSecDetails details;
	private String fYear;
	private String fMonth;
	private String code;
	private String length;

	private List<FormSecDetails> formSecList;
	private View view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (listView == null) {			
			view = inflater.inflate(R.layout.fragment_chart_c, null);
			ViewUtils.inject(this, view);
			
			details = (FormSecDetails) getArguments().getSerializable("cFormSecDetails");
			fYear = getArguments().getString("fyear");
			fMonth = getArguments().getString("fmonth");
			code = getArguments().getString("kmas_code");
			length = getArguments().getString("length");
			
			getData();
		}
		
		SceoUtil.removeViewFromParent(view);
		return view;
	}

	private void getData() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(getActivity()));
		params.addBodyParameter("fyear", fYear);
		params.addBodyParameter("fmonth", fMonth);
		params.addBodyParameter("kmas_code", code);
		params.addBodyParameter("st", details.getSt());
		params.addBodyParameter("cc_level", details.getCc_level());//第一次传空字符串
		params.addBodyParameter("ppif_id", details.getPpif_id());
		params.addBodyParameter("hasright", details.getHasright());
		params.addBodyParameter("lvl", details.getLvl());
		HttpUtils http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=QueryData",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadView.setVisibility(View.GONE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadView.setVisibility(View.GONE);
						
						FormSecData data = SceoUtil.parseJson(responseInfo.result, FormSecData.class);
						if (data.getStatus() == 0) {
							formSecList = data.getData().getList();
							FormSecInfo info = data.getData();
							ChartCAdapter adapter = new ChartCAdapter(getActivity(), formSecList);
							listView.setAdapter(adapter);
							listView.setMode(Mode.DISABLED);
							
							final ArrayList<String> xvalue = new ArrayList<>();
							xvalue.add(info.getM1());
							xvalue.add(info.getM2());
							xvalue.add(info.getM3());
							xvalue.add(info.getM4());
							xvalue.add(info.getM5());
							xvalue.add(info.getM6());
							xvalue.add(info.getM7());
							xvalue.add(info.getM8());
							xvalue.add(info.getM9());
							xvalue.add(info.getM10());
							xvalue.add(info.getM11());
							xvalue.add(info.getM12());
							
							setListOnItemClickListener(listView, xvalue);
							
						}else if (data.getStatus() == 88) {
							Toasty.error(getActivity(), data.getMessage(), Toast.LENGTH_SHORT, true).show();
							SceoUtil.gotoLogin(getActivity());
							SceoApplication.getInstance().exit();
						}else {
							Toasty.error(getActivity(), data.getMessage(), Toast.LENGTH_SHORT, true).show();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadView.setVisibility(View.VISIBLE);
					}
				});
	}
	
	private void setListOnItemClickListener(PullToRefreshListView listView, final ArrayList<String> xvalue){
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent = new Intent(getActivity(), ChartActivity.class);
				intent.putExtra("formSecList", (Serializable)formSecList.get(position-1));
				intent.putStringArrayListExtra("xvalue", xvalue);
				intent.putExtra("fyear", fYear);
				intent.putExtra("fmonth", fMonth);
				intent.putExtra("kmas_code", code);
				intent.putExtra("length", length);
				intent.putExtra("ccCode", formSecList.get(position - 1).getCc_code());
				startActivity(intent);
			}
		});
	}
	
}
