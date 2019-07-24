package com.huntkey.software.sceo.ui.fragment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.DefaultYAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.huntkey.software.sceo.Conf;
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormWeekAndQuarterXData;
import com.huntkey.software.sceo.bean.FormWeekAndQuarterXDetails;
import com.huntkey.software.sceo.bean.FormWeekData;
import com.huntkey.software.sceo.bean.FormWeekDetails;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.huntkey.software.sceo.utils.StringUtil;
import com.huntkey.software.sceo.widget.ChartWeekMarkerView;
import com.huntkey.software.sceo.widget.ErrorView;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
/**
 * 当周图表
 * @author chenliang3
 *
 */
public class ChartWeekFragment extends Fragment implements OnChartValueSelectedListener, OnChartGestureListener{

	@ViewInject(R.id.chartW_lineChart)
	LineChart lineChart;
	@ViewInject(R.id.chartW_load)
	RelativeLayout loadView;
	
	private View view;
	private HttpUtils http;
	private String ppifId;
	private String upperCode;
	private String rulerName;
	private String unitDigit;
	private String fyear;
	private List<String> wxAxis;//周数据的x轴数据
	private List<FormWeekDetails> wyDetails;//周数据的y轴数据
	private List<String> dates;//周日期(如：FW40(11/27~12/03))
	private List<Float> values = new ArrayList<>();//将所有数据放到list中以获取最大最小值
	private float minYValue = 0f;//y轴最小值
	private float maxYValue = 0f;//y轴最大值
	private List<Entry> yEntry_tj;//统计值
	private List<Entry> yEntry_gh;//规划值
	private List<Entry> yEntry_tb;//同比值
	private ChartWeekMarkerView markerView;
	private int fweek = 0;//当前周的前一周
	
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
		wxAxis = new ArrayList<>();
		wyDetails = new ArrayList<>();
		dates = new ArrayList<>();
		yEntry_tj = new ArrayList<>();
		yEntry_gh = new ArrayList<>();
		yEntry_tb = new ArrayList<>();
		
