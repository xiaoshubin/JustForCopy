package com.smallcake.smallutils.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.Keep
import com.smallcake.smallutils.R

/**
 * 圆形进度条（可设置 线性渐变-背景色-进度条颜色-圆弧宽度）
 * 1.xml中配置
 <com.smallcake.smallutils.custom.CirclePercentView
    android:id="@+id/circle_percent_progress"
    android:layout_width="40dp"
    android:layout_height="40dp"
    android:layout_gravity="center_horizontal"
    app:circleBgColor="@color/lightgray"
    app:circleIsGradient="true"
    app:circleProgressColor="@color/red" />
 2.代码中设置进度
    progressView.setPercentage(50);//传入百分比的值
 3.带动画
animProgress(100,60f)

private fun animProgress(max: Int, current: Float) {
    val percentage = 100f * current / max
    val animator: ObjectAnimator = ObjectAnimator.ofFloat(progressView, "percentage", 0f, percentage)
    animator.duration = 2000
    animator.start()
}
 */
class CirclePercentView : View {
    private lateinit var mPaint: Paint
    private var progressPercent = 0f
    private var radiusWidth = 8//弧线半径 : 弧线线宽 (比例)
    private var radius = 0
    private var rectF: RectF? = null
    private var bgColor = 0
    private var progressColor = 0
    private var startColor = 0
    private var endColor = 0
    private var gradient: LinearGradient? = null
    private var isGradient = false

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CirclePercentView)
        typedArray.apply {
            bgColor = getColor(R.styleable.CirclePercentView_circleBgColor, Color.LTGRAY)
            progressColor =getColor(R.styleable.CirclePercentView_circleProgressColor, Color.RED)
            radius = getInt(R.styleable.CirclePercentView_circleRadious, radiusWidth)
            isGradient = getBoolean(R.styleable.CirclePercentView_circleIsGradient, false)
            startColor = getColor(R.styleable.CirclePercentView_circleStartColor, Color.BLUE)
            endColor = getColor(R.styleable.CirclePercentView_circleEndColor, Color.RED)
        }
        typedArray.recycle()
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth,measuredWidth) //自定义的View能够使用wrap_content或者是match_parent的属性
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        gradient =LinearGradient(width.toFloat(), 0F, width.toFloat(), height.toFloat(), startColor, endColor, Shader.TileMode.MIRROR)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 1、绘制背景灰色圆环
        val centerX = width / 2
        val strokeWidth = centerX / radius
        mPaint.shader = null //必须设置为null，否则背景也会加上渐变色
        mPaint.strokeWidth = strokeWidth.toFloat() //设置画笔的大小
        mPaint.color = bgColor
        canvas.drawCircle(
            centerX.toFloat(),
            centerX.toFloat(),
            (centerX - strokeWidth / 2).toFloat(),
            mPaint
        )
        // 2、绘制比例弧
        if (rectF == null) { //外切正方形
            rectF = RectF(
                (strokeWidth / 2).toFloat(),
                (strokeWidth / 2).toFloat(),
                (2 * centerX - strokeWidth / 2).toFloat(),
                (2 * centerX - strokeWidth / 2).toFloat()
            )
        }
        //3、是否绘制渐变色
        if (isGradient) {
            mPaint.shader = gradient //设置线性渐变
        } else {
            mPaint.color = progressColor
        }
        canvas.drawArc(rectF!!, -90f, 3.6f * progressPercent, false, mPaint) //画比例圆弧
    }

    private fun init() {
        mPaint = Paint()
        //画笔样式
        mPaint.style = Paint.Style.STROKE
        //设置笔刷的样式:圆形
        mPaint.strokeCap = Paint.Cap.ROUND
        //设置抗锯齿
        mPaint.isAntiAlias = true
    }

    @Keep
    fun setPercentage(percentage: Float) {
        progressPercent = percentage
        invalidate()
    }

    fun setRadius(radius: Int) {
        this.radius = radius
    }

    fun setBgColor(bgColor: Int) {
        this.bgColor = bgColor
    }

    fun setProgressColor(progressColor: Int) {
        this.progressColor = progressColor
    }

    fun setStartColor(startColor: Int) {
        this.startColor = startColor
    }

    fun setEndColor(endColor: Int) {
        this.endColor = endColor
    }

    fun setGradient(gradient: Boolean) {
        isGradient = gradient
    }
}