package com.smallcake.temp.weight

import android.content.Context
import net.lucode.hackware.magicindicator.buildins.ArgbEvaluatorHolder
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

class SelectBigPagerTitleView(context: Context) : SimplePagerTitleView(context) {
    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(leavePercent, mSelectedColor, mNormalColor)
        setTextColor(color)
    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {
        val color = ArgbEvaluatorHolder.eval(enterPercent, mNormalColor, mSelectedColor)
        setTextColor(color)
    }
    override fun onSelected(index: Int, totalCount: Int) {
        textSize = 18f
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        textSize = 14f
    }
}