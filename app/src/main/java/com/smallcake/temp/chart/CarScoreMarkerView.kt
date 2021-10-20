package com.smallcake.temp.chart

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.smallcake.smallutils.text.ShapeTextButton
import com.smallcake.temp.R

/**
 * 车辆数据显示
 * @property tvContent TextView?
 * @constructor
 */
class CarScoreMarkerView(context: Context?, layoutResource: Int = R.layout.marker_car_score) : MarkerView(context, layoutResource) {
    private var tvContent: ShapeTextButton? = null
    init {
         tvContent = findViewById(R.id.tv_content)

    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        tvContent?.text = highlight?.y?.toInt().toString()
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}