package com.smallcake.temp

import android.os.Bundle
import android.util.Log
import com.amap.api.mapcore.util.dp
import com.haibin.calendarview.YearRecyclerView
import com.smallcake.smallutils.TimeUtils
import com.smallcake.smallutils.dp
import com.smallcake.smallutils.px
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.smallutils.twoDecimals
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.bean.Song
import com.smallcake.temp.databinding.ActivityMainBinding
import com.smallcake.temp.utils.BottomNavUtils
import com.smallcake.temp.utils.L
import org.litepal.LitePal
import org.litepal.extension.find
import org.litepal.extension.findAll
import java.util.*


/**
 * @see com.smallcake.temp.fragment.PageFragment 页面
 * @see com.smallcake.temp.fragment.ListFragment 控件
 * @see com.smallcake.temp.fragment.MineFragment 我的
 */
class MainActivity : BaseBindActivity<ActivityMainBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.hide()
        initView()
        onEvent()
    }

    private fun onEvent() {
//        Song("儿歌",108).save()
//        Song("儿歌2",68).save()
//        val list = LitePal.findAll<Song>()
//        list.forEach{
//            L.e("song:$it")
//        }
        val ca = Calendar.getInstance()
        val day = ca.get(Calendar.DAY_OF_WEEK_IN_MONTH)
        Log.e("月","==${day}")
    }

    private fun initView() {
        BottomNavUtils.tabBindViewPager(this,bind.tabLayout,bind.viewPager)
    }





}


