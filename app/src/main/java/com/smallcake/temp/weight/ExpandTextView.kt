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
 * 收起展开文本控件
 */
class ExpandTextView : AppCompatTextView {
    /**展开状态 true：展开，false：收起 */
    var expandState: Boolean = false

    /** 源文字内容 */
    var mText: String? = ""

    /** 最多展示的行数 */
    var maxLineCount = 3

    /** 省略文字 */
    var ellipsizeText = "..."

    /** 展开文案文字 */
    var expandText = "全文"

    /** 展开文案文字颜色 */
    var expandTextColor: Int = Color.parseColor("#1C7FFD")

    /** 收起文案文字 */
    var collapseText = "收起"

    /** 收起文案文字颜色 */
    var collapseTextColor: Int = Color.parseColor("#1C7FFD")

    /** 是否添加下划线 */
    var underlineEnable = false

    /** 是否仅仅点击后缀才伸缩 true:点击后缀才伸缩，点击全文收缩 */
    private var mSuffixTrigger = true

    constructor(context: Context) : super(context) {
        initTextView()
    }

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        initTextView()
    }

    constructor(context: Context, attributes: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributes,
        defStyleAttr
    ) {
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
                applyState()
            }
        }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (measuredWidth == 0) {
            return
        }
        if ((mText?.length ?: 0) == 0) {
            return
        }
        val canUseWidth = resources.displayMetrics.widthPixels - paddingLeft - paddingRight
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
        val result: SpannableString?
        // 总计行数
        var lineCount = sl.lineCount
        //总行数大于最大行数
        if (lineCount > maxLineCount) {
            if (expandState) {
                // 收起文案和源文字组成的新的文字
                val newEndLineText = mText + collapseText
                //收起文案和源文字组成的新的文字
                result = SpannableString(newEndLineText).apply {
                    //给收起设成监听
                    setSpan(
                        mClickSpanListener,
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
                lineText = lineText.replace("\r\n", "", true)
                lineText = lineText.replace("\n", "", true)
                val newEndLineText: String
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
                    newEndLineText =
                        (mText?.substring(0, start) ?: "") + lineText + ellipsizeText + expandText
                }

                //全部文字
                result = SpannableString(newEndLineText).apply {
                    //给查看全部设成监听
                    setSpan(
                        mClickSpanListener,
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
        for (i in 0 .. if (expandState)lineCount else lineCount-1) {
            lineHeight += (paint.fontMetrics.bottom - paint.fontMetrics.top) * lineSpacingMultiplier
        }
        lineHeight += paddingTop + paddingBottom
        setMeasuredDimension(measuredWidth, lineHeight.toInt() + 1)
        text = result
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (lineCount > maxLineCount) {
            applyState()
        }
    }

    private fun applyState() {
        if (TextUtils.isEmpty(mText)) return
        var note:String
        val suffix: String//文字后缀：如全文|收起
        if (expandState) {
            note = mText!!
            suffix = collapseText
        } else {
            note = mText+ellipsizeText
            suffix = expandText
            if (maxLineCount - 1 < 0) {
                throw RuntimeException("CollapsedLines must equal or greater than 1")
            }
            val lineEnd = layout.getLineEnd(maxLineCount - 1)


            val newEnd = lineEnd - suffix.length - 1

            var end = if (newEnd > 0) newEnd else lineEnd
            val paint = paint
            val maxWidth: Int =
                maxLineCount * (measuredWidth - paddingLeft - paddingRight)
            while (paint.measureText(note.substring(0, end) + suffix) > maxWidth) end--
            note = note.substring(0, end)
        }
        val str = SpannableString(note +suffix)
        if (mSuffixTrigger) {
            str.setSpan(
                mClickSpanListener,
                note.length,
                note.length + suffix.length,
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        str.setSpan(
            ForegroundColorSpan(if (expandState) collapseTextColor else expandTextColor),
            note.length,
            note.length + suffix.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        post { text = str }
    }

    private val mClickSpanListener: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            if (mSuffixTrigger) {
                expandState = !expandState
                applyState()
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
            ds.isUnderlineText = underlineEnable
        }
    }


}

