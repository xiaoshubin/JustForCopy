package com.smallcake.temp.utils

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.tabs.TabLayout
import com.smallcake.smallutils.px
import com.smallcake.temp.R
import com.smallcake.temp.weight.SelectBigPagerTitleView
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
                    selectedColor = ContextCompat.getColor(context,R.color.tv_blue)
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
                    setColors(ContextCompat.getColor(context, R.color.tv_blue))
                    roundRadius = 2f.px
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
                    selectedColor = ContextCompat.getColor(context,R.color.tv_blue)
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
                    setColors(ContextCompat.getColor(context, R.color.tv_blue))
                    roundRadius = 2f.px
                    mode = LinePagerIndicator.MODE_EXACTLY
                }

                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator

    }

    /**
     * 动态创建Tab
     * TabUtils.initTabCreate(bind.tabLayout,listOf("服务内容","费用说明","用户评价")){}
     */
    fun initTabCreate(tabLayout: TabLayout, list:List<String>, cb:(Int)->Unit){
        tabLayout.removeAllTabs()
        list.forEachIndexed {i,it->
            val tab = tabLayout.newTab()
            val view  = LayoutInflater.from(tabLayout.context).inflate(R.layout.tab_text,null)
            view.findViewById<TextView>(R.id.tv).text = it
            tab.customView = view
            tabLayout.addTab(tab)
            val iv = tab.customView?.findViewById<ImageView>(R.id.iv)
            iv?.visibility = if (i==0) View.VISIBLE else View.INVISIBLE
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                cb(tab.position)
                val iv = tab.customView?.findViewById<ImageView>(R.id.iv)
                iv?.visibility = View.VISIBLE
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
                val iv = tab.customView?.findViewById<ImageView>(R.id.iv)
                iv?.visibility = View.INVISIBLE
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

}