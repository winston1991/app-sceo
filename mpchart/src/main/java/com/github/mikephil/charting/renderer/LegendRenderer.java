
package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LegendRenderer extends Renderer {

    /**
     * paint for the legend labels
     */
    protected Paint mLegendLabelPaint;

    /**
     * paint used for the legend forms
     */
    protected Paint mLegendFormPaint;

    /**
     * the legend object this renderer renders
     */
    protected Legend mLegend;

    public LegendRenderer(ViewPortHandler viewPortHandler, Legend legend) {
        super(viewPortHandler);

        this.mLegend = legend;

        mLegendLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendLabelPaint.setTextSize(Utils.convertDpToPixel(9f));
        mLegendLabelPaint.setTextAlign(Align.LEFT);

        mLegendFormPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLegendFormPaint.setStyle(Paint.Style.FILL);
        mLegendFormPaint.setStrokeWidth(10f);
    }

    /**
     * Returns the Paint object used for drawing the Legend labels.
     *
     * @return
     */
    public Paint getLabelPaint() {
        return mLegendLabelPaint;
    }

    /**
     * Returns the Paint object used for drawing the Legend forms.
     *
     * @return
     */
    public Paint getFormPaint() {
        return mLegendFormPaint;
    }

    /**
     * Prepares the legend and calculates all needed forms, labels and colors.
     *
     * @param data
     */
    public void computeLegend(ChartData<?> data) {

        if (!mLegend.isLegendCustom()) {

            List<String> labels = new ArrayList<String>();
            List<Integer> colors = new ArrayList<Integer>();

            // loop for building up the colors and labels used in the legend
            for (int i = 0; i < data.getDataSetCount(); i++) {

                DataSet<? extends Entry> dataSet = data.getDataSetByIndex(i);

                List<Integer> clrs = dataSet.getColors();
                int entryCount = dataSet.getEntryCount();

                // if we have a barchart with stacked bars
                if (dataSet instanceof BarDataSet && ((BarDataSet) dataSet).isStacked()) {

                    BarDataSet bds = (BarDataSet) dataSet;
                    String[] sLabels = bds.getStackLabels();

                    for (int j = 0; j < clrs.size() && j < bds.getStackSize(); j++) {

                        labels.add(sLabels[j % sLabels.length]);
                        colors.add(clrs.get(j));
                    }

                    if (bds.getLabel() != null) {
                        // add the legend description label
                        colors.add(ColorTemplate.COLOR_SKIP);
                        labels.add(bds.getLabel());
                    }

                } else if (dataSet instanceof PieDataSet) {

                    List<String> xVals = data.getXVals();
                    PieDataSet pds = (PieDataSet) dataSet;

                    for (int j = 0; j < clrs.size() && j < entryCount && j < xVals.size(); j++) {

                        labels.add(xVals.get(j));
                        colors.add(clrs.get(j));
                    }

                    if (pds.getLabel() != null) {
                        // add the legend description label
                        colors.add(ColorTemplate.COLOR_SKIP);
                        labels.add(pds.getLabel());
                    }

                } else { // all others

                    for (int j = 0; j < clrs.size() && j < entryCount; j++) {

                        // if multiple colors are set for a DataSet, group them
                        if (j < clrs.size() - 1 && j < entryCount - 1) {

                            labels.add(null);
                        } else { // add label to the last entry

                            String label = data.getDataSetByIndex(i).getLabel();
                            labels.add(label);
                        }

                        colors.add(clrs.get(j));
                    }
                }
            }

            if (mLegend.getExtraColors() != null && mLegend.getExtraLabels() != null) {
                for (int color : mLegend.getExtraColors())
                    colors.add(color);
                Collections.addAll(labels, mLegend.getExtraLabels());
            }

            mLegend.setComputedColors(colors);
            mLegend.setComputedLabels(labels);
        }

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        // calculate all dimensions of the mLegend
        mLegend.calculateDimensions(mLegendLabelPaint, mViewPortHandler);
    }

    public void renderLegend(Canvas c) {

        if (!mLegend.isEnabled())
            return;

        Typeface tf = mLegend.getTypeface();

        if (tf != null)
            mLegendLabelPaint.setTypeface(tf);

        mLegendLabelPaint.setTextSize(mLegend.getTextSize());
        mLegendLabelPaint.setColor(mLegend.getTextColor());

        float labelLineHeight = Utils.getLineHeight(mLegendLabelPaint);
        float labelLineSpacing = Utils.getLineSpacing(mLegendLabelPaint) + mLegend.getYEntrySpace();
        float formYOffset = labelLineHeight - Utils.calcTextHeight(mLegendLabelPaint, "ABC") / 2.f;

        String[] labels = mLegend.getLabels();
        int[] colors = mLegend.getColors();

        float formToTextSpace = mLegend.getFormToTextSpace();
        float xEntrySpace = mLegend.getXEntrySpace();
        Legend.LegendDirection direction = mLegend.getDirection();
        float formSize = mLegend.getFormSize();

        // space between the entries
        float stackSpace = mLegend.getStackSpace();

        float posX, posY;

        float yoffset = mLegend.getYOffset();
        float xoffset = mLegend.getXOffset();

        Legend.LegendPosition legendPosition = mLegend.getPosition();

        switch (legendPosition) {
            case BELOW_CHART_LEFT:
            case BELOW_CHART_RIGHT:
            case BELOW_CHART_CENTER:
            case ABOVE_CHART_LEFT:
            case ABOVE_CHART_RIGHT:
            case ABOVE_CHART_CENTER: {
                float contentWidth = mViewPortHandler.contentWidth();

                float originPosX;

                if (legendPosition == Legend.LegendPosition.BELOW_CHART_LEFT
                        || legendPosition == Legend.LegendPosition.ABOVE_CHART_LEFT) {
                    originPosX = mViewPortHandler.contentLeft() + xoffset;

                    if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                        originPosX += mLegend.mNeededWidth;
                } else if (legendPosition == Legend.LegendPosition.BELOW_CHART_RIGHT
                        || legendPosition == Legend.LegendPosition.ABOVE_CHART_RIGHT) {
                    originPosX = mViewPortHandler.contentRight() - xoffset;

                    if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                        originPosX -= mLegend.mNeededWidth;
                } else // BELOW_CHART_CENTER || ABOVE_CHART_CENTER
                    originPosX = mViewPortHandler.contentLeft() + contentWidth / 2.f;

                FSize[] calculatedLineSizes = mLegend.getCalculatedLineSizes();
                FSize[] calculatedLabelSizes = mLegend.getCalculatedLabelSizes();
                Boolean[] calculatedLabelBreakPoints = mLegend.getCalculatedLabelBreakPoints();

                posX = originPosX;

                if (legendPosition == Legend.LegendPosition.ABOVE_CHART_LEFT ||
                        legendPosition == Legend.LegendPosition.ABOVE_CHART_RIGHT ||
                        legendPosition == Legend.LegendPosition.ABOVE_CHART_CENTER) {
                    posY = 0.f;
                } else {
                    posY = mViewPortHandler.getChartHeight() - yoffset - mLegend.mNeededHeight;
                }

                int lineIndex = 0;

                for (int i = 0, count = labels.length; i < count; i++) {
                    if (i < calculatedLabelBreakPoints.length && calculatedLabelBreakPoints[i]) {
                        posX = originPosX;
                        posY += labelLineHeight + labelLineSpacing;
                    }

                    if (posX == originPosX && legendPosition == Legend.LegendPosition.BELOW_CHART_CENTER && lineIndex < calculatedLineSizes.length) {
                        posX += (direction == Legend.LegendDirection.RIGHT_TO_LEFT ? calculatedLineSizes[lineIndex].width : -calculatedLineSizes[lineIndex].width) / 2.f;
                        lineIndex++;
                    }

                    boolean drawingForm = colors[i] != ColorTemplate.COLOR_SKIP;
                    boolean isStacked = labels[i] == null; // grouped forms have null labels

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= formSize;

                        drawForm(c, posX, posY + formYOffset, i, mLegend);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += formSize;
                    }

                    if (!isStacked) {
                        if (drawingForm)
                            posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -formToTextSpace : formToTextSpace;

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX -= calculatedLabelSizes[i].width;

                        drawLabel(c, posX, posY + labelLineHeight, labels[i]);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX += calculatedLabelSizes[i].width;

                        posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -xEntrySpace : xEntrySpace;
                    } else
                        posX += direction == Legend.LegendDirection.RIGHT_TO_LEFT ? -stackSpace : stackSpace;
                }

            }
            break;

            case PIECHART_CENTER:
            case RIGHT_OF_CHART:
            case RIGHT_OF_CHART_CENTER:
            case RIGHT_OF_CHART_INSIDE:
            case LEFT_OF_CHART:
            case LEFT_OF_CHART_CENTER:
            case LEFT_OF_CHART_INSIDE: {
                // contains the stacked legend size in pixels
                float stack = 0f;
                boolean wasStacked = false;

                if (legendPosition == Legend.LegendPosition.PIECHART_CENTER) {
                    posX = mViewPortHandler.getChartWidth() / 2f
                            + (direction == Legend.LegendDirection.LEFT_TO_RIGHT ? -mLegend.mTextWidthMax / 2f
                            : mLegend.mTextWidthMax / 2f);
                    posY = mViewPortHandler.getChartHeight() / 2f - mLegend.mNeededHeight / 2f
                            + mLegend.getYOffset();
                } else {
                    boolean isRightAligned = legendPosition == Legend.LegendPosition.RIGHT_OF_CHART
                            ||
                            legendPosition == Legend.LegendPosition.RIGHT_OF_CHART_CENTER ||
                            legendPosition == Legend.LegendPosition.RIGHT_OF_CHART_INSIDE;

                    if (isRightAligned) {
                        posX = mViewPortHandler.getChartWidth() - xoffset;
                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            posX -= mLegend.mTextWidthMax;
                    } else {
                        posX = xoffset;
                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            posX += mLegend.mTextWidthMax;
                    }

                    if (legendPosition == Legend.LegendPosition.RIGHT_OF_CHART ||
                            legendPosition == Legend.LegendPosition.LEFT_OF_CHART) {
                        posY = mViewPortHandler.contentTop() + yoffset;
                    } else if (legendPosition == Legend.LegendPosition.RIGHT_OF_CHART_CENTER ||
                            legendPosition == Legend.LegendPosition.LEFT_OF_CHART_CENTER) {
                        posY = mViewPortHandler.getChartHeight() / 2f - mLegend.mNeededHeight / 2f;
                    } else /*
                            * if (legendPosition ==
                            * Legend.LegendPosition.RIGHT_OF_CHART_INSIDE ||
                            * legendPosition ==
                            * Legend.LegendPosition.LEFT_OF_CHART_INSIDE)
                            */ {
                        posY = mViewPortHandler.contentTop() + yoffset;
                    }
                }

                for (int i = 0; i < labels.length; i++) {

                    Boolean drawingForm = colors[i] != ColorTemplate.COLOR_SKIP;
                    float x = posX;

                    if (drawingForm) {
                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            x += stack;
                        else
                            x -= formSize - stack;

                        drawForm(c, x, posY + formYOffset, i, mLegend);

                        if (direction == Legend.LegendDirection.LEFT_TO_RIGHT)
                            x += formSize;
                    }

                    if (labels[i] != null) {

                        if (drawingForm && !wasStacked)
                            x += direction == Legend.LegendDirection.LEFT_TO_RIGHT ? formToTextSpace
                                    : -formToTextSpace;
                        else if (wasStacked)
                            x = posX;

                        if (direction == Legend.LegendDirection.RIGHT_TO_LEFT)
                            x -= Utils.calcTextWidth(mLegendLabelPaint, labels[i]);

                        if (!wasStacked) {
                            drawLabel(c, x, posY + labelLineHeight, labels[i]);
                        } else {
                            posY += labelLineHeight + labelLineSpacing;
                            drawLabel(c, x, posY + labelLineHeight, labels[i]);
                        }

                        // make a step down
                        posY += labelLineHeight + labelLineSpacing;
                        stack = 0f;
                    } else {
                        stack += formSize + stackSpace;
                        wasStacked = true;
                    }
                }
            }
            break;
        }
    }

    /**
     * Draws the Legend-form at the given position with the color at the given
     * index.
     *
     * @param c     canvas to draw with
     * @param x     position
     * @param y     position
     * @param index the index of the color to use (in the colors array)
     */
    protected void drawForm(Canvas c, float x, float y, int index, Legend legend) {

        if (legend.getColors()[index] == ColorTemplate.COLOR_SKIP)
            return;

        mLegendFormPaint.setColor(legend.getColors()[index]);

        float formsize = legend.getFormSize();
        float half = formsize / 2f;

        switch (legend.getForm()) {
            case CIRCLE:
                c.drawCircle(x + half, y, half, mLegendFormPaint);
                break;
            case SQUARE:
                c.drawRect(x, y - half, x + formsize, y + half, mLegendFormPaint);
                break;
            case LINE:
                c.drawLine(x, y, x + formsize, y, mLegendFormPaint);
                break;
        }
    }

    /**
     * Draws the provided label at the given position.
     *
     * @param c     canvas to draw with
     * @param x
     * @param y
     * @param label the label to draw
     */
    protected void drawLabel(Canvas c, float x, float y, String label) {
        c.drawText(label, x, y, mLegendLabelPaint);
    }
}
