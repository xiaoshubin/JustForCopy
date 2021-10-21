package com.smallcake.temp.chart

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler

/**
 * 自定义高亮显示线为虚线段
 */
class CarLineChartRenderer(chart: LineDataProvider?,animator: ChartAnimator?, viewPortHandler: ViewPortHandler?
) : LineChartRenderer(chart, animator, viewPortHandler) {
    private val mHighlightLinePath = Path()
    override fun drawHighlighted(c: Canvas?, indices: Array<out Highlight>?) {

        indices?.forEach {high->
            val set: ILineDataSet = mChart.lineData.getDataSetByIndex(high.getDataSetIndex())
            if (set == null || !set.isHighlightEnabled) return
            val e = set.getEntryForXValue(high.x, high.y)
            if (!isInBoundsX(e, set)) return
            val pix = mChart.getTransformer(set.axisDependency).getPixelForValues(e.x, e.y * mAnimator.phaseY)
            high.setDraw(pix.x.toFloat(), pix.y.toFloat())
            mHighlightPaint.color = set.highLightColor
            mHighlightPaint.strokeWidth = set.highlightLineWidth
            mHighlightPaint.pathEffect = DashPathEffect(floatArrayOf(5F,5F,5F,5F),1F)
            mHighlightLinePath.reset()
            mHighlightLinePath.moveTo(pix.x.toFloat(), mViewPortHandler.contentTop())
            mHighlightLinePath.lineTo(pix.x.toFloat(), mViewPortHandler.contentBottom())
            c?.drawPath(mHighlightLinePath, mHighlightPaint)
        }
    }

}