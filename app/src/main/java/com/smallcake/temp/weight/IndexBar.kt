package com.smallcake.temp.weight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import com.smallcake.temp.R

/**
 * 动态添加数据的索引条
 */
class IndexBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    private lateinit var mPaint: Paint         //绘制字母画笔
    private lateinit var mCirclePaint: Paint   //绘制圆的画笔
    private var mHeight = 0                    //索引条的高度
    private var mCellWidth = 0                 //选中圆的宽度
    private var mCellHeight = 0f               //选中圆的高度
    private val normalColor: Int               //文字正常颜色
    private val selecColor: Int                //选择字母颜色
    private val selectCirlceColor: Int         //选择圆的颜色
    private var cx:Float = 0f                  //字母 x轴位置
    private val dimension: Float               //文字size大小
    private var beginY = 0f                    //字母的Y轴起点
    private var mRect: Rect? = null            //文本绘制区域
    private var pressed:Boolean? = false       //按下时的状态
    @Nullable
    private var letters: List<String>? = null  //字母集合

    @Nullable
    fun getLetters(): List<String>? {
        return letters
    }

    fun setLetters(@Nullable letters: List<String>?) {
        if (letters == null) {
            visibility = GONE
            return
        }
        this.letters = letters
        mHeight = measuredHeight - paddingTop - paddingBottom
        mCellWidth = measuredWidth
        mCellHeight = mHeight * 1.0f / 26
        beginY = (mHeight - mCellHeight * letters.size) * 0.5f
        invalidate()
    }


    private fun init() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.apply {
            color = normalColor
            textSize = dimension
            typeface = Typeface.DEFAULT
        }
        mCirclePaint  =  Paint(Paint.ANTI_ALIAS_FLAG)
        mRect = Rect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (letters==null)return
        for (i in letters!!.indices) {
            val text = letters!![i]
            val textWidth = mPaint.measureText(text)
            mPaint.getTextBounds(text, 0, text.length, mRect)
            val textHeight = mRect!!.height().toFloat()
            val x = mCellWidth * 0.5f - textWidth * 0.5f
            if (cx == 0f) cx = x
            val y = mCellHeight * 0.5f + textHeight * 0.5f + mCellHeight * i + beginY + paddingTop
            //重新设置颜色
            mPaint.color = if (i == mIndex) selecColor else normalColor
            mCirclePaint.strokeWidth = cx  //笔画宽度
            mCirclePaint.color = if (i == mIndex)selectCirlceColor else Color.TRANSPARENT
            canvas.drawCircle(width/2f, y -textHeight*0.5f , cx, mCirclePaint)
            canvas.drawText(text, x, y, mPaint)

        }

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHeight = measuredHeight - paddingTop - paddingBottom
        mCellWidth = measuredWidth
        mCellHeight = mHeight * 1.0f / 26
        if (letters != null) {
            beginY = (mHeight - mCellHeight * letters!!.size) * 0.5f
        }
    }

    private var mIndex = -1
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return super.dispatchTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val y: Float
        invalidate()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                refreshState(true)
                y = event.y
                checkIndex(y)
            }
            MotionEvent.ACTION_MOVE -> {
                y = event.y
                checkIndex(y)
            }
            MotionEvent.ACTION_UP -> {
                //如果需要抬起就释放选中选状态，放开下面的两行注释
//                refreshState(false)
//                mIndex = -1
                onLetterChangeListener?.onLetterNone()
            }
            else -> {
            }
        }
        return true
    }

    private fun checkIndex(y: Float) {
        if (y < beginY + paddingTop)return
        val currentIndex: Int = ((y - beginY - paddingTop) / mCellHeight).toInt()
        if (currentIndex != mIndex&&onLetterChangeListener != null) {
            if (letters != null && currentIndex < letters!!.size) {
                onLetterChangeListener!!.onLetterChange(currentIndex, letters!![currentIndex])
                mIndex = currentIndex
            }
        }
    }

    private fun refreshState(state: Boolean) {
        if (pressed != state) {
            pressed = state
            refreshDrawableState()
        }
    }

    public override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val states = super.onCreateDrawableState(extraSpace + 1)
        if (pressed!!) {
            mergeDrawableStates(states, STATE_FOCUSED)
        }
        return states
    }

    private fun sp2px(sp: Float): Int {
        val scaledDensity = context.resources.displayMetrics.scaledDensity
        return (sp * scaledDensity + 0.5f).toInt()
    }

    interface OnLetterChangeListener {
        fun onLetterChange(position: Int, letter: String?)
        fun onLetterNone()
    }

    var onLetterChangeListener: OnLetterChangeListener? = null



    fun setMyOnLetterChangeListener(onLetterChangeListener1: OnLetterChangeListener) {
        onLetterChangeListener = onLetterChangeListener1
    }

    companion object {
        private val STATE_FOCUSED = intArrayOf(android.R.attr.state_focused)
    }

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.IndexBar, defStyleAttr, 0
        )
        normalColor = a.getColor(R.styleable.IndexBar_normalColor, Color.GRAY)  //正常颜色
        selecColor = a.getColor(R.styleable.IndexBar_selectColor, Color.WHITE)  //选择颜色
        selectCirlceColor = a.getColor(R.styleable.IndexBar_selectCircleColor, Color.BLUE)  //选择圆圈颜色
        dimension = a.getDimensionPixelSize(R.styleable.IndexBar_indexSize, sp2px(14f)).toFloat() //尺寸
        a.recycle()
        init()
    }
}