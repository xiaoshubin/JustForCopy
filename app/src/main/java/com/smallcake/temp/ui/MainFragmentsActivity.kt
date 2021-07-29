package com.smallcake.temp.ui

import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.base.addFragment
import com.smallcake.temp.databinding.ActivityMainFragmentsBinding
import com.smallcake.temp.databinding.ActivityMainMiddleOutBinding
import com.smallcake.temp.fragment.HomeTabRecyclerFragment
import com.smallcake.temp.utils.BottomNavUtils2
import com.smallcake.temp.utils.TabUtils

/**
 * 展示各种首页布局方式的Fragments
 *
 */
class MainFragmentsActivity : BaseBindActivity<ActivityMainFragmentsBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.hide()
        bar.setImmersed(true)
        initView()
        onEvent()
    }

    private fun onEvent() {

        TabUtils.createTabs(bind.tabLayout, listOf("首页1")){

        }
    }

    private fun initView() {
        addFragment(HomeTabRecyclerFragment(), R.id.container)

    }




}


