package com.smallcake.temp.weight

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.RelativeLayout
import androidx.core.widget.NestedScrollView

/**
 * Describe:ScrollView滑动冲突
 */
class NotTouchScrollView : RelativeLayout {
    private var scrollView: NestedScrollView? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    fun setScrollView(scrollView: NestedScrollView?) {
        this.scrollView = scrollView
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (scrollView == null) return false
        if (ev.action == MotionEvent.ACTION_UP) {
            scrollView!!.requestDisallowInterceptTouchEvent(false)
        } else {
            scrollView!!.requestDisallowInterceptTouchEvent(true)
        }
        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return true
    }
}