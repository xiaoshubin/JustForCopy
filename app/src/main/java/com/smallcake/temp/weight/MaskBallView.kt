package com.smallcake.temp.weight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import com.smallcake.smallutils.px


class MaskBallView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private val paintBg= Paint()
    private val paint= Paint()
    init {

        paintBg.apply {
            isAntiAlias=true
            color = Color.parseColor("#ff0000")
            style = Paint.Style.FILL
            maskFilter = BlurMaskFilter(80f, BlurMaskFilter.Blur.INNER)
        }


        paint.apply {
            isAntiAlias=true
            color = Color.parseColor("#ff0000")
            style = Paint.Style.FILL
            maskFilter = BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID)
        }
    }
    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
//        canvas.drawCircle(width/2f,height/2f,width/2f,paintBg)
//        canvas.drawCircle(width/2f,height/2f,width/2f-16f,paint)

        val colors = intArrayOf(Color.parseColor("#FFF38970"), Color.parseColor("#FFFFFFFF"))
        val drawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors)
        drawable.setSize(6,6)
        drawable.cornerRadius = 40F.px
        val save = canvas.save()
        drawable.setBounds(
            (0F).toInt(),
            (0F).toInt(),
            (width).toInt(),
            (height).toInt()
        )
        drawable.draw(canvas)
        canvas.restoreToCount(save)


    }
}