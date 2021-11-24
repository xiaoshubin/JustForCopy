package com.smallcake.smallutils

import android.annotation.SuppressLint
import android.graphics.Paint
import android.text.TextUtils
import android.widget.TextView
import android.graphics.drawable.Drawable
import android.text.method.ScrollingMovementMethod

/**
 * 文本控件相关工具
 */
object TextViewUtils {
    /**
     * 设置文本滚动,跑马灯消息
     * @param tv TextView
     */
    fun setTextMarquee(tv: TextView) {
        tv.apply {
            ellipsize = TextUtils.TruncateAt.MARQUEE
            isSingleLine = true
            isSelected = true
            isFocusable = true
            isFocusableInTouchMode = true
        }
    }

    /**
     * 绘制文字左侧图片
     * @param tv TextView
     * @param icon Int
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun drawLeft(tv: TextView, icon: Int) {
        val img: Drawable = tv.resources.getDrawable(icon,null)
        img.setBounds(0, 0, img.minimumWidth, img.minimumHeight)
        tv.setCompoundDrawables(img, null, null, null)
    }

    /**
     * 加入删除线
     * @param tv TextView
     */
    private fun addDelLine(tv: TextView) {
        tv.paint.flags = Paint.STRIKE_THRU_TEXT_FLAG
    }
    /**
     * 加入下划线
     * @param tv TextView
     */
    private fun addBottomLine(tv: TextView) {
        tv.paint.flags = Paint.UNDERLINE_TEXT_FLAG
    }
    /**
     * 是否加粗
     * @param tv TextView
     */
    private fun setBold(tv: TextView,isBold:Boolean) {
        tv.paint.isFakeBoldText = isBold
    }

    /**
     * 设置垂直滚动
     * 首先：xml中 android:scrollbars="vertical"
     * @param tv TextView
     */
    private fun setVerScroll(tv: TextView) {
        tv.movementMethod = ScrollingMovementMethod.getInstance()
    }
}