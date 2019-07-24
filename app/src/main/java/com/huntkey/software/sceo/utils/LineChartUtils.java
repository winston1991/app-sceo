package com.huntkey.software.sceo.utils;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;

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
import com.huntkey.software.sceo.bean.FormSecDetails;

public class LineChartUtils {

	/**
	 * 创建折线图表
	 * @param context 上下文
	 * @param details y轴数据
	 * @param xvalue x轴数据
	 * @param lineChart LineChart
	 * @param minYValue 最小值
	 * @param maxYValue 最大值
	 * @param yvalue1  统计值
	 * @param yvalue2  规划值
	 * @param yvalue3  同比值
	 */
	public static void chartBuild(Context context, FormSecDetails details, ArrayList<String> xvalue, LineChart lineChart, float minYValue, float maxYValue,
			ArrayList<Entry> yvalue1, ArrayList<Entry> yvalue2, ArrayList<Entry> yvalue3){
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
		
		XAxis xAxis = lineChart.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setLabelsToSkip(0);//设置x轴坐标点之间是否隐藏--设为0不隐藏
		
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "OpenSans-Regular.ttf");
		
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
		
		Legend l = lineChart.getLegend();
		l.setForm(LegendForm.LINE);//设置下标显示为线
		l.setFormSize(15f);
		l.setTypeface(tf);
		l.setFormToTextSpace(1f);
		l.setXEntrySpace(15f);
		
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
		
		LineDataSet set2 = new LineDataSet(yvalue2, details.getRuler_name());
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
	private static void setYMaxMin(YAxis leftAxis, LineData data, FormSecDetails details){		
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
	
}
