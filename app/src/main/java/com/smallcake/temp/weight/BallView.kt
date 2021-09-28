package com.smallcake.temp.weight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt

class BallView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paintBg= Paint()
    private val paint= Paint()
    private val paintWhite= Paint()

    private val startAngle = 15f
    private val sweepAngle = 180-startAngle*2
    private val spaceSize = 8f
    private val spaceBottom = spaceSize-1f
    init {
        paintBg.color = Color.parseColor("#E50000")
        paintBg.isAntiAlias=true

        paint.color = Color.parseColor("#FF0000")
        paint.isAntiAlias=true

        paintWhite.color = Color.WHITE
        paintWhite.isAntiAlias=true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawCircle(width/2f,height/2f,width/2f,paintBg)
        canvas.drawCircle(width/2f,height/2f,width/2f-spaceSize,paintWhite)

        val ovalRect=RectF(spaceBottom,spaceBottom,width.toFloat()-spaceBottom,height.toFloat()-spaceBottom)
        canvas.drawArc(ovalRect,startAngle,sweepAngle,false,paint)

    }

     fun setBgColor(@ColorInt color:Int){
        paintBg.color = color
        invalidate()
    }
     fun setBottomColor(@ColorInt color:Int){
        paint.color = color
        invalidate()
    }
}