		getXAxis();
	}
	
	/**
	 * 获取x轴数据
	 */
	private void getXAxis(){
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
								if (detail.getTp() == 2) {
									wxAxis.add(detail.getName().replace("FW", ""));
									dates.add(detail.getDates());
								}
							}
							
							FormWeekDetails d = new FormWeekDetails();
							for (int i = 0; i < wxAxis.size(); i++) {
								wyDetails.add(d);//向wyDetails中添加空数据以占位,为下一步list.add(index,Obj)做准备
							}
							getYAxis();//获取到x轴数据之后再获取y轴数据
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
	 * 获取周数据的y轴数据
	 */
	private void getYAxis(){
		RequestParams params = new RequestParams();
		params.addBodyParameter("ppif_id", ppifId);
		params.addBodyParameter("upper_code", upperCode);
		params.addBodyParameter("ratio", "1");
		http.send(HttpRequest.HttpMethod.POST, 
				Conf.SERVICE_URL + "sceosrv/csp/sceosrv.dll?page=EAA08AB&pcmd=QueryDetail",
				params, 
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						loadView.setVisibility(View.GONE);
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						loadView.setVisibility(View.GONE);
						
						FormWeekData wData = SceoUtil.parseJson(responseInfo.result, FormWeekData.class);
						if (wData.getTotal() > 0) {
							List<FormWeekDetails> wDetails = wData.getRows();
							if (wDetails != null && wDetails.size() > 0) {								
								initMpControl(wDetails);
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
	
	private void initMpControl(List<FormWeekDetails> wDetails){
		for (int i = 0; i < wDetails.size(); i++) {
			FormWeekDetails detail = wDetails.get(i);
			if (detail.getPpid_time().length() == 4 && detail.getPpid_time().contains("FW")) {
				if (detail.getPpid_time() != null && !"".equals(detail.getPpid_time())) {	
					fweek = detail.getFweek();
					String time = detail.getPpid_time();
					int index = Integer.parseInt(time.substring(time.length()-2, time.length())) - 1;
					wyDetails.add(index, detail);
				}
			}
		}
		
		for (int i = 0; i < wyDetails.size(); i++) {
			FormWeekDetails detail = wyDetails.get(i);
			
			try {
				if (i <= detail.getFweek() && detail.getFfl() != null && !"".equals(detail.getFfl())) {						
					values.add(Float.parseFloat(detail.getFfl()));
				}
				if (detail.getP_ffl() != null && !"".equals(detail.getP_ffl())) {						
					values.add(Float.parseFloat(detail.getP_ffl()));
				}
				if (detail.getRuler() != null && !"".equals(detail.getRuler())) {						
					values.add(Float.parseFloat(detail.getRuler()));
				}
			} catch (Exception e) {
				
			}
			
			//统计值
			if (i <= detail.getFweek() && detail.getFfl() != null && !"".equals(detail.getFfl())) {
				Entry entry = new Entry(StringUtil.string2float(detail.getFfl()), i);
				yEntry_tj.add(entry);
			}
			
			//规划值
			if (detail.getRuler() != null && !"".equals(detail.getRuler())) {
				Entry entry = new Entry(StringUtil.string2float(detail.getRuler()), i);
				yEntry_gh.add(entry);				
			}
			
			//同比
			if (detail.getP_ffl() != null && !"".equals(detail.getP_ffl())) {
				Entry entry = new Entry(StringUtil.string2float(detail.getP_ffl()), i);
				yEntry_tb.add(entry);
			}
		}
		
		if (yEntry_tj.size() > 0 || yEntry_gh.size() > 0 || yEntry_tb.size() > 0) {
			lineChart.setOnChartValueSelectedListener(this);
			lineChart.setOnChartGestureListener(this);
			
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
			
			lineChart.centerViewTo(fweek-12, 0f, AxisDependency.LEFT);
			lineChart.zoom(4.5f, 1f, 0, 0);
			lineChart.setScaleMaxma(4.5f, 1f);
			
			markerView = new ChartWeekMarkerView(getActivity(), R.layout.view_chartweek_marker, fyear, dates);
			lineChart.setMarkerView(markerView);
			
			XAxis xAxis = lineChart.getXAxis();
			xAxis.setPosition(XAxisPosition.BOTTOM);
			xAxis.setLabelsToSkip(0);
			
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
			
			lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);//设置动画效果
			
			Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
			Legend l = lineChart.getLegend();
			l.setForm(LegendForm.LINE);//设置下标显示为线
			l.setFormSize(15f);
			l.setTypeface(tf);
			l.setFormToTextSpace(1f);
			l.setXEntrySpace(15f);
			
			LineDataSet set_tj = new LineDataSet(yEntry_tj, "统计值");
			set_tj.enableDashedHighlightLine(10f, 5f, 0f);
			set_tj.setColor(Color.RED);
			set_tj.setCircleColor(Color.RED);
			set_tj.setValueTextColor(Color.RED);
			set_tj.setDrawCubic(true);//设置为平滑曲线
			set_tj.setCubicIntensity(0.1f);//设置平滑度
			set_tj.setLineWidth(1f);
			set_tj.setCircleSize(3f);
			set_tj.setDrawCircleHole(false);
			set_tj.setValueTextSize(9f);
			set_tj.setDrawValues(false);
			set_tj.setFillAlpha(65);
			set_tj.setFillColor(Color.BLACK);
			set_tj.setHighLightColor(Color.RED);//设置标识线的颜色
			set_tj.setHighlightLineWidth(1f);//设置标识线宽度
			set_tj.setHighlightEnabled(true);
			
			LineDataSet set_gh = new LineDataSet(yEntry_gh, rulerName);
			set_gh.enableDashedHighlightLine(10f, 5f, 0f);
			set_gh.setColor(Color.BLACK);
			set_gh.setCircleColor(Color.BLACK);
			set_gh.setValueTextColor(Color.BLACK);
			set_gh.setDrawCubic(true);//设置为平滑曲线
			set_gh.setCubicIntensity(0.1f);//设置平滑度
			set_gh.setLineWidth(1f);
			set_gh.setCircleSize(3f);
			set_gh.setDrawCircleHole(false);
			set_gh.setValueTextSize(9f);
			set_gh.setDrawValues(false);
			set_gh.setFillAlpha(65);
			set_gh.setFillColor(Color.BLACK);
			set_gh.setHighLightColor(Color.BLACK);//设置标识线的颜色
			set_gh.setHighlightLineWidth(1f);//设置标识线宽度
			set_gh.setHighlightEnabled(true);
			
			LineDataSet set_tb = new LineDataSet(yEntry_tb, "同比值");
			set_tb.enableDashedHighlightLine(10f, 5f, 0f);
			set_tb.setColor(Color.BLUE);
			set_tb.setCircleColor(Color.BLUE);
			set_tb.setValueTextColor(Color.BLUE);
			set_tb.setDrawCubic(true);//设置为平滑曲线
			set_tb.setCubicIntensity(0.1f);//设置平滑度
			set_tb.setLineWidth(1f);
			set_tb.setCircleSize(3f);
			set_tb.setDrawCircleHole(false);
			set_tb.setValueTextSize(9f);
			set_tb.setDrawValues(false);
			set_tb.setFillAlpha(65);
			set_tb.setFillColor(Color.BLACK);
			set_tb.setHighLightColor(Color.BLUE);//设置标识线的颜色
			set_tb.setHighlightLineWidth(1f);//设置标识线宽度
			set_tb.setHighlightEnabled(true);
			
			ArrayList<LineDataSet> dataSets = new ArrayList<>();
			dataSets.add(set_gh);
			dataSets.add(set_tj);
			dataSets.add(set_tb);
			LineData data = new LineData(wxAxis, dataSets);
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
	
	/**
	 * 规划  统计  同比
	 */
	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		int index = e.getXIndex();
		Entry ghEntry = null;
		Entry tjEntry = null;
		Entry tbEntry = null;
		for (Entry tmpEntry : yEntry_gh) {
			if (tmpEntry.getXIndex() == index) {
				ghEntry = tmpEntry;
			}
		}
		for (Entry tmpEntry : yEntry_tj) {
			if (tmpEntry.getXIndex() == index) {
				tjEntry = tmpEntry;
			}
		}
		for (Entry tmpeEntry : yEntry_tb) {
			if (tmpeEntry.getXIndex() == index) {
				tbEntry = tmpeEntry;
			}
		}
		if (ghEntry != null && tjEntry != null && tbEntry != null) {
			markerView.refreshContent(ghEntry, tjEntry, tbEntry, h);
		}else if (ghEntry != null && tjEntry != null && tbEntry == null) {
			markerView.refreshContent(ghEntry, tjEntry, h, 2);
		}else if (ghEntry != null && tbEntry != null && tjEntry == null) {
			markerView.refreshContent(ghEntry, tbEntry, h, 1);
		}else if (tjEntry != null && tbEntry != null && ghEntry == null) {
			markerView.refreshContent(tjEntry, tbEntry, h, 0);
		}else if (ghEntry != null && tjEntry == null && tbEntry == null) {
			markerView.refreshContent(ghEntry, h, 0);
		}else if (tjEntry != null && ghEntry == null && tbEntry == null) {
			markerView.refreshContent(tjEntry, h, 1);
		}else if (tbEntry != null && ghEntry == null && tjEntry == null) {
			markerView.refreshContent(tbEntry, h, 2);
		}
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onChartGestureStart(MotionEvent me,
			ChartGesture lastPerformedGesture) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onChartGestureEnd(MotionEvent me,
			ChartGesture lastPerformedGesture) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onChartLongPressed(MotionEvent me) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onChartDoubleTapped(MotionEvent me) {
		lineChart.zoom(2f, 2f, 0, 0);
	}

	@Override
	public void onChartSingleTapped(MotionEvent me) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
		lineChart.getXAxis().resetLabelsToSkip();
		if (lineChart.getScaleX() <= 1) {
			lineChart.getXAxis().setLabelsToSkip(3);
			lineChart.getXAxis().setGridLineCount(0);//设置x轴gridline的条数
		}else if(lineChart.getScaleX() > 1 && lineChart.getScaleX() <= 2){
			lineChart.getXAxis().setLabelsToSkip(2);
		}else if (lineChart.getScaleX() > 2 && lineChart.getScaleX() <= 3) {
			lineChart.getXAxis().setLabelsToSkip(1);
		}else {
			lineChart.getXAxis().setLabelsToSkip(0);
		}
	}

	@Override
	public void onChartTranslate(MotionEvent me, float dX, float dY) {
		// TODO Auto-generated method stub
	}
	
}
