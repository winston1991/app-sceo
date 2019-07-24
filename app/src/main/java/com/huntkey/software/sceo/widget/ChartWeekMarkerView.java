package com.huntkey.software.sceo.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.Utils;
import com.huntkey.software.sceo.R;

/**
 * Created by chenl
 */
public class ChartWeekMarkerView extends MarkerView {

	private TextView tvTime;
	private TextView tvGH;
	private TextView tvTJ;
	private TextView tvTB;
	private int digit;
	private String fyear;
	private List<String> dates = new ArrayList<>();
	
	public ChartWeekMarkerView(Context context, int layoutResource, String fyear, List<String> dates) {
		super(context, layoutResource);
		tvTime = (TextView) findViewById(R.id.marker_time);
		tvGH = (TextView) findViewById(R.id.marker_gh);
		tvTJ = (TextView) findViewById(R.id.marker_tj);
		tvTB = (TextView) findViewById(R.id.marker_tb);
		this.fyear = fyear.substring(fyear.length() - 2, fyear.length());
		this.dates = dates;
	}

	@Override
	public void refreshContent(Entry e, Highlight highlight) {
		
	}
	
	/**
	 * 
	 * @param e
	 * @param highlight
	 * @param flag  0-显示规划值  1-显示统计值  2-显示同比值
	 */
	public void refreshContent(Entry e, Highlight highlight, int flag){
		if (e instanceof CandleEntry) {
			CandleEntry ce = (CandleEntry) e;
			if (String.valueOf(ce.getHigh()).contains(".")) {
				digit = String.valueOf(ce.getHigh()).length() - String.valueOf(ce.getHigh()).indexOf(".") - 1;
			}
			if (ce.getXIndex() < 9) {
				tvTime.setText(/*fyear + */"FW0" + Utils.formatNumber(ce.getXIndex() + 1, 0, true) + "(" + dates.get(ce.getXIndex()) + ")");
			}else {				
				tvTime.setText(/*fyear + */"FW" + Utils.formatNumber(ce.getXIndex() + 1, 0, true) + "(" + dates.get(ce.getXIndex()) + ")");
			}
			switch (flag) {
			case 0:
				tvGH.setVisibility(View.VISIBLE);
				tvTJ.setVisibility(View.GONE);
				tvTB.setVisibility(View.GONE);
				tvGH.setText("规划值:" + Utils.formatNumber(ce.getHigh(), digit, true));
				break;
			case 1:
				tvTJ.setVisibility(View.VISIBLE);
				tvGH.setVisibility(View.GONE);
				tvTB.setVisibility(View.GONE);
				tvTJ.setText("统计值:" + Utils.formatNumber(ce.getHigh(), digit, true));
				break;
			case 2:
				tvTB.setVisibility(View.VISIBLE);
				tvTJ.setVisibility(View.GONE);
				tvGH.setVisibility(View.GONE);
				tvTB.setText("同比值:" + Utils.formatNumber(ce.getHigh(), digit, true));
				break;

			default:
				break;
			}
		}else {
			if (String.valueOf(e.getVal()).contains(".")) {
				digit = String.valueOf(e.getVal()).length() - String.valueOf(e.getVal()).indexOf(".") - 1;
			}
			if (e.getXIndex() < 9) {
				tvTime.setText(/*fyear + */"FW0" + Utils.formatNumber(e.getXIndex() + 1, 0, true) + "(" + dates.get(e.getXIndex()) + ")");
			}else {				
				tvTime.setText(/*fyear + */"FW" + Utils.formatNumber(e.getXIndex() + 1, 0, true) + "(" + dates.get(e.getXIndex()) + ")");
			}
			switch (flag) {
			case 0:
				tvGH.setVisibility(View.VISIBLE);
				tvTJ.setVisibility(View.GONE);
				tvTB.setVisibility(View.GONE);
				tvGH.setText("规划值:" + Utils.formatNumber(e.getVal(), digit, true));
				break;
			case 1:
				tvTJ.setVisibility(View.VISIBLE);
				tvGH.setVisibility(View.GONE);
				tvTB.setVisibility(View.GONE);
				tvTJ.setText("统计值:" + Utils.formatNumber(e.getVal(), digit, true));
				break;
			case 2:
				tvTB.setVisibility(View.VISIBLE);
				tvTJ.setVisibility(View.GONE);
				tvGH.setVisibility(View.GONE);
				tvTB.setText("同比值:" + Utils.formatNumber(e.getVal(), digit, true));
				break;

			default:
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param e1   flag=0,e1=统计值    flag=1,e1=规划值    flag=2,e1=规划值
	 * @param e2   flag=0,e2=同比值    flag=1,e2=同比值    flag=2,e2=统计值
	 * @param highlight
	 * @param flag  0-隐藏规划值   1-隐藏统计值   2-隐藏同比值
	 */
	public void refreshContent(Entry e1, Entry e2, Highlight highlight, int flag){
		if (e1 instanceof CandleEntry && e2 instanceof CandleEntry) {
			CandleEntry ce1 = (CandleEntry) e1;
			CandleEntry ce2 = (CandleEntry) e2;
			if (String.valueOf(ce1.getHigh()).contains(".")) {
				digit = String.valueOf(ce1.getHigh()).length() - String.valueOf(ce1.getHigh()).indexOf(".") - 1;
			}
			if (ce1.getXIndex() < 9) {
				tvTime.setText(/*fyear + */"FW0" + Utils.formatNumber(ce1.getXIndex() + 1, 0, true) + "(" + dates.get(ce1.getXIndex()) + ")");
			}else {				
				tvTime.setText(/*fyear + */"FW" + Utils.formatNumber(ce1.getXIndex() + 1, 0, true) + "(" + dates.get(ce1.getXIndex()) + ")");
			}
			switch (flag) {
			case 0:
				tvGH.setVisibility(View.GONE);
				tvTB.setVisibility(View.VISIBLE);
				tvTJ.setVisibility(View.VISIBLE);
				tvTJ.setText("统计值:" + Utils.formatNumber(ce1.getHigh(), digit, true));
				tvTB.setText("同比值:" + Utils.formatNumber(ce2.getHigh(), digit, true));
				break;
			case 1:
				tvTJ.setVisibility(View.GONE);
				tvGH.setVisibility(View.VISIBLE);
				tvTB.setVisibility(View.VISIBLE);
				tvGH.setText("规划值:" + Utils.formatNumber(ce1.getHigh(), digit, true));
				tvTB.setText("同比值:" + Utils.formatNumber(ce2.getHigh(), digit, true));
				break;
			case 2:
				tvTB.setVisibility(View.GONE);
				tvTJ.setVisibility(View.VISIBLE);
				tvGH.setVisibility(View.VISIBLE);
				tvGH.setText("规划值:" + Utils.formatNumber(ce1.getHigh(), digit, true));
				tvTJ.setText("统计值:" + Utils.formatNumber(ce2.getHigh(), digit, true));
				
				break;

			default:
				break;
			}
		}else {
			if (String.valueOf(e1.getVal()).contains(".")) {
				digit = String.valueOf(e1.getVal()).length() - String.valueOf(e1.getVal()).indexOf(".") - 1;
			}
			if (e1.getXIndex() < 9) {
				tvTime.setText(/*fyear + */"FW0" + Utils.formatNumber(e1.getXIndex() + 1, 0, true) + "(" + dates.get(e1.getXIndex()) + ")");
			}else {				
				tvTime.setText(/*fyear + */"FW" + Utils.formatNumber(e1.getXIndex() + 1, 0, true) + "(" + dates.get(e1.getXIndex()) + ")");
			}
			switch (flag) {
			case 0:
				tvGH.setVisibility(View.GONE);
				tvTB.setVisibility(View.VISIBLE);
				tvTJ.setVisibility(View.VISIBLE);
				tvTJ.setText("统计值:" + Utils.formatNumber(e1.getVal(), digit, true));
				tvTB.setText("同比值:" + Utils.formatNumber(e2.getVal(), digit, true));
				break;
			case 1:
				tvTJ.setVisibility(View.GONE);
				tvGH.setVisibility(View.VISIBLE);
				tvTB.setVisibility(View.VISIBLE);
				tvGH.setText("规划值:" + Utils.formatNumber(e1.getVal(), digit, true));
				tvTB.setText("同比值:" + Utils.formatNumber(e2.getVal(), digit, true));
				break;
			case 2:
				tvTB.setVisibility(View.GONE);
				tvTJ.setVisibility(View.VISIBLE);
				tvGH.setVisibility(View.VISIBLE);
				tvGH.setText("规划值:" + Utils.formatNumber(e1.getVal(), digit, true));
				tvTJ.setText("统计值:" + Utils.formatNumber(e2.getVal(), digit, true));
				
				break;

			default:
				break;
			}
		}
	}
	
	/**
	 * 
	 * @param e1   规划值
	 * @param e2   统计值
	 * @param e3   同比值
	 * @param highlight
	 */
	public void refreshContent(Entry e1, Entry e2, Entry e3, Highlight highlight) {
		if (e1 instanceof CandleEntry && e2 instanceof CandleEntry && e3 instanceof CandleEntry) {
			CandleEntry ce1 = (CandleEntry) e1;
			CandleEntry ce2 = (CandleEntry) e2;
			CandleEntry ce3 = (CandleEntry) e3;
			if (String.valueOf(ce1.getHigh()).contains(".")) {
				digit = String.valueOf(ce1.getHigh()).length() - String.valueOf(ce1.getHigh()).indexOf(".") - 1;
			}
			if (ce1.getXIndex() < 9) {
				tvTime.setText(/*fyear + */"FW0" + Utils.formatNumber(ce1.getXIndex() + 1, 0, true) + "(" + dates.get(ce1.getXIndex()) + ")");
			}else {				
				tvTime.setText(/*fyear + */"FW" + Utils.formatNumber(ce1.getXIndex() + 1, 0, true) + "(" + dates.get(ce1.getXIndex()) + ")");
			}
			tvGH.setText("规划值:" + Utils.formatNumber(ce1.getHigh(), digit, true));
			tvTJ.setText("统计值:" + Utils.formatNumber(ce2.getHigh(), digit, true));
			tvTB.setText("同比值:" + Utils.formatNumber(ce3.getHigh(), digit, true));
		}else {
			if (String.valueOf(e1.getVal()).contains(".")) {				
				digit = String.valueOf(e1.getVal()).length() - String.valueOf(e1.getVal()).indexOf(".") - 1;
			}
			if (e1.getXIndex() < 10) {
				tvTime.setText(fyear + "FW0" + Utils.formatNumber(e1.getXIndex() + 1, 0, true));
			}else {				
				tvTime.setText(fyear + "FW" + Utils.formatNumber(e1.getXIndex() + 1, 0, true));
			}
			tvGH.setText("规划值:" + Utils.formatNumber(e1.getVal(), digit, true));
			tvTJ.setText("统计值:" + Utils.formatNumber(e2.getVal(), digit, true));
			tvTB.setText("同比值:" + Utils.formatNumber(e3.getVal(), digit, true));
		}
	}

	@Override
	public int getXOffset(float xpos) {
		return -(getWidth() / 2);
	}

	@Override
	public int getYOffset(float ypos) {
		return -getHeight();
	}

}
