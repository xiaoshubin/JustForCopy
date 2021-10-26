package com.smallcake.temp.chart

import android.graphics.*
import android.graphics.drawable.GradientDrawable
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.renderer.RadarChartRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler


/**
 * 绘制了顶角圆点的雷达图渲染器
 */
class RadarChartPointsRenderer(mChart: RadarChart, animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?): RadarChartRenderer (mChart,animator,viewPortHandler){

    override fun drawValues(c: Canvas) {
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY
        val sliceangle = mChart.sliceAngle

        val factor = mChart.factor
        val center = mChart.centerOffsets
        val pOut = MPPointF.getInstance(0f, 0f)
        val pIcon = MPPointF.getInstance(0f, 0f)
        val yoffset = Utils.convertDpToPixel(5f)
        for (i in 0 until mChart.data.dataSetCount) {
            val dataSet = mChart.data.getDataSetByIndex(i)
            if (!shouldDrawValues(dataSet)) continue
            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet)
            val formatter = dataSet.valueFormatter
            val iconsOffset = MPPointF.getInstance(dataSet.iconsOffset)
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x)
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y)
            for (j in 0 until dataSet.entryCount) {
                val entry = dataSet.getEntryForIndex(j)
                Utils.getPosition(
                    center,
                    (entry.y - mChart.yChartMin) * factor * phaseY,
                    sliceangle * j * phaseX + mChart.rotationAngle,
                    pOut
                )
                if (dataSet.isDrawValuesEnabled) {
                    drawValue(
                        c,
                        formatter.getRadarLabel(entry),
                        pOut.x,
                        pOut.y - yoffset,
                        dataSet.getValueTextColor(j)
                    )
                }
                if (entry.icon != null && dataSet.isDrawIconsEnabled) {
                    val icon = entry.icon
                    Utils.getPosition(
                        center,
                        entry.y * factor * phaseY + iconsOffset.y,
                        sliceangle * j * phaseX + mChart.rotationAngle,
                        pIcon
                    )
                    pIcon.y += iconsOffset.x
                    Utils.drawImage(
                        c,
                        icon,
                        pIcon.x.toInt(),
                        pIcon.y.toInt(),
                        icon.intrinsicWidth,
                        icon.intrinsicHeight
                    )
                }
                //绘制雷达图内部数据各个顶点圆点
                if (i ==  mChart.data.dataSetCount - 1) {
                    drawFillCircleMask(c,pOut)
                    drawFillCircle(c,pOut)
                }
            }
            MPPointF.recycleInstance(iconsOffset)
        }
        MPPointF.recycleInstance(center)
        MPPointF.recycleInstance(pOut)
        MPPointF.recycleInstance(pIcon)
    }
    /**
     * 绘制内部数据网各个顶大的渐变圆点
     * @param c Canvas
     * @param pOut MPPointF
     */
    private fun drawFillCircle(c:Canvas,pOut:MPPointF) {
        // 创建渐变的shape drawable
        val colors = intArrayOf(Color.parseColor("#FFF38970"), Color.parseColor("#FFFFFFFF"))
        val drawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors)
        drawable.setSize(6,6)
        drawable.cornerRadius = 60f
        val save = c.save()
        drawable.setBounds(
            (pOut.x-8f).toInt(),
            (pOut.y-8f).toInt(),
            (pOut.x+8f).toInt(),
            (pOut.y+8f).toInt()
        )
        drawable.draw(c)
        c.restoreToCount(save)

    }
    /**
     * 绘制圆的光影
     * @param c Canvas
     * @param pOut MPPointF
     */
    private fun drawFillCircleMask(canvas:Canvas,pOut:MPPointF) {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint.color = Color.parseColor("#FFF35531")
        paint.maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.NORMAL)
        canvas.drawCircle(pOut.x,pOut.y,10f,paint)
    }

}