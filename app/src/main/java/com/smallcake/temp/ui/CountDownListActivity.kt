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
import com.yx.jiading.utils.sizeNull

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
 * 4.刷新数据，当条目减少时闪退
 * 解决：在适配器中刷新数据时，重新设置标志位
 */
class CountDownListActivity : BaseBindActivity<ActivityCountDownListBinding>() {

    private val mAdapter=CountDownAdapter()
    private var page=1

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("倒计时列表")

        bind.recyclerView.apply{
            layoutManager = LinearLayoutManager(this@CountDownListActivity)
            adapter = mAdapter
        }
        mAdapter.loadMoreModule.setOnLoadMoreListener{
            page++
            loadData()
        }
        mAdapter.setOnTimeOverListener {
            page=1
            loadData()
        }


        bind.refreshLayout.setOnRefreshListener{
            page=1
            loadData()
        }
        loadData()
    }
        private fun loadData() {
            bind.refreshLayout.isRefreshing = true
            Handler().postDelayed({
                val dataSize = RadomUtils.getInt(10)
                val list = ArrayList<CountDownBean>()
                for (index in 0..dataSize){
                    list.add(CountDownBean(TimeUtils.currentTime+RadomUtils.getInt(30,100)))
                }
                if (page==1){
                    mAdapter.setList(list)
                    mAdapter.loadMoreModule.loadMoreComplete()
                }else{
                    if (list.sizeNull()>0){
                        mAdapter.addData(list)
                        mAdapter.loadMoreModule.loadMoreComplete()
                    }else mAdapter.loadMoreModule.loadMoreEnd()
                }
                bind.refreshLayout.isRefreshing = false
            },300)


        }

    override fun onDestroy() {
        super.onDestroy()
        mAdapter.destoryTimer()
    }

}