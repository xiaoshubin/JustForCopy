package com.smallcake.temp.ui

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smallcake.smallutils.custom.GridItemDecoration
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.adapter.SafeGoRoundHistoryInfoAdapter
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.bean.TestBean
import com.smallcake.temp.databinding.ActivitySafeGoRoundHistoryBinding
import com.smallcake.temp.utils.AdapterUtils
import com.smallcake.temp.utils.setSpaceView
import com.yx.jiading.property.adapter.*
import com.smallcake.temp.utils.sizeNull

class SafeGoRoundHistoryInfoActivity : BaseBindActivity<ActivitySafeGoRoundHistoryBinding>() {
    private val mAdapter = SafeGoRoundHistoryInfoAdapter()
    private var page=1
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("巡更历史详情")
        initView()
        onEvent()
        loadData()
    }

    private fun onEvent() {
        bind.refreshLayout.setOnRefreshListener {
            page=1
            loadData()
        }
    }

    private fun initView() {
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SafeGoRoundHistoryInfoActivity)
            addItemDecoration(GridItemDecoration(1))
            adapter = mAdapter
            recycledViewPool.setMaxRecycledViews(R.id.layout_desc_img,0)
        }
        mAdapter.apply {
            setSpaceView()
            loadMoreModule.setOnLoadMoreListener {
                page++
                loadData()
            }

            addChildClickViewIds(R.id.tv_time)
            setOnItemChildClickListener{ adapter: BaseQuickAdapter<*, *>, view: View, i: Int ->
               val item =  adapter.getItem(i) as TestBean
                    if (view.id==R.id.tv_time){
                        val name = item.name
                        item.name = if (TextUtils.isEmpty(name)) "show" else ""
                        notifyItemChanged(i)

                    }
            }
        }
    }
    private fun loadData() {

        bind.refreshLayout.isRefreshing = true
        Handler().postDelayed({
            val list = if (page > 3) ArrayList<TestBean>() else AdapterUtils.createTestDatas()
                mAdapter.apply {
                    if (list.sizeNull() > 0) {
                        if (page == 1) setList(list) else addData(list)
                        loadMoreModule.loadMoreComplete()
                    } else {
                        if (page == 1) setList(list)
                        loadMoreModule.loadMoreEnd()
                    }
                }
            bind.refreshLayout.isRefreshing = false
        }, 300)


    }
}


