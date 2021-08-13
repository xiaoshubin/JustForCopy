package com.smallcake.temp.weight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.widget.AppCompatTextView

/**
 * 参考：https://github.com/timqi/CollapsibleTextView
 * 参考：https://github.com/Cmahjong/ExpandTextView
 *
 * 收起展开文本控件
 * @property expandState Boolean
 * @property mCallback Callback?
 * @property mText String?
 * @property maxLineCount Int
 * @property ellipsizeText String
 * @property expandText String
 * @property expandTextColor Int
 * @property collapseText String
 * @property collapseTextColor Int
 * @property collapseEnable Boolean
 * @property underlineEnable Boolean
 * @property marginStartPX Int
 * @property marginEndPX Int
 * @property clickEnable Boolean
 *
 */
class ExpandTextView : AppCompatTextView {
    /**展开状态 true：展开，false：收起 */
    var expandState: Boolean = false
    /** 源文字内容 */
    var mText: String? = ""
    /** 最多展示的行数 */
    var maxLineCount = 2
    /** 省略文字 */
    var ellipsizeText = "..."
    /** 展开文案文字 */
    var expandText = "查看全文"
    /** 展开文案文字颜色 */
    var expandTextColor: Int = Color.parseColor("#1C7FFD")
    /** 收起文案文字 */
    var collapseText = "收起来"
    /** 收起文案文字颜色 */
    var collapseTextColor: Int = Color.parseColor("#999999")
    /**是否支持收起功能*/
    var collapseEnable = true
    /** 是否添加下划线 */
    var underlineEnable = false
    var marginStartPX = 0//marginStart
    var marginEndPX = 0//marginStart
    /** 是否应该初始化布局 */
    private var mShouldInitLayout = true
    private var mSuffixTrigger = true//是否只有点击后缀才伸缩
    private var mCustomClickListener: OnClickListener? = null

    override fun setOnClickListener(l: OnClickListener?) {
        mCustomClickListener = l
    }
    constructor(context: Context) : super(context) {
        initTextView()
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        initTextView()
    }

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(context, attributes, defStyleAttr) {
        initTextView()
    }

    private fun initTextView() {
        mText = if (text == null) null else text.toString()
        movementMethod = LinkMovementMethod.getInstance()
        super.setOnClickListener(mClickListener)
    }

