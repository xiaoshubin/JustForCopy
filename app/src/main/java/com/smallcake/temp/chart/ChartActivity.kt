package com.smallcake.temp.chart

import android.graphics.Color
import android.os.Bundle
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityChartBinding

/**
 * MPChart官方文档：
 * https://weeklycoding.com/mpandroidchart-documentation/getting-started/
 * 雷达图参考：
 * https://blog.csdn.net/petterp/article/details/90115690
 *
 * 雷达图问题：
 * 1.设置Y轴的文字偏移量无效
 * 2.默认显示数据各个点
 */
class ChartActivity : BaseBindActivity<ActivityChartBinding>() {


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("雷达图")
        initView()
    }
    private fun initView() {
        bind.radarChart.apply {
            isRotationEnabled = false
            setBackgroundColor(Color.parseColor("#FF2A375E"))
            //禁用图例和图表描述
            description.isEnabled = false
            legend.isEnabled = false
            webLineWidth = 1f
            webColor = Color.parseColor("#FF2773E6")
            webLineWidthInner = 1f
            webColorInner = Color.parseColor("#FF2773E6")
            webAlpha = 100

            //设置x轴标签字体颜色
            xAxis.textSize = 12f
            //自定义5个角的标签
            xAxis.valueFormatter = object :ValueFormatter(){
                override fun getFormattedValue(value: Float): String {
                    xAxis.textColor = Color.WHITE
                   return when(value){
                        0f->"安全问题"
                        1f->"违法"
                        2f->"风险"
                        3f->"动态报警"
                        4f->"隐患"
                        else->"--"
                    }
                }
            }
            //设置y轴的标签个数
            yAxis.apply {
               setLabelCount(6, true)
                //设置y轴从0f开始
               axisMinimum = 0f
               axisMaximum = 100f
               //启用绘制Y轴顶点标签，这个是最新添加的功能
               setDrawTopYLabelEntry(true)
                //设置字体大小
               textSize = 10f
                //设置字体颜色
               textColor = Color.WHITE
            }

        }
        setData()
    }

    private fun setData() {
        //网格背景色
        val entries0: ArrayList<RadarEntry> = ArrayList()
        entries0.add(RadarEntry(100f))
        entries0.add(RadarEntry(100f))
        entries0.add(RadarEntry(100f))
        entries0.add(RadarEntry(100f))
        entries0.add(RadarEntry(100f))
        val set0 = RadarDataSet(entries0, "")
        set0.apply {
            color = Color.TRANSPARENT
            fillColor = Color.parseColor("#202f6d")
            setDrawFilled(true)
            fillAlpha = 100
            lineWidth = 0.5f
            valueTextColor = Color.WHITE
            isDrawHighlightCircleEnabled = true
            setDrawValues(true)
            setDrawHighlightIndicators(false)
        }
        //实际数据
        val entries1: ArrayList<RadarEntry> = ArrayList()
        entries1.add(RadarEntry(20f))
        entries1.add(RadarEntry(40f))
        entries1.add(RadarEntry(70f))
        entries1.add(RadarEntry(60f))
        entries1.add(RadarEntry(40f))


        val set1 = RadarDataSet(entries1, "")
        set1.apply {
            color = Color.parseColor("#FFE35838")
            fillColor = Color.parseColor("#FFF35531")
            setDrawFilled(true)
            fillAlpha = 100
            lineWidth = 0.5f
            valueTextColor = Color.WHITE

            isDrawHighlightCircleEnabled = true
            highlightCircleFillColor = Color.parseColor("#FFF38970")
            highlightCircleStrokeColor = Color.WHITE
            highlightCircleInnerRadius = 3f
            highlightCircleOuterRadius = 4f
            highlightCircleStrokeWidth = 2f
            setDrawValues(false)
            setDrawHighlightIndicators(false)
        }

        val sets: ArrayList<IRadarDataSet> = ArrayList()
        sets.add(set0)
        sets.add(set1)

        val data = RadarData(sets)
        data.setValueTextSize(8f)
        data.setDrawValues(false)
        data.setLabels("8888")
        data.setValueTextColor(Color.WHITE)
        bind.radarChart.data = data
        bind.radarChart.invalidate()
    }
}