package com.smallcake.temp.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

/**
 * 参考：
 * https://blog.csdn.net/weixin_44819566/article/details/127072971
 */
class LdChartView(context: Context?, attrs: AttributeSet?): View(context, attrs)  {
    private val paint= Paint()
    companion object{
        //几边形
        const val COUNT=5
    }
    private val centerPoint by lazy {
        PointF(width/2f,height/2f)
    }

    init {
        paint.color = Color.parseColor("#FF0000")
        paint.isAntiAlias=true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //中心x,y坐标
        val cx = centerPoint.x
        val cy = centerPoint.y
        val radios = if (width>height)height/2f else width/2f
        //辅助圆
        canvas.drawCircle(cx,cy,radios,paint)
        //多边形每个间隔角度
        val eachAngle = 360/ COUNT
        (0 until COUNT).forEach{
            val angle = it*eachAngle.toDouble()
            val x = (radios* cos(Math.toRadians(angle))+cx).toFloat()
            val y = (radios* sin(Math.toRadians(angle))+cy).toFloat()
            canvas.drawCircle(x,y,10f,paint)
        }

    }

}