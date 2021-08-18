package com.smallcake.temp.utils

import android.content.Context
import android.graphics.Color
import androidx.core.content.ContextCompat
import com.smallcake.smallutils.DpUtils
import com.smallcake.temp.R
import com.smallcake.temp.weight.SelectBigPagerTitleView
import com.yx.jiading.utils.sizeNull
import net.lucode.hackware.magicindicator.FragmentContainerHelper
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView


/**
 * Date:2021/7/28 16:04
 * Author:SmallCake
 * Desc:快速的创建切换Tabs
 **/
object TabUtils {
    /**
     * 文字颜色过渡的Tab
     * @param magicIndicator MagicIndicator
     * @param tabs List<String>
     * @param cb Function1<Int, Unit>
     */
    fun createTabs(magicIndicator: MagicIndicator,tabs:List<String>,cb:(Int)->Unit){
        val helper =  FragmentContainerHelper(magicIndicator)
        val commonNavigator = CommonNavigator(magicIndicator.context)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return  tabs.sizeNull()
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val tv = ColorTransitionPagerTitleView(context)
                tv.apply {
                    textSize = 16f
                    normalColor = Color.GRAY
                    selectedColor = ContextCompat.getColor(context,R.color.text_blue)
                    text = tabs[index]
                    setOnClickListener {
                        helper.handlePageSelected(index)
                        cb.invoke(index)
                    }
                }

                return tv
            }

            override fun getIndicator(context: Context): IPagerIndicator? {
                val indicator = LinePagerIndicator(context)
                indicator.apply {
                    yOffset = 30f
                    lineWidth = 60f
                    setColors(ContextCompat.getColor(context, R.color.text_blue))
                    roundRadius = DpUtils.dp2pxFloat(2f)
                    mode = LinePagerIndicator.MODE_EXACTLY
                }

                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator

    }

    /**
     * 文字选中变大,文字颜色过渡
     * @param magicIndicator MagicIndicator
     * @param tabs List<String>
     * @param cb Function1<Int, Unit>
     */
    fun createSelectBigTabs(magicIndicator: MagicIndicator,tabs:List<String>,cb:(Int)->Unit){
        val helper =  FragmentContainerHelper(magicIndicator)
        val commonNavigator = CommonNavigator(magicIndicator.context)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return  tabs.sizeNull()
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val tv = SelectBigPagerTitleView(context)
                tv.apply {
                    normalColor = Color.GRAY
                    selectedColor = ContextCompat.getColor(context,R.color.text_blue)
                    text = tabs[index]
                    setOnClickListener {
                        helper.handlePageSelected(index)
                        cb.invoke(index)
                    }
                }

                return tv
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.apply {
                    yOffset = 30f
                    lineWidth = 60f
                    setColors(ContextCompat.getColor(context, R.color.text_blue))
                    roundRadius = DpUtils.dp2pxFloat(2f)
                    mode = LinePagerIndicator.MODE_EXACTLY
                }

                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator

    }

}