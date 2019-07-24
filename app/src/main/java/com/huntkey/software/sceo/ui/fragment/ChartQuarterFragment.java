package com.huntkey.software.sceo.ui.fragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.DefaultYAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormQuarterData;
import com.huntkey.software.sceo.bean.FormQuarterDetails;
import com.huntkey.software.sceo.bean.FormWeekAndQuarterXData;
import com.huntkey.software.sceo.bean.FormWeekAndQuarterXDetails;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.StringUtil;
import com.huntkey.software.sceo.widget.ChartWeekMarkerView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
/**
 * 当季图表
 * @author chenliang3
 *
 */
public class ChartQuarterFragment extends Fragment implements OnChartValueSelectedListener{
	
	@ViewInject(R.id.chartW_lineChart)
	LineChart lineChart;
	@ViewInject(R.id.chartW_load)
	RelativeLayout loadView;
	
	private View view;
	private String ppifId;
	private String upperCode;
	private String rulerName;//规划值名称
	private String unitDigit;//y轴小数点位数
	private HttpUtils http;
	private List<String> qxAxis;
	private List<Entry> yEntry_tj;//统计值
	private List<Entry> yEntry_gh;//规划值
	private List<Entry> yEntry_tb;//同比值
	private List<Entry> yEntry_tj_v1;//季化虚线
	private List<Float> values = new ArrayList<>();//将所有数据放到list中以获取最大最小值
	private float minYValue = 0f;
	private float maxYValue = 0f;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (lineChart == null) {
			view = inflater.inflate(R.layout.fragment_chart_week, null);
			ViewUtils.inject(this, view);
			
			ppifId = getArguments().getString("ppif_id");
			upperCode = getArguments().getString("upper_code");
			rulerName = getArguments().getString("rulerName");
			unitDigit = getArguments().getString("unitDigit");
			
			init();
		}
		
