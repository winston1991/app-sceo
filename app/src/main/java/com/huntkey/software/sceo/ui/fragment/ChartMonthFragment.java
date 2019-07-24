package com.huntkey.software.sceo.ui.fragment;

import java.math.BigDecimal;
import java.util.ArrayList;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
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
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormSecDetails;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.StringUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
/**
 * 月K线
 * @author chenliang3
 *
 */
public class ChartMonthFragment extends Fragment implements OnChartValueSelectedListener{

	@ViewInject(R.id.chartA_lineChart)
	LineChart lineChart;
	
	private FormSecDetails details;
	private ArrayList<String> xvalue;
	private String length;
	private View view;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (lineChart == null) {			
			view = inflater.inflate(R.layout.fragment_chart_a, null);
			ViewUtils.inject(this, view);
			
			details = (FormSecDetails) getArguments().getSerializable("aFormSecDetails");
			xvalue = getArguments().getStringArrayList("aXvalue");
			length = getArguments().getString("aLength");//用来计算统计值有几个月份有值
			
			initMpControl();
		}
		
		SceoUtil.removeViewFromParent(view);
		return view;
	}

	private void initMpControl() {
		lineChart.setOnChartValueSelectedListener(this);
		
		float minTmp = string2float(details.getMin());
		float maxTmp = string2float(details.getMax());
		
		if(!"".equals(details.getFfl_virtual())){
			float virtualTmp = string2float(details.getFfl_virtual());
			if(virtualTmp<minTmp){
				minTmp=virtualTmp;
			}
			if(virtualTmp>maxTmp){
				maxTmp=virtualTmp;
			}
		}
				
		float minYValue = minTmp;// < virtualTmp ? minTmp : virtualTmp;//获取最小值
		float maxYValue = maxTmp;// > virtualTmp ? maxTmp : virtualTmp;
		
		int len = Integer.parseInt(length.substring(length.length()-2));
		
		//统计值
		ArrayList<Entry> yvalue1 = new ArrayList<>();
		//月化虚线
		ArrayList<Entry> yvalue1_v1 = new ArrayList<>();
		if (!TextUtils.isEmpty(details.getFfl_virtual())) {
			if (len > 1) {				
				for (int i = 0; i < len-1; i++) {
					if (!"".equals(details.getDetail_c().get(i).getFfl())) {				
						Entry entry = new Entry(string2float(details.getDetail_c().get(i).getFfl()), i);
						yvalue1.add(entry);
					}
				}
				
				if (!TextUtils.isEmpty(details.getDetail_c().get(len-2).getFfl())) {
					Entry entry1 = new Entry(string2float(details.getDetail_c().get(len-2).getFfl()), len-2);
					yvalue1_v1.add(entry1);
				}
				Entry entry2 = new Entry(string2float(details.getFfl_virtual()), len-1);
				yvalue1_v1.add(entry2);
			}else if (len == 1) {
				Entry entry2 = new Entry(string2float(details.getFfl_virtual()), len-1);
				yvalue1_v1.add(entry2);
			}
		}else {
			for (int i = 0; i < len; i++) {
				if (!"".equals(details.getDetail_c().get(i).getFfl())) {				
					Entry entry = new Entry(string2float(details.getDetail_c().get(i).getFfl()), i);
					yvalue1.add(entry);
				}
			}
		}
		
		//规划值
		ArrayList<Entry> yvalue2 = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			if (!"".equals(details.getDetail_c().get(i).getRuler())) {
				Entry entry = new Entry(string2float(details.getDetail_c().get(i).getRuler()), i);
				yvalue2.add(entry);
			}
		}
		
		//同比值
		ArrayList<Entry> yvalue3 = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			if (!"".equals(details.getDetail_c().get(i).getP_ffl())) {
				Entry entry = new Entry(string2float(details.getDetail_c().get(i).getP_ffl()), i);
				yvalue3.add(entry);
			}
		}
		
		if (yvalue1.size() > 0 || yvalue2.size() > 0 || yvalue3.size() > 0 || yvalue1_v1.size() > 0) {			
			chartBuild(getActivity(), details, xvalue, lineChart, 
					minYValue, maxYValue, yvalue1, yvalue2, yvalue3, yvalue1_v1);
		}
		
	}
	
	private void chartBuild(Context context, FormSecDetails details, ArrayList<String> xvalue, LineChart lineChart, float minYValue, float maxYValue,
			ArrayList<Entry> yvalue1, ArrayList<Entry> yvalue2, ArrayList<Entry> yvalue3, ArrayList<Entry> yvalue1_v1){
		lineChart.setDrawGridBackground(true);//设置是否显示表格
		lineChart.setDescription("");//设置描述信息(设置为空)
		lineChart.setNoDataTextDescription("无数据");
		lineChart.setTouchEnabled(true);
		lineChart.setDragEnabled(true);//设置是否可拖动
		lineChart.setScaleEnabled(true);//设置是否可缩放
		lineChart.setPinchZoom(true);
		lineChart.setBackgroundColor(Color.WHITE);
		lineChart.setGridBackgroundColor(Color.WHITE);
		lineChart.setDoubleTapToZoomEnabled(true);//双击放大缩小
		lineChart.setExtraOffsets(0f, 15f, 10f, 0f);
		lineChart.setAutoScaleMinMaxEnabled(true);
		
		XAxis xAxis = lineChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setLabelsToSkip(0);//设置x轴坐标点之间是否隐藏--设为0不隐藏
		
		LimitLine limitLine = new LimitLine(StringUtil.string2float(details.getP_ffl_avg()));//设置分割线
		limitLine.setLineWidth(1f);
		limitLine.setLineColor(Color.BLUE);
		limitLine.enableDashedLine(10f, 10f, 0f);//设置limitLine的style
		limitLine.setLabelPosition(LimitLabelPosition.RIGHT_TOP);
		
		YAxis leftAxis = lineChart.getAxisLeft();
		leftAxis.removeAllLimitLines();//移除分界线以避免重叠
//		leftAxis.addLimitLine(limitLine);
		leftAxis.setDrawLimitLinesBehindData(true);
		if (minYValue == maxYValue&&minYValue!=0) {
			minYValue = minYValue/2;
			maxYValue=maxYValue+maxYValue/2;
		}else if(minYValue == 0 && maxYValue == 0) {
			maxYValue = 1;
		}
		leftAxis.setAxisMinValue(minYValue);//设置y轴最小值
		leftAxis.setAxisMaxValue(maxYValue);//设置y轴最大值
		leftAxis.setStartAtZero(false);
		leftAxis.enableGridDashedLine(10f, 10f, 0f);
		leftAxis.setLabelCount(10, false);
		
		lineChart.getAxisRight().setEnabled(true);
		lineChart.getAxisRight().setAxisMaxValue(0f);
		lineChart.getAxisRight().setAxisMinValue(0f);
		lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);//设置动画
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
		
		Legend l = lineChart.getLegend();
		l.setForm(LegendForm.LINE);//设置下标显示为线
		l.setFormSize(15f);
		l.setTypeface(tf);
		l.setFormToTextSpace(1f);
		l.setXEntrySpace(15f);
		l.setCustom(new int[]{Color.BLACK, Color.RED, Color.BLUE}, 
				new String[]{details.getRuler_name(), "统计值", "同比值"});//设置下标
		
		LineDataSet set1 = new LineDataSet(yvalue1, "统计值");
		set1.enableDashedHighlightLine(10f, 5f, 0f);
		set1.setColor(Color.RED);
		set1.setCircleColor(Color.RED);
		set1.setValueTextColor(Color.RED);
		set1.setDrawCubic(true);//设置为平滑曲线
		set1.setCubicIntensity(0.1f);//设置平滑度，最大1.0f，最小0.05f，默认0.2f
		set1.setHighLightColor(Color.RED);//设置标识线的颜色
		set1.setHighlightLineWidth(1f);//设置标识线宽度
		set1.setHighlightEnabled(false);
		set1.setLineWidth(1f);
		set1.setCircleSize(3f);
		set1.setDrawCircleHole(false);
		set1.setValueTextSize(9f);
		set1.setFillAlpha(65);
		set1.setFillColor(Color.BLACK);
		
		LineDataSet set1_v1 = new LineDataSet(yvalue1_v1, "月化虚线");
		set1_v1.enableDashedHighlightLine(10f, 5f, 0f);
		set1_v1.setColor(Color.RED);
		set1_v1.setCircleColor(Color.RED);
		set1_v1.setValueTextColor(Color.RED);
		set1_v1.setDrawCubic(false);//设置为平滑曲线
		set1_v1.setHighLightColor(Color.RED);//设置标识线的颜色
		set1_v1.setHighlightLineWidth(1f);//设置标识线宽度
		set1_v1.setHighlightEnabled(false);
		set1_v1.setLineWidth(1f);
		set1_v1.setCircleSize(3f);
		set1_v1.setDrawCircleHole(false);
		set1_v1.setValueTextSize(9f);
		set1_v1.setFillAlpha(65);
		set1_v1.setFillColor(Color.BLACK);
		set1_v1.enableDashedLine(15, 15, 0);//设置是否为虚线
		
		LineDataSet set2 = new LineDataSet(yvalue2, "规划值");
		set2.enableDashedHighlightLine(10f, 5f, 0f);
		set2.setColor(Color.BLACK);
		set2.setCircleColor(Color.BLACK);
		set2.setValueTextColor(Color.BLACK);
		set2.setDrawCubic(true);//设置为平滑曲线
		set2.setCubicIntensity(0.1f);//设置平滑度，最大1.0f，最小0.05f，默认0.2f
		set2.setHighLightColor(Color.BLACK);//设置标识线的颜色
		set2.setHighlightLineWidth(1f);//设置标识线宽度
		set2.setHighlightEnabled(false);
		set2.setLineWidth(1f);
		set2.setCircleSize(3f);
		set2.setDrawCircleHole(false);
		set2.setValueTextSize(9f);
		set2.setFillAlpha(65);
		set2.setFillColor(Color.BLACK);
		
		LineDataSet set3 = new LineDataSet(yvalue3, "同比值");
		set3.enableDashedHighlightLine(10f, 5f, 0f);
		set3.setColor(Color.BLUE);
		set3.setCircleColor(Color.BLUE);
		set3.setValueTextColor(Color.BLUE);
		set3.setDrawCubic(true);
		set3.setCubicIntensity(0.1f);//设置平滑度，最大1.0f，最小0.05f，默认0.2f
		set3.setHighLightColor(Color.BLUE);//设置标识线的颜色
		set3.setHighlightLineWidth(1f);//设置标识线宽度
		set3.setHighlightEnabled(false);
		set3.setLineWidth(1f);
		set3.setCircleSize(3f);
		set3.setDrawCircleHole(false);
		set3.setValueTextSize(9f);
		set3.setFillAlpha(65);
		set3.setFillColor(Color.BLACK);
		
		ArrayList<LineDataSet> dataSets = new ArrayList<>();
		dataSets.add(set2);
		dataSets.add(set1);
		dataSets.add(set3);
		dataSets.add(set1_v1);
		LineData data = new LineData(xvalue, dataSets);
		
		lineChart.setData(data);
		
		setYMaxMin(leftAxis, data, details);
	}
	
	/**
	 * 重置最大值
	 * @param leftAxis
	 * @param data
	 * @param details
	 */
	private void setYMaxMin(YAxis leftAxis, LineData data, FormSecDetails details){		
		float maxValue=0f;
		float minValue=0f;
		float newMax0=0f;
		float newMax1=0f;
		
		for(int i=0;i<3;i++){						
			if (leftAxis.getFormattedLabel(2) != null && !leftAxis.getFormattedLabel(2).equals("") 
					&& leftAxis.getFormattedLabel(1) != null && !"".equals(leftAxis.getFormattedLabel(1))) {
				newMax1=newMax0;
				
				if(i==0){
					float f1=Float.parseFloat(leftAxis.getFormattedLabel(2).replace(",", ""));
					float f2=Float.parseFloat(leftAxis.getFormattedLabel(1).replace(",", ""));
					
					BigDecimal b1 = new BigDecimal(Float.toString(f1));
					BigDecimal b2 = new BigDecimal(Float.toString(f2));
					
					try {
						String tmp = leftAxis.getFormattedLabel(2);
						int digit = Integer.parseInt(details.getUnit_digit());
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
				} else{
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
										
					/*if(labelCount<10){						
						newMax=newMax+(int)((10-labelCount)/2)*newMax0;						
						newMin=newMin-(10-labelCount-(int)((10-labelCount)/2))*newMax0;
						labelCount=10;
					}*/
					
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
		
	}

	@Override
	public void onNothingSelected() {
		
	}
	
	private float string2float(String str){
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			return 0f;
		}
	}
	
}