    private val mClickListener =
        OnClickListener { v ->
            if (!mSuffixTrigger) {
                expandState = !expandState
                applyState(expandState)
            }
            mCustomClickListener?.onClick(v)

        }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredWidth == 0) {
            return
        }
        if ((mText?.length?:0) == 0) {
            return
        }
        val canUseWidth =
            resources.displayMetrics.widthPixels - paddingLeft - paddingRight - marginStartPX - marginEndPX
        //StaticLayout对象
        val sl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(
                mText ?: "",
                0,
                mText?.length ?: 0,
                paint,
                canUseWidth
            ).apply {
                setAlignment(Layout.Alignment.ALIGN_CENTER)
            }.build()
        } else {
            StaticLayout(
                mText,
                paint,
                canUseWidth,
                Layout.Alignment.ALIGN_CENTER,
                1f,
                0f,
                true
            )
        }
        var result:SpannableString?=null
        // 总计行数
        var lineCount = sl.lineCount
        //总行数大于最大行数
        if (lineCount > maxLineCount) {
            if (expandState) {
                //是否支持收起功能
                if (collapseEnable) {
                    // 收起文案和源文字组成的新的文字
                    val newEndLineText = mText + collapseText
                    //收起文案和源文字组成的新的文字
                    result = SpannableString(newEndLineText).apply {
                            //给收起设成监听
                                setSpan(object : ClickableSpan() {
                                        override fun onClick(widget: View) {
                                            setChanged(false)
                                        }
                                        override fun updateDrawState(ds: TextPaint) {
                                            super.updateDrawState(ds)
                                            ds.isUnderlineText = false
                                        }

                                    },
                                    newEndLineText.length - collapseText.length,
                                    newEndLineText.length,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )

                            //给收起设成蓝色
                            setSpan(
                                ForegroundColorSpan(collapseTextColor),
                                newEndLineText.length - collapseText.length,
                                newEndLineText.length,
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                }
            } else {

                lineCount = maxLineCount
                // 省略文字和展开文案的宽度
                val dotWidth = paint.measureText(ellipsizeText + expandText)
                // 找出显示最后一行的文字
                val start = sl.getLineStart(lineCount - 1)
                val end = sl.getLineEnd(lineCount - 1)
                var lineText = mText?.substring(start, end) ?: ""
                // 将第最后一行最后的文字替换为 ellipsizeText和expandText
                //如果有换行符的话，会出现收齐状态显示未占满全文状态，那么先判断收齐状态的情况下是否有换行符，然后文字内容加上省略符号是否超过可用宽度
                lineText = lineText.replace("\r\n", "",true)
                lineText = lineText.replace("\n", "",true)
                var newEndLineText: String
                if (paint.measureText(lineText + ellipsizeText + expandText) > canUseWidth) {
                    var endIndex = 0
                    for (i in lineText.length - 1 downTo 0) {
                        val str = lineText.substring(i, lineText.length)
                        // 找出文字宽度大于 ellipsizeText 的字符
                        if (paint.measureText(str) >= dotWidth) {
                            endIndex = i
                            break
                        }
                    }
                    // 新的文字
                    newEndLineText = (mText?.substring(0, start) ?: "") + lineText.substring(
                        0,
                        endIndex
                    ) + ellipsizeText + expandText
                } else {
                    newEndLineText = (mText?.substring(0, start) ?: "") +lineText + ellipsizeText + expandText
                }

                //全部文字
                result = SpannableString(newEndLineText).apply {
                        //给查看全部设成监听
                        setSpan(object : ClickableSpan() {
                                override fun onClick(widget: View) {
                                    setChanged(true)
                                }
                                override fun updateDrawState(ds: TextPaint) {
                                    super.updateDrawState(ds)
                                    ds.isUnderlineText = false
                                }
                            },
                            newEndLineText.length - expandText.length,
                            newEndLineText.length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                    //给查看全部设成颜色
                    setSpan(
                        ForegroundColorSpan(expandTextColor),
                        newEndLineText.length - expandText.length,
                        newEndLineText.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

            }
        } else {
            result = SpannableString(mText)
        }
        // 重新计算高度
        var lineHeight = 0f
        for (i in 0 until lineCount) {
            lineHeight +=(paint.fontMetrics.bottom - paint.fontMetrics.top)* lineSpacingMultiplier
        }
        lineHeight += paddingTop + paddingBottom
        setMeasuredDimension(measuredWidth, lineHeight.toInt()+1)
        text = result
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mShouldInitLayout && lineCount > maxLineCount) {
            mShouldInitLayout = false
            applyState(expandState)
        }
    }
    private fun applyState(expanded: Boolean) {
        if (TextUtils.isEmpty(mText)) return
        var note = mText!!
        val suffix: String
        if (expanded) {
            suffix = collapseText
        } else {
            if (maxLineCount - 1 < 0) {
                throw RuntimeException("CollapsedLines must equal or greater than 1")
            }
            val lineEnd = layout.getLineEnd(maxLineCount - 1)
            suffix = expandText
            val newEnd = lineEnd - suffix.length - 1
            var end = if (newEnd > 0) newEnd else lineEnd
            val paint = paint
            val maxWidth: Int = maxLineCount * (measuredWidth - paddingLeft - paddingRight)
            while (paint.measureText(note.substring(0, end) + suffix) > maxWidth) end--
            note = note.substring(0, end)
        }
        val str = SpannableString(note + suffix)
        if (mSuffixTrigger) {
            str.setSpan(
                mClickSpanListener,
                note.length,
                note.length + suffix.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            str.setSpan(
                ForegroundColorSpan(if (expanded)collapseTextColor else expandTextColor),
                note.length,
                note.length + suffix.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        post { text = str }
    }

    private val mClickSpanListener: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            if (mSuffixTrigger) {
                expandState = !expandState
                applyState(expandState)
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = false
        }
    }

    /**
     * 设置要显示的文字以及状态
     * @param text
     * @param expanded true：展开，false：收起
     * @param callback
     */
    fun setText(text: String?, expanded: Boolean) {
        mText = text
        expandState = expanded
        invalidate()
    }

    /**
     * 展开收起状态变化
     * @param expanded
     */
    fun setChanged(expanded: Boolean) {
        expandState = expanded
        requestLayout()
    }
}

