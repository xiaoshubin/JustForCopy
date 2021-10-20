package com.smallcake.temp.chart

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.highlight.Highlight


class CarLineChart @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null, defStyle: Int): LineChart(context,attrs,defStyle){

    // 获取高亮点坐标
    fun getHighLightPos(highlight: Highlight): FloatArray {
        return getMarkerPosition(highlight)
    }

    override fun drawMarkers(canvas: Canvas?) {
        super.drawMarkers(canvas)

    }

}