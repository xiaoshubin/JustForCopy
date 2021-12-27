package com.smallcake.temp.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.WindowManager
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.smallcake.smallutils.Screen

class HorizontalScrollTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :  AppCompatTextView(context, attrs, defStyleAttr), View.OnClickListener {
    private var textLength = 0f // 文本长度
    private var viewWidth = 0f //文本控件的长度
    private var step = 0f // 文本的横坐标
    private var tvY = 0f // 文本的纵坐标
    private var isStarting = false // 是否开始滚动
    private var paint: Paint? = null
//    private var textStr:CharSequence?="" // 文本内容
    private var onSrollCompleteListener : (()->Unit)? = null//滚动结束监听

    init {
        initView()
        init()
    }



    fun setScrollText(txt: CharSequence) {
        text = txt
        init()
        startScroll()
    }


    fun setOnScrollCompleteListener(listener: ()->Unit) {
        onSrollCompleteListener = listener
    }

    private fun initView() {
        setOnClickListener(this)
    }

    fun init() {
        paint = getPaint()
        //设置滚动字体颜色
//        textStr = text
        textLength = paint?.measureText(text.toString())?:0f
        viewWidth = Screen.width.toFloat()
        tvY = textSize + paddingTop
    }

    //开启滚动
    fun startScroll() {
        isStarting = true
        invalidate()
    }

    //停止滚动
    fun stopScroll() {
        isStarting = false
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        canvas.drawText(text!!,0,text!!.length, -step, tvY, paint!!)
        if (!isStarting) {
            return
        }
        step += 2.0f // 2.0为文字的滚动速度
        //判断是否滚动结束
        if (step > textLength) {
            step = 0f
            onSrollCompleteListener?.invoke()
        }
        invalidate()
    }

    //控制点击停止或者继续运行
    override fun onClick(v: View) {
        if (isStarting) stopScroll() else startScroll()
    }

}