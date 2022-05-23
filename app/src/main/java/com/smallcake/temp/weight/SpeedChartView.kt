package com.smallcake.temp.weight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.smallcake.smallutils.px

/**
 * 一个速度表的自定义View
 * 默认是底部缺口120，也就是240度的速度表
 *
 * 1.xml中
<com.demo.smallutils.SpeedChartView
android:id="@+id/speed_chart"
android:layout_width="160dp"
android:layout_height="160dp"/>
2.设置进度
speedChartView.setProgress(80f)
 */
class SpeedChartView : View {

    private lateinit var paint: Paint
    private var progress = 0f                              //圆弧进度比例0-100
    private val gapAngle = 120                             //仪表盘 缺口 弧度
    private val arcWith  = 8f.px                           //圆弧粗细
    private val startAngle = (90 + gapAngle/2).toFloat()   //圆弧开始角度
    private val sweepAngle = (360 - gapAngle).toFloat()    //圆弧总跨度

    private val kdNum = 53                                 //刻度个数
    private val divAngle = (sweepAngle) / kdNum            //每个刻度线之间的夹角
    private var dashMargin = 16.px                         //刻度边距
    private var dashSize = 1f.px                           //刻度粗
    private var dashLong = 1.5f.px                         //刻度长

    private val shadowSize = 2f.px//阴影大小

    constructor(context: Context?) : super(context) {init()}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {init() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }
    private fun init() {
        paint = Paint()
        paint.apply {
            color = Color.parseColor("#F2F2F2")
            //画笔样式
            style = Paint.Style.STROKE
            //设置笔刷的样式:圆形
            strokeCap = Paint.Cap.ROUND
            //设置抗锯齿
            isAntiAlias = true
            strokeWidth = arcWith
        }
    }

    /**
     * 设置当前比例
     * @param num Float
     */
    internal fun setProgress(num:Float){
        progress = num
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        //1.画弧背景
        paint.clearShadowLayer()
        paint.strokeWidth = arcWith
        paint.color= Color.parseColor("#F2F2F2")
        val rect = RectF(arcWith/2+shadowSize, arcWith/2+shadowSize, width.toFloat()-arcWith/2-shadowSize, height.toFloat()-arcWith/2-shadowSize)
        canvas.drawArc(rect,startAngle, sweepAngle, false, paint)
        //2.画阴影弧进度
        paint.setShadowLayer(arcWith/4,0f,0f,Color.parseColor("#2962B5"))
        val currentSweepAngle = sweepAngle*(progress/100f)
        paint.color= Color.parseColor("#2962B5")
        canvas.drawArc(rect,startAngle, currentSweepAngle, false, paint)
        //3.画刻度
        paint.setShadowLayer(dashSize/4,0f,0f,Color.parseColor("#2962B5"))
        paint.strokeWidth = dashSize
        //移动和旋转画布
        canvas.translate(width/2f,height/2f)
        canvas.rotate(90+gapAngle/2f-divAngle/2)
        for (i in 0..kdNum+1){
            canvas.drawLine(width.toFloat()/2-dashLong-dashMargin,0f,width.toFloat()/2-dashMargin,0f,paint)
            canvas.rotate(divAngle)
        }
    }
}