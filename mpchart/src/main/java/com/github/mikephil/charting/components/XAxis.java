
package com.github.mikephil.charting.components;

import com.github.mikephil.charting.formatter.DefaultXAxisValueFormatter;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the x-axis labels settings. Only use the setter methods to
 * modify it. Do not access public variables directly. Be aware that not all
 * features the XLabels class provides are suitable for the RadarChart.
 * 
 * @author Philipp Jahoda
 */
public class XAxis extends AxisBase {

    /** the arraylist containing all the x-axis labels */
    protected List<String> mValues = new ArrayList<String>();

    /**
     * width of the x-axis labels in pixels - this is calculated by the
     * calcTextWidth() method of the utils
     */
    public int mLabelWidth = 1;

    /**
     * height of the x-axis labels in pixels - this is calculated by the
     * calcTextHeight() method of the utils
     */
    public int mLabelHeight = 1;

    /**
     * the space that should be left out (in characters) between the x-axis
     * labels
     */
    private int mSpaceBetweenLabels = 4;

    /**
     * the modulus that indicates if a value at a specified index in an
     * array(list) for the x-axis-labels is drawn or not. If index % modulus ==
     * 0 DRAW, else dont draw.
     */
    public int mAxisLabelModulus = 1;
    
    public int mAxisGridLineCount = 1;

    /**
     * Is axisLabelModulus a custom value or auto calculated? If false, then
     * it's auto, if true, then custom. default: false (automatic modulus)
     */
    private boolean mIsAxisModulusCustom = false;

    /**
     * the modulus that indicates if a value at a specified index in an
     * array(list) for the y-axis-labels is drawn or not. If index % modulus ==
     * 0 DRAW, else dont draw. THIS IS ONLY FOR HORIZONTAL BARCHART.
     */
    public int mYAxisLabelModulus = 1;

    /**
     * if set to true, the chart will avoid that the first and last label entry
     * in the chart "clip" off the edge of the chart
     */
    private boolean mAvoidFirstLastClipping = false;

    /**
     * Custom formatter for adjusting x-value strings
     */
    protected XAxisValueFormatter mXAxisValueFormatter = new DefaultXAxisValueFormatter();

    /** the position of the x-labels relative to the chart */
    private XAxisPosition mPosition = XAxisPosition.TOP;

    /** enum for the position of the x-labels relative to the chart */
    public enum XAxisPosition {
        TOP, BOTTOM, BOTH_SIDED, TOP_INSIDE, BOTTOM_INSIDE
    }

    public XAxis() {
        super();
    }

    /**
     * returns the position of the x-labels
     */
    public XAxisPosition getPosition() {
        return mPosition;
    }

    /**
     * sets the position of the x-labels
     * 
     * @param pos
     */
    public void setPosition(XAxisPosition pos) {
        mPosition = pos;
    }

    /**
     * Sets the space (in characters) that should be left out between the x-axis
     * labels, default 4. This only applies if the number of labels that will be
     * skipped in between drawn axis labels is not custom set.
     * 
     * @param spaceCharacters
     */
    public void setSpaceBetweenLabels(int spaceCharacters) {
        mSpaceBetweenLabels = spaceCharacters;
    }

    /**
     * Sets the number of labels that should be skipped on the axis before the
     * next label is drawn. This will disable the feature that automatically
     * calculates an adequate space between the axis labels and set the number
     * of labels to be skipped to the fixed number provided by this method. Call
     * resetLabelsToSkip(...) to re-enable automatic calculation.
     * 
     * @param count
     */
    public void setLabelsToSkip(int count) {

        if (count < 0)
            count = 0;

        mIsAxisModulusCustom = true;
        mAxisLabelModulus = count + 1;
        mAxisGridLineCount = mAxisLabelModulus;
    }
    
    /**
     * 设置gridline显示条数(若不设置该项，gridline显示条数和x轴显示数字数量一致)
     * @param count
     */
    public void setGridLineCount(int count){
    	if (count < 0) {
			count = 0;
		}
    	
    	mAxisGridLineCount = count + 1;
    }

    /**
     * Calling this will disable a custom number of labels to be skipped (set by
     * setLabelsToSkip(...)) while drawing the x-axis. Instead, the number of
     * values to skip will again be calculated automatically.
     */
    public void resetLabelsToSkip() {
        mIsAxisModulusCustom = false;
    }

    /**
     * Returns true if a custom axis-modulus has been set that determines the
     * number of labels to skip when drawing.
     * 
     * @return
     */
    public boolean isAxisModulusCustom() {
        return mIsAxisModulusCustom;
    }

    /**
     * Returns the space (in characters) that should be left out between the
     * x-axis labels
     */
    public int getSpaceBetweenLabels() {
        return mSpaceBetweenLabels;
    }

    /**
     * if set to true, the chart will avoid that the first and last label entry
     * in the chart "clip" off the edge of the chart or the screen
     * 
     * @param enabled
     */
    public void setAvoidFirstLastClipping(boolean enabled) {
        mAvoidFirstLastClipping = enabled;
    }

    /**
     * returns true if avoid-first-lastclipping is enabled, false if not
     * 
     * @return
     */
    public boolean isAvoidFirstLastClippingEnabled() {
        return mAvoidFirstLastClipping;
    }

    /**
     * Sets the labels for this axis.
     * 
     * @param values
     */
    public void setValues(List<String> values) {
        mValues = values;
    }

    /**
     * Returns the labels for this axis.
     * 
     * @return
     */
    public List<String> getValues() {
        return mValues;
    }


    /**
     * Sets a custom XAxisValueFormatter for the data object that allows custom-formatting
     * of all x-values before rendering them. Provide null to reset back to the
     * default formatting.
     *
     * @param formatter
     */
    public void setValueFormatter(XAxisValueFormatter formatter) {
        if(formatter == null)
            mXAxisValueFormatter = new DefaultXAxisValueFormatter();
        else
            mXAxisValueFormatter = formatter;
    }

    /**
     * Returns the custom XAxisValueFormatter that is set for this data object.
     * @return
     */
    public XAxisValueFormatter getValueFormatter() {
        return mXAxisValueFormatter;
    }

    @Override
    public String getLongestLabel() {

        String longest = "";

        for (int i = 0; i < mValues.size(); i++) {
            String text = mValues.get(i);

            if (longest.length() < text.length())
                longest = text;
        }

        return longest;
    }
}
