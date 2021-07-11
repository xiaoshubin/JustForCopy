package com.smallcake.temp.ui

import android.os.Bundle
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityMainMiddleOutBinding
import com.smallcake.temp.utils.BottomNavUtils2

/**
 * 中间菜单突出
 */
class MainMiddleOutActivity : BaseBindActivity<ActivityMainMiddleOutBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.hide()
        bar.setImmersed(true)
        initView()
        onEvent()
    }

    private fun onEvent() {


    }

    private fun initView() {
        BottomNavUtils2.tabBindViewPager(this,bind.tabLayout,bind.viewPager,bind.layoutCenter)


    }




}


