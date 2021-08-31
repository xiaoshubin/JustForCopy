package com.smallcake.temp.ui

import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.smallcake.smallutils.RadomUtils
import com.smallcake.smallutils.TimeUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.adapter.CountDownAdapter
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.bean.CountDownBean
import com.smallcake.temp.databinding.ActivityCountDownListBinding

/**
 * 倒计时列表
 * 目前存在的问题：
 * 1.刷新数据出现并发异常：java.util.ConcurrentModificationException
 * 解决：在适配器中设置循环Handler
 * 2.多个倒计时多次滑动卡顿
 * 解决：在适配器中设置循环Handler，刷新Item子项的文本控件
 * 3.当倒计时结束时，刷新数据不生效
 * 解决：当前控件可能处于隐藏状态，对视图进行了判空： CountDownAdapter中的 if (view!=null)来开启倒计时
 * 应该写在判断外面
 */
class CountDownListActivity : BaseBindActivity<ActivityCountDownListBinding>() {

    private lateinit var mAdapter:CountDownAdapter
    private var page=1

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("倒计时列表")
        mAdapter = CountDownAdapter {
            page++
            loadData()
        }
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CountDownListActivity)
            adapter = mAdapter
        }
        loadData()
    }
        private fun loadData() {
            val list = listOf(
                CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)),
                CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(20)),
                CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)),
                CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)),
                CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)),
                CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)),
                CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)),
                CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)),
                CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)),
                CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)),
                )
            if (page==1)mAdapter.setList(list)
            else mAdapter.addData(list)

        }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.destoryTimer()
    }

}