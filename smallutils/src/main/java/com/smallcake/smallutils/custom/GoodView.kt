package com.smallcake.smallutils.custom

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView

/**
 * 点赞效果:
 * +1 文字
 * GoodView txt = new GoodView(this);
 * txt.setText("+1");
 * txt.show(view);
 * ★ 图片效果
 * GoodView img = new GoodView(this);
 * img.setImage(R.mipmap.star);
 * img.show(view);
 */
class GoodView(context: Context?) : PopupWindow(context) {
    private var DISTANCE = 60 // 默认移动距离
    private var FROM_Y_DELTA = 0 // Y轴移动起始偏移量
    private var TO_Y_DELTA = DISTANCE // Y轴移动最终偏移量
    private var FROM_ALPHA = 1.0f // 起始时透明度
    private var TO_ALPHA = 0.0f // 结束时透明度
    private var DURATION = 1000 // 动画时长
    private var TEXT = "" // 默认文本
    private var TEXT_SIZE = 16 // 默认文本字体大小
    private var TEXT_COLOR = Color.BLACK // 默认文本字体颜色
    private var mText = TEXT
    private var mTextColor = TEXT_COLOR
    private var mTextSize = TEXT_SIZE
    private var mFromY = FROM_Y_DELTA
    private var mToY = TO_Y_DELTA
    private var mFromAlpha = FROM_ALPHA
    private var mToAlpha = TO_ALPHA
    private var mDuration = DURATION
    private var mDistance = DISTANCE
    private var mAnimationSet: AnimationSet? = null
    private var mChanged = false
    private var mContext: Context? = null
    private var mGood: TextView? = null
    private fun initView() {
        val layout = RelativeLayout(mContext)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_HORIZONTAL)
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        mGood = TextView(mContext)
        mGood!!.includeFontPadding = false
        mGood!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize.toFloat())
        mGood!!.setTextColor(mTextColor)
        mGood!!.text = mText
        mGood!!.layoutParams = params
        layout.addView(mGood)
        contentView = layout
        val w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        mGood!!.measure(w, h)
        width = mGood!!.measuredWidth
        height = mDistance + mGood!!.measuredHeight
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isFocusable = false
        isTouchable = false
        isOutsideTouchable = false
        mAnimationSet = createAnimation()
    }

    /**
     * 设置文本
     *
     * @param text
     */
    fun setText(text: String):GoodView {
        require(!TextUtils.isEmpty(text)) { "text cannot be null." }
        mText = text
        mGood!!.text = text
        mGood!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val w = mGood!!.paint.measureText(text).toInt()
        width = w
        height = mDistance + getTextViewHeight(mGood, w)
        return this
    }

    /**
     * 设置文本颜色
     *
     * @param color
     */
    fun setTextColor(color: Int):GoodView {
        mTextColor = color
        mGood!!.setTextColor(color)
        return this
    }

    /**
     * 设置文本大小
     *
     * @param textSize
     */
    fun setTextSize(textSize: Int):GoodView {
        mTextSize = textSize
        mGood!!.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
        return this
    }

    /**
     * 设置文本信息
     *
     * @param text
     * @param textColor
     * @param textSize
     */
    fun setTextInfo(text: String, textColor: Int, textSize: Int):GoodView {
        setTextColor(textColor)
        setTextSize(textSize)
        setText(text)
        return this
    }

    /**
     * 设置图片
     *
     * @param resId
     */
    fun setImage(resId: Int):GoodView {
        setImage(mContext!!.resources.getDrawable(resId))
        return this
    }

    /**
     * 设置图片
     *
     * @param drawable
     */
    fun setImage(drawable: Drawable?) {
        requireNotNull(drawable) { "drawable cannot be null." }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mGood!!.background = drawable
        } else {
            mGood!!.setBackgroundDrawable(drawable)
        }
        mGood!!.text = ""
        width = drawable.intrinsicWidth
        height = mDistance + drawable.intrinsicHeight
    }

    /**
     * 设置移动距离
     *
     * @param dis
     */
    fun setDistance(dis: Int) {
        mDistance = dis
        mToY = dis
        mChanged = true
        height = mDistance + mGood!!.measuredHeight
    }

    /**
     * 设置Y轴移动属性
     *
     * @param fromY
     * @param toY
     */
    fun setTranslateY(fromY: Int, toY: Int) {
        mFromY = fromY
        mToY = toY
        mChanged = true
    }

    /**
     * 设置透明度属性
     *
     * @param fromAlpha
     * @param toAlpha
     */
    fun setAlpha(fromAlpha: Float, toAlpha: Float) {
        mFromAlpha = fromAlpha
        mToAlpha = toAlpha
        mChanged = true
    }

    /**
     * 设置动画时长
     *
     * @param duration
     */
    fun setDuration(duration: Int) {
        mDuration = duration
        mChanged = true
    }

    /**
     * 重置属性
     */
    fun reset() {
        mText = TEXT
        mTextColor = TEXT_COLOR
        mTextSize = TEXT_SIZE
        mFromY = FROM_Y_DELTA
        mToY = TO_Y_DELTA
        mFromAlpha = FROM_ALPHA
        mToAlpha = TO_ALPHA
        mDuration = DURATION
        mDistance = DISTANCE
        mChanged = false
        mAnimationSet = createAnimation()
    }

    /**
     * 展示
     *
     * @param v
     */
    fun show(v: View) {
        if (!isShowing) {
            val offsetY = -v.height - height
            showAsDropDown(v, v.width / 2 - width / 2, offsetY)
            if (mAnimationSet == null || mChanged) {
                mAnimationSet = createAnimation()
                mChanged = false
            }
            mGood!!.startAnimation(mAnimationSet)
        }
    }

    fun show(v: View, launchX: Int) {
        if (!isShowing) {
            val offsetY = -v.height - height
            showAsDropDown(v, launchX, offsetY)
            if (mAnimationSet == null || mChanged) {
                mAnimationSet = createAnimation()
                mChanged = false
            }
            mGood!!.startAnimation(mAnimationSet)
        }
    }

    /**
     * 动画
     *
     * @return
     */
    private fun createAnimation(): AnimationSet {
        mAnimationSet = AnimationSet(true)
        val translateAnim = TranslateAnimation(0f, 0f, mFromY.toFloat(), (-mToY).toFloat())
        val alphaAnim = AlphaAnimation(mFromAlpha, mToAlpha)
        mAnimationSet!!.addAnimation(translateAnim)
        mAnimationSet!!.addAnimation(alphaAnim)
        mAnimationSet!!.duration = mDuration.toLong()
        mAnimationSet!!.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                if (isShowing) {
                    Handler().post { dismiss() }
                }
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        return mAnimationSet!!
    }

    companion object {
        private fun getTextViewHeight(textView: TextView?, width: Int): Int {
            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST)
            val heightMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            textView!!.measure(widthMeasureSpec, heightMeasureSpec)
            return textView.measuredHeight
        }
    }

    init {
        mContext = context
        initView()
    }
}