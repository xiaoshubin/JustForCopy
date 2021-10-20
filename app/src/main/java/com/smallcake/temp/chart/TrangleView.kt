package com.smallcake.temp.chart

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class TrangleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val p =  Paint()
        p.color = Color.parseColor("#FFFF2D55")
        //实例化路径
        val path =  Path()
        path.moveTo(0f, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat()/2f, height.toFloat())
        path.lineTo(0f, 0f)
        path.close()
        canvas.drawPath(path, p)
    }
}