package com.smallcake.temp.chart;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.renderer.PieChartRenderer;
import com.github.mikephil.charting.utils.ViewPortHandler;


/**
 * 支持换行的PieChartRenderer渲染器
 * https://www.it610.com/article/1296492010936541184.htm
 */
public class NewLinePieChartRenderer extends PieChartRenderer {

    public NewLinePieChartRenderer(PieChart chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }
    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        mValuePaint.setColor(color);
        if (valueText.contains(System.getProperty("line.separator"))){
            String[] texts = valueText.split(System.getProperty("line.separator"));
            float textHeight = measureTextHeight(mValuePaint);
            for(String text : texts){
                c.drawText(text, x, y, mValuePaint);
                mValueLinePaint.measureText(text);
                y = y + textHeight;
            }
        }else {
            c.drawText(valueText, x, y, mValuePaint);
        }

    }

    /**
     * 测量画笔文字高度
     * @param paint
     * @return
     */
    private  float measureTextHeight(Paint paint){
        float height = 0f;
        if(null == paint)return height;
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        height = fontMetrics.descent - fontMetrics.ascent;
        return height;
    }

    @Override
    protected void drawCenterText(Canvas c) {
        super.drawCenterText(c);
    }
}
