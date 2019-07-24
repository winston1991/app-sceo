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
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.base.BaseFragment;
import com.huntkey.software.sceo.bean.FormYearData;
import com.huntkey.software.sceo.bean.FormYearDetails;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.StringUtil;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class ChartYearFragment extends BaseFragment implements OnChartValueSelectedListener{

	@ViewInject(R.id.chartW_lineChart)
	LineChart lineChart;
	@ViewInject(R.id.chartW_load)
	RelativeLayout loadView;
	
	private View view;
	private String ppifId;
	private String upperCode;
	private String rulerName;//规划值名称
	private String unitDigit;//y轴小数位数
	private String fyear;//当前年份
	
	private HttpUtils http;
	private List<String> yxAxis;
	private List<Entry> yEntry_tj;//统计值
	private List<Entry> yEntry_gh;//规划值
	private List<Entry> yEntry_tb;//同比值
	private List<Entry> yEntry_tj_v1;//年化虚线
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
			fyear = getArguments().getString("fyear");
			
			init();
		}
		
		SceoUtil.removeViewFromParent(view);
		return view;
	}

	private void init() {
		http = new HttpUtils();
		http.configResponseTextCharset("GB2312");
		
		yEntry_gh = new ArrayList<>();
		yEntry_tb = new ArrayList<>();
		yEntry_tj = new ArrayList<>();
		yEntry_tj_v1 = new ArrayList<>();
		
		getXAxis();
	}

	private void getXAxis() {
		yxAxis = new ArrayList<>();
		int tmpYear = 0;
		try {
			tmpYear = Integer.parseInt(fyear);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 12; i++) {
			yxAxis.add(String.valueOf(tmpYear - 11 + i));
		}
		
		getYAxis();
	}

	private void getYAxis() {
		RequestParams params = new RequestParams();
		params.addBodyParameter("ppif_id", ppifId);
		params.addBodyParameter("upper_code", upperCode);
		params.addBodyParameter("ratio", "1");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=QueryYear",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadView.setVisibility(View.GONE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadView.setVisibility(View.GONE);
						
						FormYearData yData = SceoUtil.parseJson(responseInfo.result, FormYearData.class);
						if (yData.getTotal() > 0) {
							List<FormYearDetails> yDetails = yData.getRows();
							if (yDetails != null && yDetails.size() > 0) {
								initMpControl(yDetails);
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
	
	private void initMpControl(List<FormYearDetails> yDetails) {
		FormYearDetails detail = yDetails.get(0);
		
		String virtual = detail.getFfl_virtual();//当前财年的年化值，用以显示年化虚线
		
		List<String> fflValues = new ArrayList<>();//统计值
		List<String> pfflValues = new ArrayList<>();//同比值
		List<String> rulerValues = new ArrayList<>();//规划值
		List<Float> values = new ArrayList<>();//将所有的值添加进一个list中以获取最大最小值
		
		fflValues.add(detail.getFfl11());
		fflValues.add(detail.getFfl10());
		fflValues.add(detail.getFfl9());
		fflValues.add(detail.getFfl8());
		fflValues.add(detail.getFfl7());
		fflValues.add(detail.getFfl6());
		fflValues.add(detail.getFfl5());
		fflValues.add(detail.getFfl4());
		fflValues.add(detail.getFfl3());
		fflValues.add(detail.getFfl2());
		fflValues.add(detail.getFfl1());
		fflValues.add(detail.getFfl0());
		
		pfflValues.add(detail.getFfl12());
		pfflValues.add(detail.getFfl11());
		pfflValues.add(detail.getFfl10());
		pfflValues.add(detail.getFfl9());
		pfflValues.add(detail.getFfl8());
		pfflValues.add(detail.getFfl7());
		pfflValues.add(detail.getFfl6());
		pfflValues.add(detail.getFfl5());
		pfflValues.add(detail.getFfl4());
		pfflValues.add(detail.getFfl3());
		pfflValues.add(detail.getFfl2());
		pfflValues.add(detail.getFfl1());
		
		rulerValues.add(detail.getRuler11());
		rulerValues.add(detail.getRuler10());
		rulerValues.add(detail.getRuler9());
		rulerValues.add(detail.getRuler8());
		rulerValues.add(detail.getRuler7());
		rulerValues.add(detail.getRuler6());
		rulerValues.add(detail.getRuler5());
		rulerValues.add(detail.getRuler4());
		rulerValues.add(detail.getRuler3());
		rulerValues.add(detail.getRuler2());
		rulerValues.add(detail.getRuler1());
		rulerValues.add(detail.getRuler0());
		
		for (int i = 0; i < 12; i++) {
			if (pfflValues.get(i) != null && !"".equals(pfflValues.get(i))) {
				Entry entry = new Entry(StringUtil.string2float(pfflValues.get(i)), i);
				yEntry_tb.add(entry);
				values.add(StringUtil.string2float(pfflValues.get(i)));
			}
			if (rulerValues.get(i) != null && !"".equals(rulerValues.get(i))) {
				Entry entry = new Entry(StringUtil.string2float(rulerValues.get(i)), i);
				yEntry_gh.add(entry);
				values.add(StringUtil.string2float(rulerValues.get(i)));
			}
		}
		
		if (!TextUtils.isEmpty(virtual)) {
			for (int i = 0; i < 11; i++) {
				if (fflValues.get(i) != null && !"".equals(fflValues.get(i))) {
					Entry entry = new Entry(StringUtil.string2float(fflValues.get(i)), i);
					yEntry_tj.add(entry);
					values.add(StringUtil.string2float(fflValues.get(i)));
				}
			}
			
			if (!TextUtils.isEmpty(fflValues.get(10))) {
				Entry entry1 = new Entry(StringUtil.string2float(fflValues.get(10)), 10);
				yEntry_tj_v1.add(entry1);
				values.add(StringUtil.string2float(fflValues.get(10)));
			}
			Entry entry2 = new Entry(StringUtil.string2float(virtual), 11);
			yEntry_tj_v1.add(entry2);
			values.add(StringUtil.string2float(virtual));
		}else {
			for (int i = 0; i < 12; i++) {
				if (fflValues.get(i) != null && !"".equals(fflValues.get(i))) {
					Entry entry = new Entry(StringUtil.string2float(fflValues.get(i)), i);
					yEntry_tj.add(entry);
					values.add(StringUtil.string2float(fflValues.get(i)));
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
			
			LineDataSet set_tj_v1 = new LineDataSet(yEntry_tj_v1, "年化虚线");
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
			LineData data = new LineData(yxAxis, dataSets);
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
