package com.smallcake.temp.fragment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smallcake.temp.adapter.PageBeanAdapter
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.bean.PageBean
import com.smallcake.temp.databinding.FragmentHomeBinding
import com.smallcake.temp.databinding.FragmentRecyclerviewBinding
import com.smallcake.temp.map.LocationMapActivity
import com.smallcake.temp.ui.CitySelectActivity
import com.smallcake.temp.ui.MainMiddleOutActivity
import com.smallcake.temp.ui.ReportRepairActivity

class PageFragment: BaseBindFragment<FragmentRecyclerviewBinding>() {
    private val mAdapter = PageBeanAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.recyclerView.apply {
            layoutManager  = GridLayoutManager(context,3)
            adapter = mAdapter
        }
        val list = listOf(
            PageBean("中间凸起导航栏",MainMiddleOutActivity::class.java,"导航"),
            PageBean("城市选择", CitySelectActivity::class.java,"城市选择，字母选择定位"),
            PageBean("地图点击选择位置", LocationMapActivity::class.java,"定位，点击选择位置，高德地图"),
            PageBean("反馈", ReportRepairActivity::class.java,"图片选择，反馈，报事报修"),
        )
        mAdapter.setList(list)
        mAdapter.setOnItemClickListener{ adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            val item = adapter.getItem(position) as PageBean
            goActivity(item.clz)

        }
    }
}