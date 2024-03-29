package com.smallcake.temp.chart

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.smallcake.smallutils.SpannableStringUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityChartBinding


/**
 * MPAndroidChart Github地址：
 * https://github.com/PhilJay/MPAndroidChart
 * MPChart官方文档：
 * https://weeklycoding.com/mpandroidchart-documentation/getting-started/
 * 优秀博客系列参考；
 * https://blog.csdn.net/dt235201314/article/details/70142117
 *
 *
 * 雷达图参考：
 * https://blog.csdn.net/petterp/article/details/90115690
 * [RadarChart（雷达蜘蛛图）绘制圆点]https://blog.csdn.net/qq_27489007/article/details/107083691
 * 曲线图参考：
 * https://www.jianshu.com/p/185e50a70aa7
 * 饼状图参考：
 * https://blog.csdn.net/baidu_31956557/article/details/80930116
 * 设置MarkerView参考：
 * https://www.jianshu.com/p/54d7322ee1e1
 * https://blog.csdn.net/qq_26787115/article/details/53199030
 *
 * 雷达图问题：
 * 1.设置Y轴的文字偏移量无效
 * 2.默认显示数据各个点
 *
 *
 */
class ChartActivity : BaseBindActivity<ActivityChartBinding>() {


    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("雷达图")
        initView()
    }

    private fun initView() {
        //自定义View，雷达图
        bind.ldChart.setData(listOf(
            LdChartView.LdTxtValue("诚信",86.30f),
            LdChartView.LdTxtValue("师资力量",64.78f),
            LdChartView.LdTxtValue("学员评价",31.58f),
            LdChartView.LdTxtValue("投诉",70.24f),
            LdChartView.LdTxtValue("违规",20.36f),
        ))
        initRadarChart()
        initLineChart()
        initPieChart()
        initBarChart()
    }
    private fun initBarChart() {
        bind.barChart.apply {
            setDrawBorders(false)
            animateY(1000)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description.isEnabled = false
            setMaxVisibleValueCount(60)
            setPinchZoom(false)
            setDrawGridBackground(false)
            setTouchEnabled(false)
            xAxis.apply {
                mAxisMinimum=-0.5f
//                setCenterAxisLabels(true)//将X轴的值显示在中央
                setDrawAxisLine(false)
                setDrawLimitLinesBehindData(false)
                //是否绘制垂直竖线
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val i = value.toInt()
                        return "部门${i+1}"
                    }
                }
            }

            axisLeft.apply {
                setDrawAxisLine(false)
                setDrawZeroLine(false)
                setLabelCount(5, false)
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return value.toInt().toString()
                    }
                }