		SceoUtil.removeViewFromParent(view);
		return view;
	}
	
	private void init() {
		http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		qxAxis = new ArrayList<>();
		yEntry_tj = new ArrayList<>();
		yEntry_gh = new ArrayList<>();
		yEntry_tb = new ArrayList<>();
		yEntry_tj_v1 = new ArrayList<>();
		
		getXAxis();
	}
	
	/**
	 * 获取x轴数据
	 */
	private void getXAxis() {
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=QueryXAxis",
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadView.setVisibility(View.GONE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadView.setVisibility(View.GONE);
						
						FormWeekAndQuarterXData wqxData = SceoUtil.parseJson(responseInfo.result, FormWeekAndQuarterXData.class);
						if (wqxData.getTotal() > 0) {
							List<FormWeekAndQuarterXDetails> wqxDetails = wqxData.getRows();
							for (FormWeekAndQuarterXDetails detail : wqxDetails) {
								String name = detail.getName();
								if (detail.getTp() == 3) {//tp为3是季度的x轴数据
									try {//更改x轴数据的显示样式
										if (!"1".equals(name.substring(name.length()-1, name.length()))) {
											name = name.substring(name.length()-3, name.length());
										}
										qxAxis.add(name);
									} catch (Exception e) {
										qxAxis.add(name);
									}
								}
							}
							getYAxis();
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadView.setVisibility(View.VISIBLE);
					}
				});
	}

	/**
	 * 获取y轴数据
	 */
	private void getYAxis() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("ppif_id", ppifId);
		params.addBodyParameter("upper_code", upperCode);
		params.addBodyParameter("ratio", "1");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=QueryQuarter",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadView.setVisibility(View.GONE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadView.setVisibility(View.GONE);
						
						FormQuarterData qData = SceoUtil.parseJson(responseInfo.result, FormQuarterData.class);
						if (qData.getTotal() > 0) {
							List<FormQuarterDetails> qDetails = qData.getRows();
							if (qDetails != null && qDetails.size() > 0) {								
								initMpControl(qDetails);
							}
						}
					}
					
					@Override
					public void onStart() {
						super.onStart();
						loadView.setVisibility(View.VISIBLE);
					}
				});
	}
	
	String virtual;//当前财月的季化值，用以显示季化虚线
	private void initMpControl(List<FormQuarterDetails> qDetails) {
		List<String> tjValues = new ArrayList<>();
		List<String> ghValues = new ArrayList<>();
		List<String> tbValues = new ArrayList<>();
		
		for (FormQuarterDetails detail : qDetails) {
			try {
				if (detail.getFfl() != null && !"".equals(detail.getFfl())) {					
					values.add(Float.parseFloat(detail.getFfl()));
				}
				if (detail.getP_ffl() != null && !"".equals(detail.getP_ffl())) {					
					values.add(Float.parseFloat(detail.getP_ffl()));
				}
				if (detail.getPp_ffl() != null && !"".equals(detail.getPp_ffl())) {					
					values.add(Float.parseFloat(detail.getPp_ffl()));
				}
				if (detail.getPpp_ffl() != null && !"".equals(detail.getPpp_ffl())) {					
					values.add(Float.parseFloat(detail.getPpp_ffl()));
				}
				if (detail.getRuler() != null && !"".equals(detail.getRuler())) {					
					values.add(Float.parseFloat(detail.getRuler()));
				}
				if (detail.getP_ruler() != null && !"".equals(detail.getP_ruler())) {					
					values.add(Float.parseFloat(detail.getP_ruler()));
				}
				if (detail.getPp_ruler() != null && !"".equals(detail.getPp_ruler())) {					
					values.add(Float.parseFloat(detail.getPp_ruler()));
				}
				if (detail.getFfl_virtual() != null && !"".equals(detail.getFfl_virtual())) {
					values.add(Float.parseFloat(detail.getFfl_virtual()));
				}
			} catch (Exception e) {
				
			}
		}
		int len = qDetails.size();
		for (int i = 0; i < len; i++) {			
			tjValues.add(qDetails.get(i).getPp_ffl());
			ghValues.add(qDetails.get(i).getPp_ruler());
			tbValues.add(qDetails.get(i).getPpp_ffl());
		}
		for (int i = 0; i < len; i++) {
			tjValues.add(qDetails.get(i).getP_ffl());
			ghValues.add(qDetails.get(i).getP_ruler());
			tbValues.add(qDetails.get(i).getPp_ffl());
		}
		for (int i = 0; i < len; i++) {
			String fq = qDetails.get(i).getFq();
			String ppidTime = qDetails.get(i).getPpid_time();
			int mFq = Integer.parseInt(fq.substring(fq.length()-1, fq.length()));
			int mPPidTime = Integer.parseInt(ppidTime.substring(ppidTime.length()-1, ppidTime.length()));
			if (mPPidTime <= mFq) {
				tjValues.add(qDetails.get(i).getFfl());
			}
			ghValues.add(qDetails.get(i).getRuler());
			tbValues.add(qDetails.get(i).getP_ffl());
			
			//季化虚线
			if (ppidTime.equals(fq) && !TextUtils.isEmpty(qDetails.get(i).getFfl_virtual())) {
				virtual = qDetails.get(i).getFfl_virtual();
			}
		}
		
		for (int i = 0; i < ghValues.size(); i++) {
			if (!"".equals(ghValues.get(i))) {
				Entry entry = new Entry(StringUtil.string2float(ghValues.get(i)), i);
				yEntry_gh.add(entry);
			}
			if (!"".equals(tbValues.get(i))) {
				Entry entry = new Entry(StringUtil.string2float(tbValues.get(i)), i);
				yEntry_tb.add(entry);
			}
		}
		
		int tmpLen = tjValues.size();
		//处理统计值及季化虚线
		if (!TextUtils.isEmpty(virtual)) {
			if (tmpLen > 1) {				
				for (int i = 0; i < tmpLen-1; i++) {
					if (!"".equals(tjValues.get(i))) {
						Entry entry = new Entry(StringUtil.string2float(tjValues.get(i)), i);
						yEntry_tj.add(entry);
					}
				}
				
				if (!TextUtils.isEmpty(tjValues.get(tmpLen-2))) {
					Entry entry1 = new Entry(StringUtil.string2float(tjValues.get(tmpLen-2)), tmpLen-2);
					yEntry_tj_v1.add(entry1);
				}
				Entry entry2 = new Entry(StringUtil.string2float(virtual), tmpLen-1);
				yEntry_tj_v1.add(entry2);
			}else if (tmpLen == 1) {
				Entry entry2 = new Entry(StringUtil.string2float(virtual), tmpLen-1);
				yEntry_tj_v1.add(entry2);
			}
		}else {
			for (int i = 0; i < tmpLen; i++) {
				if (!TextUtils.isEmpty(tjValues.get(i))) {					
					Entry entry = new Entry(StringUtil.string2float(tjValues.get(i)), i);
					yEntry_tj.add(entry);
				}
			}
		}
		
		if (yEntry_gh.size() > 0 || yEntry_tb.size() > 0 || yEntry_tj.size() > 0 || yEntry_tj_v1.size() > 0) {
			lineChart.setOnChartValueSelectedListener(this);
			
			try {
				minYValue = Collections.min(values);
				maxYValue = Collections.max(values);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			lineChart.setDrawGridBackground(true);//设置显示表格
			lineChart.setDescription("");//设置描述信息
			lineChart.setNoDataTextDescription("无数据");
			lineChart.setTouchEnabled(true);
			lineChart.setDragEnabled(true);//设置可拖动
			lineChart.setScaleEnabled(true);//设置可缩放
			lineChart.setPinchZoom(true);
			lineChart.setBackgroundColor(Color.WHITE);
			lineChart.setGridBackgroundColor(Color.WHITE);
			lineChart.setDoubleTapToZoomEnabled(true);//双击放大
			lineChart.setExtraOffsets(0f, 15f, 10f, 0f);
			lineChart.setAutoScaleMinMaxEnabled(true);
			
			XAxis xAxis = lineChart.getXAxis();
			xAxis.setPosition(XAxisPosition.BOTTOM);
			xAxis.setLabelsToSkip(0);//设置x轴坐标点之间是否隐藏--设为0不隐藏
			
			YAxis leftAxis = lineChart.getAxisLeft();
			if (minYValue == maxYValue && minYValue != 0) {
				minYValue = minYValue/2;
				maxYValue = maxYValue + maxYValue/2;
			}else if (minYValue == 0 && maxYValue == 0) {
				maxYValue = 1;
			}
			leftAxis.setAxisMinValue(minYValue);
			leftAxis.setAxisMaxValue(maxYValue);
			leftAxis.setStartAtZero(false);
			leftAxis.enableGridDashedLine(10f, 10f, 0f);
			leftAxis.setLabelCount(10, false);
			
			lineChart.getAxisRight().setEnabled(true);
			lineChart.getAxisRight().setAxisMaxValue(0f);
			lineChart.getAxisRight().setAxisMinValue(0f);
			
			lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
			
			Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
			Legend l = lineChart.getLegend();
			l.setForm(LegendForm.LINE);//设置下标显示为线
			l.setFormSize(15f);
			l.setTypeface(tf);
			l.setFormToTextSpace(1f);
			l.setXEntrySpace(15f);
			l.setCustom(new int[]{Color.BLACK, Color.RED, Color.BLUE}, 
					new String[]{rulerName,"统计值","同比值"});//设置下标
			
			LineDataSet set_tj = new LineDataSet(yEntry_tj, "统计值");
			set_tj.enableDashedHighlightLine(10f, 5f, 0f);
			set_tj.setColor(Color.RED);
			set_tj.setCircleColor(Color.RED);
			set_tj.setValueTextColor(Color.RED);
			set_tj.setHighlightEnabled(false);
			set_tj.setDrawCubic(true);//设置为平滑曲线
			set_tj.setCubicIntensity(0.1f);//设置平滑度
			set_tj.setLineWidth(1f);
			set_tj.setCircleSize(3f);
			set_tj.setDrawCircleHole(false);
			set_tj.setValueTextSize(9f);
			set_tj.setFillAlpha(65);
			set_tj.setFillColor(Color.BLACK);
			
			LineDataSet set_tj_v1 = new LineDataSet(yEntry_tj_v1, "虚线");
			set_tj_v1.enableDashedHighlightLine(10f, 5f, 0f);
			set_tj_v1.setColor(Color.RED);
			set_tj_v1.setCircleColor(Color.RED);
			set_tj_v1.setValueTextColor(Color.RED);
			set_tj_v1.setHighlightEnabled(false);
			set_tj_v1.setDrawCubic(false);//设置为平滑曲线
			set_tj_v1.setLineWidth(1f);
			set_tj_v1.setCircleSize(3f);
			set_tj_v1.setDrawCircleHole(false);
			set_tj_v1.setValueTextSize(9f);
			set_tj_v1.setFillAlpha(65);
			set_tj_v1.setFillColor(Color.BLACK);
			set_tj_v1.enableDashedLine(15, 15, 0);//设置是否为虚线
			
			LineDataSet set_gh = new LineDataSet(yEntry_gh, "规划值");
			set_gh.enableDashedHighlightLine(10f, 5f, 0f);
			set_gh.setColor(Color.BLACK);
			set_gh.setCircleColor(Color.BLACK);
			set_gh.setValueTextColor(Color.BLACK);
			set_gh.setHighlightEnabled(false);
			set_gh.setDrawCubic(true);//设置为平滑曲线
			set_gh.setCubicIntensity(0.1f);//设置平滑度
			set_gh.setLineWidth(1f);
			set_gh.setCircleSize(3f);
			set_gh.setDrawCircleHole(false);
			set_gh.setValueTextSize(9f);
			set_gh.setFillAlpha(65);
			set_gh.setFillColor(Color.BLACK);
			
			LineDataSet set_tb = new LineDataSet(yEntry_tb, "同比值");
			set_tb.enableDashedHighlightLine(10f, 5f, 0f);
			set_tb.setColor(Color.BLUE);
			set_tb.setCircleColor(Color.BLUE);
			set_tb.setValueTextColor(Color.BLUE);
			set_tb.setHighlightEnabled(false);
			set_tb.setDrawCubic(true);//设置为平滑曲线
			set_tb.setCubicIntensity(0.1f);//设置平滑度
			set_tb.setLineWidth(1f);
			set_tb.setCircleSize(3f);
			set_tb.setDrawCircleHole(false);
			set_tb.setValueTextSize(9f);
			set_tb.setFillAlpha(65);
			set_tb.setFillColor(Color.BLACK);
			
			ArrayList<LineDataSet> dataSets = new ArrayList<>();
			dataSets.add(set_gh);
			dataSets.add(set_tj);
			dataSets.add(set_tb);
			dataSets.add(set_tj_v1);
			LineData data = new LineData(qxAxis, dataSets);
			lineChart.setData(data);
			
			setYMaxMin(leftAxis, data, unitDigit);
		}
	}
	
	/**
	 * 重置最大最小值
	 * @param leftAxis
	 * @param data
	 * @param uDigit
	 */
	private void setYMaxMin(YAxis leftAxis, LineData data, String uDigit){
		float maxValue = 0f;
		float minValue = 0f;
		float newMax0 = 0f;
		float newMax1 = 0f;
		
		for (int i = 0; i < 3; i++) {
			if (leftAxis.getFormattedLabel(2) != null && !leftAxis.getFormattedLabel(2).equals("")
					&& leftAxis.getFormattedLabel(1) != null && !"".equals(leftAxis.getFormattedLabel(1))) {
				newMax1 = newMax0;
				
				if (i == 0) {
					float f1=Float.parseFloat(leftAxis.getFormattedLabel(2).replace(",", ""));
					float f2=Float.parseFloat(leftAxis.getFormattedLabel(1).replace(",", ""));
					
					BigDecimal b1 = new BigDecimal(Float.toString(f1));
					BigDecimal b2 = new BigDecimal(Float.toString(f2));

					try {
						String tmp = leftAxis.getFormattedLabel(2);
						int digit = Integer.parseInt(uDigit);
						if (tmp.contains(".")) { //如果y轴数据带小数点且后台返回的小数位数大于y轴数据小数位数，则使用后台返回的位数
							if (digit > (tmp.length() - tmp.indexOf(".") - 1)) {									
								YAxisValueFormatter f = new DefaultYAxisValueFormatter(digit);//设置y轴精度
								leftAxis.setValueFormatter(f);
							}
							ValueFormatter formatter = new DefaultValueFormatter(digit);
							data.setValueFormatter(formatter);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					newMax0 = Math.abs(b1.subtract(b2).floatValue());
				}else {
					float f0 = Float.parseFloat(leftAxis.getFormattedLabel(0).replace(",", ""));//3.2
					if(f0 != minValue){
						//leftAxis.setLabelCount(10, true);
					}
					newMax0 = newMax1;
				}
				if(newMax0 == newMax1){
					break;
				}else if (newMax0 != 0f) {
					maxValue = leftAxis.getAxisMaxValue();					
					minValue = leftAxis.getAxisMinValue();
					
					int maxCount = (int)(maxValue/newMax0);
					if(maxCount < maxValue/newMax0){
						maxCount++;
					}
					
					int minCount = (int)(minValue/newMax0);
					if(minCount > minValue/newMax0){
						minCount--;
					}
					
					BigDecimal b3 = new BigDecimal(Float.toString(newMax0));
					BigDecimal b4 = new BigDecimal(Float.toString(maxCount));
					BigDecimal b5 = new BigDecimal(Float.toString(minCount));
										
					float newMax = b3.multiply(b4).floatValue();
					float newMin = b3.multiply(b5).floatValue();
					
					BigDecimal m1 = new BigDecimal(Float.toString(newMax));
					BigDecimal m2 = new BigDecimal(Float.toString(newMin));
					float m3 = m1.subtract(m2).floatValue();//m1-m2
					
					int labelCount = (int)(m3/newMax0);
					if(labelCount < (m3)/newMax0){
						labelCount++;
					}
										
					leftAxis.setAxisMaxValue(newMax);					
					leftAxis.setAxisMinValue(newMin);
										
					leftAxis.setLabelCount(labelCount, false);
				}
			}else{
				break;
			}			
		}
	}

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub
	}
	
}
