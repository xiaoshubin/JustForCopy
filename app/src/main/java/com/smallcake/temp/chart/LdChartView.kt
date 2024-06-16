package com.smallcake.temp.chart

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.smallcake.smallutils.px
import kotlin.math.cos
import kotlin.math.sin

/**
 * 参考：
 * https://blog.csdn.net/weixin_44819566/article/details/127072971
 */
class LdChartView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paintBg = Paint()
    private val paintValue = Paint()
    private val paintValueFill = Paint()//填充数值画笔
    private val paintTxt = Paint()
    private val padding = 12f.px

    companion object {
        //几边形
        var COUNT = 5

        // 有几条网格
        const val NUMBER = 4
        var datas: List<LdTxtValue>? = null

    }

    private val centerPoint by lazy {
        PointF(width / 2f, height / 2f)
    }

    init {
        COUNT
        paintBg.apply {
            color = Color.LTGRAY
            strokeWidth = 2F
            isAntiAlias = true
            style = Paint.Style.STROKE
        }
        paintValue.apply {
            color = Color.parseColor("#E45E21")
            strokeWidth = 2F
            isAntiAlias = true
            style = Paint.Style.STROKE

        }
        paintValueFill.apply {
            color = Color.parseColor("#33E45E21")
            isAntiAlias = true
            style = Paint.Style.FILL

        }
        paintTxt.apply {
            color = Color.BLACK
            textSize = 12f.px
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }

    }

    private val path = Path()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //中心x,y坐标
        val cx = centerPoint.x
        val cy = centerPoint.y
        val maxRadius = if (width > height) height / 2f - padding else width / 2f - padding
        COUNT = datas?.size ?: 0
        //每一条边的间隔距离
        val INTERVAL = maxRadius / COUNT

        //多边形每个间隔角度
        val eachAngle = 360 / COUNT
        //1.绘制背景
        (0 until NUMBER).forEach { element ->
            (0 until COUNT).forEach { count ->
                val radius = element * INTERVAL + INTERVAL
                val angle = count * eachAngle.toDouble() - 90//让第一个点从顶部开始绘制，-90
                val x = (radius * cos(Math.toRadians(angle)) + cx).toFloat()
                val y = (radius * sin(Math.toRadians(angle)) + cy).toFloat()
                if (count == 0) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                //绘制链接线
                if (element == NUMBER - 1) {
                    canvas.drawLine(cx, cy, x, y, paintBg)
                }
            }
            path.close()
            canvas.drawPath(path, paintBg)
            path.reset()
        }

        //2.绘制顶点文字
        (0 until COUNT).forEach { i ->
            val angle = i * eachAngle.toDouble() - 90//让第一个点从顶部开始绘制，-90
            val x = (maxRadius * cos(Math.toRadians(angle)) + cx).toFloat()
            val y = (maxRadius * sin(Math.toRadians(angle)) + cy).toFloat()
            val item = datas?.get(i)
            //绘制链接线,顶点文字
            paintTxt.color = Color.BLACK
            canvas.drawText(item?.value.toString(), x, y, paintTxt)//黑色数值
            paintTxt.color = Color.GRAY
            canvas.drawText(item?.name ?: "", x, y + 14.px, paintTxt)//灰色描述
        }
        //3.绘制各项数据线段
        (0 until COUNT).forEach { i ->
            val angle = i * eachAngle.toDouble() - 90//让第一个点从顶部开始绘制，-90
            val scaleL = (datas?.get(i)?.value ?: 0f) / 100f//长度比例
            val radiusValue = (maxRadius-INTERVAL) * scaleL
            val xValue = (radiusValue * cos(Math.toRadians(angle)) + cx).toFloat()
            val yValue = (radiusValue * sin(Math.toRadians(angle)) + cy).toFloat()
            if (i == 0) {
                path.moveTo(xValue, yValue)
            } else {
                path.lineTo(xValue, yValue)
            }
        }
        path.close()
        canvas.drawPath(path, paintValue)
        path.reset()
        //4.绘制各项数据线段填充
        (0 until COUNT).forEach { i ->
            val angle = i * eachAngle.toDouble() - 90//让第一个点从顶部开始绘制，-90
            val scaleL = (datas?.get(i)?.value ?: 0f) / 100f//长度比例
            val radiusValue = (maxRadius-INTERVAL) * scaleL-2F
            val xValue = (radiusValue * cos(Math.toRadians(angle)) + cx).toFloat()
            val yValue = (radiusValue * sin(Math.toRadians(angle)) + cy).toFloat()
            if (i == 0) {
                path.moveTo(xValue, yValue)
            } else {
                path.lineTo(xValue, yValue)
            }
        }
        path.close()
        canvas.drawPath(path, paintValueFill)
        path.reset()

    }

    fun setData(list: List<LdTxtValue>) {
        datas = list
        COUNT = list.size
        postInvalidate()
    }

    /**
     * @property name String 名称
     * @property value Float 数值
     */
    data class LdTxtValue(val name: String, val value: Float)

}