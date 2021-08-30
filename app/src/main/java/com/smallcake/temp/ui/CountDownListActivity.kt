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
 * 2.多个倒计时多次滑动卡顿
 */
class CountDownListActivity : BaseBindActivity<ActivityCountDownListBinding>() {

    private val mAdapter = CountDownAdapter()
    private var allList = ArrayList<CountDownBean>()
    private var page = 1

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("倒计时列表")
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CountDownListActivity)
            adapter = mAdapter
        }

        loadData()
    }
        private fun loadData() {

            mAdapter.addData(CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(20)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)))
            mAdapter.addData(CountDownBean(TimeUtils.currentTime-RadomUtils.getInt(1000)))
            allList.clear()
            mAdapter.data.forEach{
                allList.add(CountDownBean(it.time))
            }
            mHandler.sendEmptyMessageDelayed(0,1000)
        }

    private val mHandler = Handler{
        allList.forEachIndexed{index,item->
            //只有当时间大于当前时间才需要倒计时
            if (item.timeX>0){
                val timeStr = TimeUtils.timeToDhms(item.timeX)
                val view = mAdapter.getViewByPosition(index, R.id.tv_deadline)
                if (view!=null)(view as TextView?)?.text ="截止时间：$timeStr"
                it.target.sendEmptyMessageDelayed(0,1000)
            }
            if (item.timeX==0){
                it.target.removeMessages(0)
                page=1
                loadData()
            }
        }

        false
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeMessages(0)
    }
}