package com.smallcake.temp

import android.os.Bundle
import coil.load
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityMainBinding
import com.smallcake.temp.http.sub
import com.smallcake.temp.utils.BottomNavUtils
import com.smallcake.temp.utils.SelectImgUtils
import java.io.File


/**
 * @see com.smallcake.temp.fragment.PageFragment 页面
 * @see com.smallcake.temp.fragment.ListFragment 控件
 * @see com.smallcake.temp.fragment.MineFragment 我的
 */
class MainActivity : BaseBindActivity<ActivityMainBinding>() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.hide()
        initView()
        onEvent()
    }

    private fun onEvent() {
//        SelectImgUtils.selectFile(this){
//            bind.iv.load(File(it))
//        }
        dataProvider.mobile.mobileGet("18324138218").sub({

        })
    }

    private fun initView() {
        BottomNavUtils.tabBindViewPager(this,bind.tabLayout,bind.viewPager)


    }

}