//                setPosition(YAxisLabelPosition.OUTSIDE_CHART)
//                spaceTop = 15f
                axisMinimum = 0f // this replaces setStartAtZero(true)
            }
            axisRight.isEnabled=false
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                form = Legend.LegendForm.SQUARE
                formSize = 9f
                textSize = 11f
                xEntrySpace = 4f
            }

        }
        setBarChartData()
    }
    private fun setBarChartData() {
        val values1: ArrayList<BarEntry> = ArrayList()
        val values2: ArrayList<BarEntry> = ArrayList()
        for (i in 0..4) {
            values1.add(BarEntry(i.toFloat(), (Math.random() * 100f).toFloat() ))
            values2.add(BarEntry(i.toFloat(), (Math.random() * 100f).toFloat() ))
        }
        val set1 = BarDataSet(values1, "检查次数")
        set1.color = Color.rgb(104, 241, 175)
        val set2 = BarDataSet(values2, "隐患数")
        set2.color = Color.rgb(164, 228, 251)
        val data = BarData(set1, set2)
        bind.barChart.data = data
        bind.barChart.xAxis.axisMaximum = data.xMax+0.5f
        bind.barChart.barData.barWidth=0.4f
        bind.barChart.groupBars(-0.5f, 0.2f, 0f)
        bind.barChart.invalidate()
    }

    private fun initPieChart() {
        bind.pieChart.apply {
            //从新定义可以换行的渲染器
            renderer = NewLinePieChartRenderer(this,animator,viewPortHandler)
            setUsePercentValues(true)//是否以百分比的值来显示圆环

            description.isEnabled = false
            legend.isEnabled = true

            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f

            val centerStr = SpannableStringUtils.getBuilder("+10%")
                .setForegroundColor(Color.parseColor("#FFFFAA45")).setProportion(1.8f).setBold()
                .append("\n同比昨天").create()

            centerText = centerStr
            isDrawHoleEnabled = true//是否显示中间空心
            setHoleColor(Color.WHITE)
            setTransparentCircleColor(Color.WHITE)
            setTransparentCircleAlpha(110)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                }

                override fun onNothingSelected() {
                }
            })
            legend.apply {
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                orientation = Legend.LegendOrientation.VERTICAL
                setDrawInside(false)
                xEntrySpace = 7f
                yEntrySpace = 0f
                yOffset = 0f
            }
            setDrawEntryLabels(false)//是否绘制文字到圆环上
            setEntryLabelColor(Color.RED) //描述文字的颜色
            setEntryLabelTextSize(14f)//描述文字的大小
            setEntryLabelTypeface(Typeface.DEFAULT_BOLD) //描述文字的样式
            animateY(1400, Easing.EaseInOutQuad)


        }
        setPieChartData()

    }
    private fun setPieChartData() {
        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(20f,"超速报警"))
        entries.add(PieEntry(30f,"2-5小时禁行"))
        entries.add(PieEntry(30f,"其他报警"))
        entries.add(PieEntry(20f,"疲劳驾驶"))
        val dataSet = PieDataSet(entries, "")
        dataSet.apply {
            values = entries
            valueTextSize = 12f
            valueTextColor = Color.BLACK
            sliceSpace = 3f//圆环之间的间隙
            iconsOffset = MPPointF(0f, 40f)
            selectionShift = 5f

            //当值位置为外边线时，表示线的前半段长度。
            valueLinePart1Length = 0.3f
            //当值位置为外边线时，表示线的后半段长度。
            valueLinePart2Length = 0.7f
            //当ValuePosits为OutsiDice时，指示偏移为切片大小的百分比
            valueLinePart1OffsetPercentage = 80f
            //当值位置为外边线时，表示线的颜色
            valueLineColor = Color.parseColor("#a1a1a1")
            //设置Y值的位置是在圆内还是圆外
            xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            //设置Y轴描述线和填充区域的颜色一致
            isUsingSliceColorAsValueLineColor = true




        }


        val colors: ArrayList<Int> = ArrayList()

        colors.add(Color.parseColor("#FF807AFF"))//紫色
        colors.add(Color.parseColor("#FCF25C5D"))//红色
        colors.add(Color.parseColor("#FFEDAE5D"))//黄色
        colors.add(Color.parseColor("#FF3FBCFC"))//蓝色
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueFormatter(object :ValueFormatter(){
            override fun getPieLabel(value: Float, pieEntry: PieEntry?): String {
                return "${pieEntry?.label}\n${pieEntry?.value}%"
            }

        })
        bind.pieChart.data = data
        bind.pieChart.invalidate()
    }


    private fun initLineChart() {
        val tvGrayColor = Color.parseColor("#FF9C9FA9")

        bind.lineChart.apply {
            renderer = CarLineChartRenderer(this,animator,viewPortHandler)
            //避免底部文字显示不完整
            extraBottomOffset = 16f
            //设置marker
            val marker = CarScoreMarkerView(context)
            setDrawMarkers(true)
            this.marker = marker
            //值选择事件
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                }

                override fun onNothingSelected() {
                }

            })
            description.isEnabled = false
            setTouchEnabled(true)

            dragDecelerationFrictionCoef = 0.9f
            isDragEnabled = true
            setScaleEnabled(true)
            setDrawGridBackground(false)
            isHighlightPerDragEnabled = true

            setPinchZoom(true)
            setBackgroundColor(Color.TRANSPARENT)
            animateY(500)

            legend.apply {
                form = Legend.LegendForm.CIRCLE
                textSize = 12f
                textColor = tvGrayColor
                yOffset = 8f
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
            }

            xAxis.apply {
                textSize = 12f
                textColor = tvGrayColor
                axisMinimum = 0f
                axisMaximum = 6.2f
                setDrawGridLines(true)
                setDrawAxisLine(false)
                xOffset = 0.5f
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return when (value) {
                            0f -> "星期一"
                            1f -> "星期二"
                            2f -> "星期三"
                            3f -> "星期四"
                            4f -> "星期五"
                            5f -> "星期六"
                            6f -> "星期日"
                            else -> ""
                        }
                    }
                }

            }

            axisLeft.apply {
                textSize = 12f
                textColor = tvGrayColor
                axisMaximum = 40f
                axisMinimum = 0f
                setDrawGridLines(true)
                setDrawTopYLabelEntry(true)//是否绘制最顶点数值
            }


            axisRight.apply {
                setDrawGridLines(false)
                isGranularityEnabled = false
                setDrawZeroLine(false)
                setDrawAxisLine(false)
                setDrawLabels(false)
                setDrawBorders(false)
                setDrawGridBackground(false)
            }

        }

        setLineChartData(7, 30f)
    }

    @SuppressLint("ResourceType")
    private fun setLineChartData(count: Int, range: Float) {

        val colorBlue = Color.parseColor("#FF3DABFF")
        val colorRed = Color.parseColor("#FFFF2D55")
        val colorYellow = Color.parseColor("#FFFFCC00")
        val colorGray = Color.parseColor("#FF9C9FA9")
        val drawableBlue = ContextCompat.getDrawable(this, R.drawable.gradient_blue_trans)
        val drawableYellow = ContextCompat.getDrawable(this, R.drawable.gradient_yellow_trans)
        val drawableRed = ContextCompat.getDrawable(this, R.drawable.gradient_red_trans)

        val values1: ArrayList<Entry> = ArrayList()
        for (i in 0 until count) {
            val num = (Math.random() * range).toFloat()
            values1.add(Entry(i.toFloat() + 0.1f, num))
        }
        val values2: ArrayList<Entry> = ArrayList()
        for (i in 0 until count) {
            val num = (Math.random() * range).toFloat()
            values2.add(Entry(i.toFloat() + 0.1f, num))
        }
        val values3: ArrayList<Entry> = ArrayList()
        for (i in 0 until count) {
            val num = (Math.random() * range).toFloat()
            values3.add(Entry(i.toFloat() + 0.1f, num))
        }

        val set1 = LineDataSet(values1, "蓝色报警车辆")
        set1.apply {

            axisDependency = YAxis.AxisDependency.LEFT
            color = colorBlue
            mode = LineDataSet.Mode.CUBIC_BEZIER//贝塞尔曲线
            setDrawFilled(true)
            setCircleColor(colorRed)
            setDrawCircles(false)
            lineWidth = 2f
            circleRadius = 3f
            fillAlpha = 65

            fillDrawable = drawableBlue
            //点击选中的竖线
            highLightColor = colorBlue
            highlightLineWidth = 1f
            setDrawHorizontalHighlightIndicator(true)
            setDrawIcons(true)

        }
        val set2 = LineDataSet(values2, "橙色报警车辆")
        set2.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = colorYellow
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            setCircleColor(colorRed)
            setDrawCircles(false)
            lineWidth = 2f
            circleRadius = 3f
            fillAlpha = 65
            fillDrawable = drawableYellow
            highLightColor = colorYellow
            highlightLineWidth = 1f
            setDrawHorizontalHighlightIndicator(false)
        }
        val set3 = LineDataSet(values3, "红色报警车辆")
        set3.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = colorRed
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            setCircleColor(colorRed)
            setDrawCircles(false)
            lineWidth = 2f
            circleRadius = 3f
            fillAlpha = 65
            fillDrawable = drawableRed
            highLightColor = colorRed
            highlightLineWidth = 1f
            setDrawHorizontalHighlightIndicator(false)
        }

        val data = LineData(set1, set2, set3)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(10f)
        data.setDrawValues(false)
        bind.lineChart.data = data


    }

    private fun initRadarChart() {

        bind.radarChart.apply {
            //自定义有圆点的渲染器
            renderer = RadarChartPointsRenderer(this,animator,viewPortHandler)

            setTouchEnabled(false)
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
            xAxis.textColor = Color.WHITE
            //自定义5个角的标签
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {

                    return when (value) {
                        0f -> "安全问题"
                        1f -> "违法"
                        2f -> "风险"
                        3f -> "动态报警"
                        4f -> "隐患"
                        else -> "--"
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
        setRadarData()
    }
    private fun createGradient(): Drawable {
        // 创建渐变的shape drawable
        val colors = intArrayOf(Color.parseColor("#FFF35531"), Color.parseColor("#14F4A77D"))
        return GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors)
    }
    private fun setRadarData() {
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
            fillDrawable = createGradient()
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