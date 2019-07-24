package com.huntkey.software.sceo.ui.activity.forms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.ChatSettingLinkman;
import com.huntkey.software.sceo.bean.FormAffairData;
import com.huntkey.software.sceo.bean.FormSecDetails;
import com.huntkey.software.sceo.base.BaseActivity;
import com.huntkey.software.sceo.ui.activity.chat.ChatActivity;
import com.huntkey.software.sceo.ui.fragment.ChartMonthFragment;
import com.huntkey.software.sceo.ui.fragment.ChartTotalFragment;
import com.huntkey.software.sceo.ui.fragment.ChartNodeFragment;
import com.huntkey.software.sceo.ui.fragment.ChartWeekFragment;
import com.huntkey.software.sceo.ui.fragment.ChartQuarterFragment;
import com.huntkey.software.sceo.ui.fragment.ChartYearFragment;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.widget.NoSlideViewPage;
import com.huntkey.software.sceo.widget.materialrohling.SlidingTabLayout;
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
 * 图表（即时快报第二级）
 * @author chenliang3
 *
 */
public class ChartActivity extends BaseActivity {

//	@ViewInject(R.id.chart_title)
//	BackTitle title;
	@ViewInject(R.id.back_title_back)
	RelativeLayout backTitleBtn;
	@ViewInject(R.id.back_title_tv)
	TextView backTitleTv;
	@ViewInject(R.id.back_title_affair)
	RelativeLayout backTitleAffair;
	@ViewInject(R.id.back_title_affair_config)
	ImageView backTitleAffairConfig;
	@ViewInject(R.id.chart_load)
	RelativeLayout loadView;
	@ViewInject(R.id.common_header_tv1)
	TextView header_tv1;//昨日
	@ViewInject(R.id.common_header_tv10)
	TextView header_tv10;//更新时间
	@ViewInject(R.id.common_header_tv11)
	TextView header_tv11;//当周统计
	@ViewInject(R.id.common_header_tv2)
	TextView header_tv2;//当月统计
	@ViewInject(R.id.common_header_tv3)
	TextView header_tv3;//当月同比率
	@ViewInject(R.id.common_header_tv4)
	TextView header_tv4;//当月完成率
	@ViewInject(R.id.common_header_tv5)
	TextView header_tv5;//财年统计
	@ViewInject(R.id.common_header_tv6)
	TextView header_tv6;//财年同比
	@ViewInject(R.id.common_header_tv7)
	TextView header_tv7;//财年完成率
	// @ViewInject(R.id.common_header_tv8)
	// TextView header_tv8;//规划统计
	// @ViewInject(R.id.common_header_tv9)
	// TextView header_tv9;//规划同比
	@ViewInject(R.id.chart_sliding_tabs)
	SlidingTabLayout slidingTabLayout;
	@ViewInject(R.id.chart_viewpager)
	NoSlideViewPage viewPager;
	
	private FormSecDetails details;
	private ArrayList<String> xvalue;
	private String fYear;
	private String fMonth;
	private String code;
	private String length;
	private String ccCode;
	private String chatFlag;//是否显示待办事务图标及红点
	private String ppifId;
	
