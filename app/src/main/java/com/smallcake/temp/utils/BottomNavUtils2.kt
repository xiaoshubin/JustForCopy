package com.smallcake.temp.utils

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.smallcake.smallutils.custom.NoScrollViewPager
import com.smallcake.temp.MyApplication
import com.smallcake.temp.R
import com.smallcake.temp.fragment.*

/**
 * Date: 2020/8/17
 * author: SmallCake
 * 地址导航绑定ViewPager，并装载Fragment
 * 使用：
BottomNavUtils.tabBindViewPager(this,bind.tabLayout,bind.viewPager)
BottomNavUtils.setListener(BottomNavListener {when(it){
0 -> showToast("选中了首页")
1 -> showToast("选中了列表")
2 -> showToast("选中了我的")
} })
 */
object BottomNavUtils2 {
    private val tabSelectedImgs = intArrayOf(
        R.mipmap.icon_selected_tab1,
        R.mipmap.icon_selected_tab2,
        R.mipmap.icon_selected_tab3,
        R.mipmap.icon_selected_tab4,
        R.mipmap.icon_selected_tab5
    )
    private val tabUnselectedImgs = intArrayOf(
        R.mipmap.icon_unselected_tab1,
        R.mipmap.icon_unselected_tab2,
        R.mipmap.icon_selected_tab3,
        R.mipmap.icon_unselected_tab4,
        R.mipmap.icon_unselected_tab5
    )
    private val tabNames = arrayOf("首页", "服务", "门禁","讯聊","我的")
    private val textColorNormal = ContextCompat.getColor(MyApplication.instance, R.color.text_gray)
    private val textColorSelect = ContextCompat.getColor(MyApplication.instance, R.color.text_red)
    private var listener : ((Int)->Unit)?=null
    fun tabBindViewPager(
        activity: AppCompatActivity,
        tabLayout: LinearLayout,
        viewPager: NoScrollViewPager,
        layoutCenter: LinearLayout
    ) {
        val fragments =
            arrayOf<Fragment>(
                HomeFragment(),
                HomeFragment(),
                HomeFragment(),
                HomeFragment(),
                MineFragment()
            )
        viewPager.adapter = object : FragmentStatePagerAdapter(activity.supportFragmentManager, 0) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(index: Int): Fragment {
                return fragments[index]
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabNames[position]
            }
        }
        viewPager.offscreenPageLimit = tabNames.size
        initTabBindViewPager(tabLayout, viewPager,layoutCenter)
    }

    private fun initTabBindViewPager(
        tabLayout: LinearLayout,
        viewPager: NoScrollViewPager,
        layoutCenter: LinearLayout

    ) {
        val childCount = tabLayout.childCount
        for (i in 0 until childCount) {
            val childAt = tabLayout.getChildAt(i) as LinearLayout
            childAt.setOnClickListener {
                toDefaultTab(i, tabLayout, viewPager,layoutCenter)
            }
        }
        layoutCenter.setOnClickListener{
            toDefaultTab(2, tabLayout, viewPager,layoutCenter)
        }
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int,positionOffset: Float,positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                toDefaultTab(position, tabLayout,layoutCenter=layoutCenter)
                listener?.invoke(position)
            }
        })
    }

    /**
     * Tab选择事件
     * 1.还原所有Tab为默认状态
     * 2.选中项加入白色背景和替换为选中图片
     * 3.联动ViewPager
     * @param selectPosition 选中项
     */
    private fun toDefaultTab(
        selectPosition: Int,
        tabLayout: LinearLayout,
        viewPager: NoScrollViewPager? = null,
        layoutCenter: LinearLayout
    ) {
        //1.还原所有Tab为默认状态
        for (i in 0 until tabLayout.childCount) {
            //恢复默认图标
            val childAt = tabLayout.getChildAt(i) as LinearLayout
            when (val view = childAt.getChildAt(0)) {
                is ImageView -> view.setImageResource(tabUnselectedImgs[i])
                is RelativeLayout -> (view.getChildAt(0) as ImageView).setImageResource(
                    tabUnselectedImgs[i]
                )
            }
            //恢复默认字体颜色
            val tvTab = childAt.getChildAt(1) as TextView
            tvTab.setTextColor(textColorNormal)
        }
        //中间还原成默认
        (layoutCenter.getChildAt(0) as ImageView).setImageResource(tabUnselectedImgs[2])
        (layoutCenter.getChildAt(1) as TextView).setTextColor(textColorNormal)

        //2.选中项加入白色背景和替换为选中图片
        val childAt = tabLayout.getChildAt(selectPosition) as LinearLayout
        when (val view = childAt.getChildAt(0)) {
            is ImageView -> view.setImageResource(tabSelectedImgs[selectPosition])
            is RelativeLayout -> (view.getChildAt(0) as ImageView).setImageResource(tabSelectedImgs[selectPosition])
        }
        if (selectPosition==2){
            tabSelectAnim(layoutCenter)
            (layoutCenter.getChildAt(0) as ImageView).setImageResource(tabSelectedImgs[2])
            //选中字体变黑色
            (layoutCenter.getChildAt(1) as TextView).setTextColor(textColorSelect)
        }else{
            tabSelectAnim(childAt)
            //选中字体变黑色
            (childAt.getChildAt(1) as TextView).setTextColor(textColorSelect)
        }


        //3.联动ViewPager
        viewPager?.currentItem = selectPosition
    }


    /**
     * tab被选中后的动画部分
     * @param view View?
     */
    private fun tabSelectAnim(view: View?) {
        val alpha = PropertyValuesHolder.ofFloat("alpha", 0.6f, 1f)
        val pvhX = PropertyValuesHolder.ofFloat("scaleX", 0.9f, 1.1f, 1f)
        val pvhY = PropertyValuesHolder.ofFloat("scaleY", 0.9f, 1.1f, 1f)
        val animator =
            ObjectAnimator.ofPropertyValuesHolder(view, alpha, pvhX, pvhY)
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = 300
        animator.start()
    }

     fun setListener(listener:((Int)->Unit)?){
        this.listener = listener
    }



}