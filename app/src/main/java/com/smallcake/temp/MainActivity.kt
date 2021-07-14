package com.smallcake.temp

import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import coil.load
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.smallutils.CameraUtils
import com.smallcake.smallutils.MediaUtils
import com.smallcake.smallutils.ShapeCreator
import com.smallcake.smallutils.TimeUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.bean.UserBean
import com.smallcake.temp.databinding.ActivityMainBinding
import com.smallcake.temp.module.MobileViewModule
import com.smallcake.temp.utils.BottomNavUtils
import com.smallcake.temp.utils.ldd
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * @see com.smallcake.temp.fragment.PageFragment 页面
 * @see com.smallcake.temp.fragment.ListFragment 控件
 */
class MainActivity : BaseBindActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.hide()
        initView()
        onEvent()
    }

    private fun onEvent() {


    }

    private fun initView() {
        BottomNavUtils.tabBindViewPager(this,bind.tabLayout,bind.viewPager)
    }




}


