package com.yx.jiading.property.adapter

import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter


/**
 * Date:2021/7/9 15:10
 * Author:SmallCake
 * Desc:
 **/
class MenuViewPagerAdapter(val mList: ArrayList<RecyclerView>): PagerAdapter() {

    override fun getCount(): Int {
        return if (mList.isEmpty()) 0 else mList.size
    }

    override fun isViewFromObject( view: View,  o: Any): Boolean {
        return view === o
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(mList[position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        container.addView(mList[position])
        return mList[position]
    }
}