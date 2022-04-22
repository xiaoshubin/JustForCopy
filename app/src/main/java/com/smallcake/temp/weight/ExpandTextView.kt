package com.smallcake.temp.weight

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.Log
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

    private var mShouldInitLayout = true

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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (mShouldInitLayout&&lineCount > maxLineCount) {
            mShouldInitLayout=false
            applyState()
        }
    }
    private var index=0
    private fun applyState() {
        if (TextUtils.isEmpty(mText)) return
        var note:String = mText!!
        val suffix: String//文字后缀：如全文|收起
        if (expandState) {
            suffix = collapseText
        } else {
            suffix = expandText
            if (maxLineCount - 1 < 0) {
                throw RuntimeException("CollapsedLines must equal or greater than 1")
            }

            var lineEnd = layout.getLineEnd(maxLineCount -1)
            //第一次因为计算了追加后缀的长度，所以要减去
            if (index==0){
                lineEnd -= expandText.length
                index++
            }

            val newEnd = lineEnd - suffix.length - 1+ellipsizeText.length

            var end = lineEnd.coerceAtMost(newEnd)
            val paint = paint
            val maxWidth: Int =
                maxLineCount * (measuredWidth - paddingLeft - paddingRight)
            while (paint.measureText(note.substring(0, end) + suffix) > maxWidth) end--
            Log.e("TAG","lineEnd=$lineEnd newEnd=$newEnd end=$end")
            note = note.substring(0, end)
        }
        val str = SpannableString(note +(if (expandState)suffix else(ellipsizeText+suffix)))
        if (mSuffixTrigger) {
            str.setSpan(
                mClickSpanListener,
                if (expandState)note.length else note.length+ellipsizeText.length,
                if (expandState)(note.length + suffix.length)else (note.length + suffix.length+ellipsizeText.length),
                SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        str.setSpan(
            ForegroundColorSpan(if (expandState) collapseTextColor else expandTextColor),
            if (expandState)note.length else note.length+ellipsizeText.length,
            if (expandState)(note.length + suffix.length)else (note.length + suffix.length+ellipsizeText.length),
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