	private int vpCount;
	private List<String> vpTitles = new ArrayList<>();
	private List<Fragment> vpFragments = new ArrayList<>();
	private ChartMonthFragment chartMonthFragment;
	private ChartTotalFragment chartTotalFragment;
	private ChartNodeFragment chartNodeFragment;
	private ChartWeekFragment chartWeekFragment;
	private ChartQuarterFragment chartQuarterFragment;
	private ChartYearFragment chartYearFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chart);
		ViewUtils.inject(this);
		
		details = (FormSecDetails) getIntent().getSerializableExtra("formSecList");
		xvalue = getIntent().getStringArrayListExtra("xvalue");
		fYear = getIntent().getStringExtra("fyear");
		fMonth = getIntent().getStringExtra("fmonth");
		code = getIntent().getStringExtra("kmas_code");
		length = getIntent().getStringExtra("length");
		ccCode = getIntent().getStringExtra("ccCode");
		chatFlag = details.getChat_flag();
		ppifId = details.getPpif_id();
		
		initTitle();
		initPager();
		initView();
		initHeader();
	}

	private void initPager() {
		chartMonthFragment = new ChartMonthFragment();
		Bundle aBundle = new Bundle();
		aBundle.putSerializable("aFormSecDetails", details);
		aBundle.putStringArrayList("aXvalue", xvalue);
		aBundle.putString("aLength", length);//用来计算统计值有几个月份有值
		chartMonthFragment.setArguments(aBundle);
		
		chartTotalFragment = new ChartTotalFragment();
		chartTotalFragment.setArguments(aBundle);
		
		chartWeekFragment = new ChartWeekFragment();
		Bundle dBundle = new Bundle();
		dBundle.putString("ppif_id", details.getPpif_id());//所选条目id
		dBundle.putString("upper_code", ccCode);
		dBundle.putString("rulerName", details.getRuler_name());//规划值名称(规划值名称从服务器获取)
		dBundle.putString("unitDigit", details.getUnit_digit());//y轴数据小数点位数
		dBundle.putString("fyear", details.getFyear());//财年
		chartWeekFragment.setArguments(dBundle);
		
		chartQuarterFragment = new ChartQuarterFragment();
		chartQuarterFragment.setArguments(dBundle);
		
		chartYearFragment = new ChartYearFragment();
		chartYearFragment.setArguments(dBundle);
		
		chartNodeFragment = new ChartNodeFragment();
		Bundle cBundle = new Bundle();
		cBundle.putSerializable("cFormSecDetails", details);
		cBundle.putString("fyear", fYear);
		cBundle.putString("fMonth", fMonth);
		cBundle.putString("kmas_code", code);
		cBundle.putString("length", length);
		chartNodeFragment.setArguments(cBundle);
		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int position) {
				
			}
		});
	}

	private void initView() {
		if (details.getSt().equals("0")) {//无下级节点
			vpCount = 5;
			vpTitles.add("月");
			vpTitles.add("累计");
			vpTitles.add("周");
			vpTitles.add("季");
			vpTitles.add("年");
			vpFragments.add(chartMonthFragment);
			vpFragments.add(chartTotalFragment);
			vpFragments.add(chartWeekFragment);
			vpFragments.add(chartQuarterFragment);
			vpFragments.add(chartYearFragment);
		}else {//有下级节点
			vpCount = 6;

			vpTitles.add("节点");
			vpTitles.add("月");
			vpTitles.add("累计");
			vpTitles.add("周");
			vpTitles.add("季");
			vpTitles.add("年");

			vpFragments.add(chartNodeFragment);
			vpFragments.add(chartMonthFragment);
			vpFragments.add(chartTotalFragment);
			vpFragments.add(chartWeekFragment);
			vpFragments.add(chartQuarterFragment);
			vpFragments.add(chartYearFragment);
		}
		
		slidingTabLayout.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
		slidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.title_color));
		slidingTabLayout.setDistributeEvenly(true);
		viewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager()));
		slidingTabLayout.setViewPager(viewPager);
		
		viewPager.setOffscreenPageLimit(2);
		viewPager.setCurrentItem(0);//1
	}
	
	private class MyFragmentAdapter extends FragmentPagerAdapter{

		public MyFragmentAdapter(FragmentManager fm) {
			super(fm);
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return vpTitles.get(position);
		}
	
		@Override
		public Fragment getItem(int position) {
			return vpFragments.get(position);
		}
	
		@Override
		public int getCount() {
			return vpCount;
		}
		
	}
	
	private void initHeader() {
		header_tv1.setText("日值：" + details.getPpid_yesterday());
		if (!TextUtils.isEmpty(details.getUpdatetime())) {			
			header_tv10.setText("更新时间：" + details.getUpdatetime().substring(5));
		}else {
			header_tv10.setText("更新时间：");
		}
		header_tv11.setText("周值：" + details.getPpid_week());
		header_tv2.setText("月值：" + details.getFfl_cur());
		header_tv3.setText("同比%：" + details.getTb_ffl_cur());
		header_tv4.setText("环比%：" +details.getHb_ffl_cur());
		header_tv5.setText("财年：" + details.getFfl_sum());
		header_tv6.setText("同比%：" + details.getTb_ffl_sum());
		header_tv7.setText("完成%：" + details.getPrecent());
		// header_tv8.setText("规划：" + details.getRuler_sum());
		// header_tv9.setText("同比%：" + details.getTb_ruler_sum());
	}
	
	private void initTitle() {
//		title.setBackBtnClickLis(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//		
//		title.setBackTitle(details.getCc_full()+"["+details.getPpif_name()+ "("+details.getUnit_name()+")" + "]");
//		title.setBackTitleColor(getResources().getColor(R.color.white));
		
		backTitleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		backTitleTv.setText(details.getCc_full()+"["+details.getPpif_name()+ "("+details.getUnit_name()+")" + "]");
		
		if (chatFlag == null || chatFlag.equals("0") || TextUtils.isEmpty(chatFlag)) {
			backTitleAffair.setVisibility(View.GONE);
		}else if (chatFlag.equals("4")) {
			backTitleAffair.setVisibility(View.VISIBLE);
			backTitleAffairConfig.setVisibility(View.VISIBLE);
		}else {
			backTitleAffair.setVisibility(View.VISIBLE);
			backTitleAffairConfig.setVisibility(View.GONE);
		}
		
		backTitleAffair.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getAffairMsg();
			}
		});
	}
	
	//如果存在对应指标的待办事务，则获取待办事务信息
	List<ChatSettingLinkman> addLinkmans = new ArrayList<ChatSettingLinkman>();
	private void getAffairMsg(){
		loadView.setVisibility(View.VISIBLE);
		if (chatFlag.equals("1") || chatFlag.equals("2") || chatFlag.equals("3") || chatFlag.equals("4")) {
			RequestParams params = new RequestParams();
//			params.addBodyParameter("sessionkey", SceoUtil.getSessionKey(ChartActivity.this));
//			params.addBodyParameter("ppif_id", ppifId);
			HttpUtils http = new HttpUtils();
			http.configResponseTextCharset("GB2312");
			http.send(HttpRequest.HttpMethod.GET, 
					Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=AddMember&sessionkey=" +
							SceoUtil.getSessionKey(ChartActivity.this) + "&ppif_id=" + ppifId, 
					new RequestCallBack<String>() {

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							loadView.setVisibility(View.GONE);
							Toasty.error(ChartActivity.this, "请求失败", Toast.LENGTH_SHORT, true).show();
						}

						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							loadView.setVisibility(View.GONE);
							
							FormAffairData data = SceoUtil.parseJson(responseInfo.result, FormAffairData.class);
							if (data.getStatus() == 0) {
								Intent intent = new Intent(ChartActivity.this, ChatActivity.class);
								intent.putExtra("taskId", Integer.parseInt(data.getData().getTmrm_id()));
								intent.putExtra("taskName", data.getData().getTmrm_name());
								intent.putExtra("peopleNum", Integer.parseInt(data.getData().getMember_count()));
								intent.putExtra("AddLinkmans", (Serializable)addLinkmans);
								startActivity(intent);
								
								if (backTitleAffairConfig.getVisibility() == View.VISIBLE) {
									backTitleAffairConfig.setVisibility(View.GONE);
								}
							}else {
								Toasty.error(ChartActivity.this, "请求失败", Toast.LENGTH_SHORT, true).show();
							}
						}
					});
		}
	}
	
}
