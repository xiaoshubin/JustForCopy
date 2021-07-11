package com.smallcake.temp.weight

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.smallcake.temp.MyApplication
import com.smallcake.temp.R


/**
 * 动态添加数据的索引条
 */
class IndexBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    View(context, attrs, defStyleAttr) {
    private var mPaint: Paint? = null
    private var mHeight = 0
    var paddingTop:Int? = 0
    var paddingBottom:Int? = 0
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
        mHeight = measuredHeight - paddingTop!! - paddingBottom!!
        mCellWidth = measuredWidth
        mCellHeight = mHeight * 1.0f / 26
        beginY = (mHeight - mCellHeight * letters.size) * 0.5f
        invalidate()
    }

    @Nullable
    private var letters: List<String>? = null
    private var mCellWidth = 0
    private var mCellHeight = 0f
    private var mRect: Rect? = null
    private var pressed:Boolean? = false
    private val normalColor: Int //正常颜色
    private val selecColor: Int  //选择颜色
    var cx:Float = 0f  //获取第一个字母的X位置
    private val dimension: Float
    private var beginY = 0f
    private fun init() {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint!!.color = normalColor
        mPaint!!.textSize = dimension
        mPaint!!.typeface = Typeface.DEFAULT
        mRect = Rect()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (letters != null) {
            for (i in letters!!.indices) {
                val text = letters!![i]
                val textWidth = mPaint!!.measureText(text)
                mPaint!!.getTextBounds(text, 0, text.length, mRect)
                val textHeight = mRect!!.height().toFloat()
                val x = mCellWidth * 0.5f - textWidth * 0.5f
                if (cx == 0f) {
                    cx = x
                }
                val y = mCellHeight * 0.5f + textHeight * 0.5f + mCellHeight * i + beginY + paddingTop!!
                mPaint!!.color = if (mIndex == i) selecColor else normalColor
                //重新设置颜色
                    mPaint!!.color = if (i == mIndex) ContextCompat.getColor(
                        MyApplication.instance,
                        R.color.text_red
                    ) else ContextCompat.getColor(MyApplication.instance, R.color.text_gray)

                //
                val paint = Paint()
                paint.strokeWidth = cx  //笔画宽度
                if (i == mIndex)
                    paint.color = ContextCompat.getColor(MyApplication.instance, R.color.text_gray)
                else
                    paint.color = Color.TRANSPARENT
                canvas.drawCircle(cx * 1.55f, y - cx*0.6f , cx, paint)// radius:半径
                canvas.drawText(text, x, y, mPaint!!)


            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mHeight = measuredHeight - paddingTop!! - paddingBottom!!
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
                refreshState(false)
                mIndex = -1
                if (onLetterChangeListener != null) {
                    onLetterChangeListener!!.onLetterNone()
                }
            }
            else -> {
            }
        }
        return true
    }

    private fun checkIndex(y: Float) {
        val currentIndex: Int
        if (y < beginY + getPaddingTop()) {
            return
        }
        currentIndex = ((y - beginY - paddingTop!!) / mCellHeight).toInt()
        if (currentIndex != mIndex) {
            if (onLetterChangeListener != null) {
                if (letters != null && currentIndex < letters!!.size) {
                    onLetterChangeListener!!.onLetterChange(currentIndex, letters!![currentIndex])
                    mIndex = currentIndex
                    //                    Log.i(TAG, "checkIndex: "+letters.get(currentIndex));
                }
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

    fun sp2px(sp: Float): Int {
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

    override fun onFinishInflate() {
        super.onFinishInflate()
        paddingTop = getPaddingTop()
        paddingBottom = getPaddingBottom()
    }

    companion object {
        private val TAG = IndexBar::class.java.simpleName
        private val STATE_FOCUSED = intArrayOf(android.R.attr.state_focused)
    }

    init {
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.IndexBar, defStyleAttr, 0
        )
        normalColor = a.getColor(R.styleable.IndexBar_normalColor, Color.GRAY)  //正常颜色
        selecColor = a.getColor(R.styleable.IndexBar_selecColor, Color.BLUE)  //选择颜色
        dimension = a.getDimensionPixelSize(R.styleable.IndexBar_indexSize, sp2px(14f)).toFloat() //尺寸
        a.recycle()
        init()
    }
}