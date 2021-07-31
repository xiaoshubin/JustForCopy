package com.smallcake.temp.weight

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.TextUtils
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.MonthView

/**
 * Date: 2020/8/21
 * author: SmallCake
 */
class MeiZuMonthView(context: Context) : MonthView(context) {
    /**
     * 自定义魅族标记的文本画笔
     */
    private val mTextPaint = Paint()
    private val mTextWhitePaint = Paint()

    /**
     * 自定义魅族标记的圆形背景
     */
    private val mSchemeBasicPaint = Paint()
    private val mRadio: Float
    private val mPadding: Int
    private val mSchemeBaseLine: Float

    /**
     * 绘制选中的日子
     *
     * @param canvas    canvas
     * @param calendar  日历日历calendar
     * @param x         日历Card x起点坐标
     * @param y         日历Card y起点坐标
     * @param hasScheme hasScheme 非标记的日期
     * @return true 则绘制onDrawScheme，因为这里背景色不是是互斥的
     */
    override fun onDrawSelected(canvas: Canvas,calendar: Calendar,x: Int,y: Int,hasScheme: Boolean): Boolean {
        mSelectedPaint.color = Color.parseColor("#43CFB6")
        canvas.drawCircle((x + mItemWidth / 2).toFloat(),(y + mItemHeight / 2).toFloat(),(mItemHeight / 2).toFloat(),mSelectedPaint)
        return true
    }

    /**
     * 绘制标记的事件日子
     *
     * @param canvas   canvas
     * @param calendar 日历calendar
     * @param x        日历Card x起点坐标
     * @param y        日历Card y起点坐标
     */
    override fun onDrawScheme(canvas: Canvas, calendar: Calendar, x: Int, y: Int) {
        mSchemeBasicPaint.color = calendar.schemeColor
    }


    /**
     * 绘制文本
     *
     * @param canvas     canvas
     * @param calendar   日历calendar
     * @param x          日历Card x起点坐标
     * @param y          日历Card y起点坐标
     * @param hasScheme  是否是标记的日期
     * @param isSelected 是否选中
     */
    override fun onDrawText(canvas: Canvas,calendar: Calendar,x: Int, y: Int,hasScheme: Boolean,isSelected: Boolean) {
        val cx = x + mItemWidth / 2
        val top = y - mItemHeight / 6
        val isInRange = isInRange(calendar) //日期是否在范围内，超出范围的可以置灰
        //
        if (isSelected) {
            canvas.drawText(calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,mSelectTextPaint)
            val scheme = calendar.scheme
            if (!TextUtils.isEmpty(scheme)) canvas.drawText(scheme,cx.toFloat(),mTextBaseLine + y + mItemHeight / 10,mSchemeBasicPaint)
        } else if (hasScheme) {
            canvas.drawText( calendar.day.toString(),cx.toFloat(),mTextBaseLine + top,if (calendar.isCurrentMonth) mCurMonthTextPaint else mOtherMonthTextPaint)
            val scheme = calendar.scheme
            if (!TextUtils.isEmpty(scheme)) canvas.drawText(scheme, cx.toFloat(),mTextBaseLine + y + mItemHeight / 10,mSchemeBasicPaint)
        } else {
            canvas.drawText(
                calendar.day.toString(), cx.toFloat(), mTextBaseLine + top,
                if (calendar.isCurrentDay) mCurMonthTextPaint else if (calendar.isCurrentMonth && isInRange) mCurMonthTextPaint else mOtherMonthTextPaint
            )
        }
    }

    companion object {
        /**
         * dp转px
         * @param context context
         * @param dpValue dp
         * @return px
         */
        private fun dipToPx(context: Context, dpValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }

    init {
        //被选择的日期背景色

        mSelectedPaint.style = Paint.Style.FILL
        //文字画笔
        mTextPaint.textSize = dipToPx(context, 8f).toFloat()
        mTextPaint.color = Color.RED
        mTextPaint.isAntiAlias = true
        mTextPaint.isFakeBoldText = true
        //计划画笔
        mSchemeBasicPaint.isAntiAlias = true
        mSchemeBasicPaint.style = Paint.Style.FILL
        mSchemeBasicPaint.textAlign = Paint.Align.CENTER
        mSchemeBasicPaint.isFakeBoldText = true
        mSchemeBasicPaint.textSize = dipToPx(context, 10f).toFloat()
        //选中后的白色
        mTextWhitePaint.textSize = dipToPx(context, 10f).toFloat()
        mTextWhitePaint.color = Color.parseColor("#08B596")
        mTextWhitePaint.style = Paint.Style.FILL
        mTextWhitePaint.textAlign = Paint.Align.CENTER
        mTextWhitePaint.isFakeBoldText = true
        mRadio = dipToPx(getContext(), 7f).toFloat()
        mPadding = dipToPx(getContext(), 4f)
        val metrics = mSchemeBasicPaint.fontMetrics
        mSchemeBaseLine = mRadio - metrics.descent + (metrics.bottom - metrics.top) / 2 + dipToPx(getContext(),1f)

//        //兼容硬件加速无效的代码
//        setLayerType(View.LAYER_TYPE_SOFTWARE, mSchemeBasicPaint);
//        //4.0以上硬件加速会导致无效
//        mSchemeBasicPaint.setMaskFilter(new BlurMaskFilter(25, BlurMaskFilter.Blur.SOLID));
    }
}