package com.huntkey.software.sceo.ui.fragment;

import java.util.ArrayList;

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
import com.huntkey.software.sceo.R;
import com.huntkey.software.sceo.bean.FormSecDetails;
import com.huntkey.software.sceo.utils.LineChartUtils;
import com.huntkey.software.sceo.utils.SceoUtil;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.R.integer;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 累计月K线
 * @author chenliang3
 *
 */
public class ChartTotalFragment extends Fragment implements OnChartValueSelectedListener{

	@ViewInject(R.id.chartB_lineChart)
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
			view = inflater.inflate(R.layout.fragment_chart_b, null);
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
		
		float minYValue = string2float(details.getMin_sum());
		float maxYValue = string2float(details.getMax_sum());
		
		//统计值
		ArrayList<Entry> yvalue1 = new ArrayList<>();
		int len = Integer.parseInt(length.substring(length.length()-2));
		for (int i = 0; i < len; i++) {
			if (!"".equals(details.getDetail_c().get(i).getFfl_sum())) {				
				Entry entry = new Entry(string2float(details.getDetail_c().get(i).getFfl_sum()), i);
				yvalue1.add(entry);
			}
		}
		
		//规划值
		ArrayList<Entry> yvalue2 = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			if (!"".equals(details.getDetail_c().get(i).getRuler_sum())) {
				Entry entry = new Entry(string2float(details.getDetail_c().get(i).getRuler_sum()), i);
				yvalue2.add(entry);
			}
		}
		
		//同比值
		ArrayList<Entry> yvalue3 = new ArrayList<>();
		for (int i = 0; i < 12; i++) {
			if (!"".equals(details.getDetail_c().get(i).getP_ffl_sum())) {
				Entry entry = new Entry(string2float(details.getDetail_c().get(i).getP_ffl_sum()), i);
				yvalue3.add(entry);
			}
		}
		
		if (yvalue1.size() > 0 || yvalue2.size() > 0 || yvalue3.size() > 0) {			
			LineChartUtils.chartBuild(getActivity(), details, xvalue, lineChart, 
					minYValue, maxYValue, yvalue1, yvalue2, yvalue3);
		}
	}

	@Override
	public void onValueSelected(final Entry e, int dataSetIndex, Highlight h) {
		
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
