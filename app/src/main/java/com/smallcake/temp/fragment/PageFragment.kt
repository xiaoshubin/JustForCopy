package com.smallcake.temp.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smallcake.temp.R
import com.smallcake.temp.adapter.PageBeanAdapter
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.bean.PageBean
import com.smallcake.temp.databinding.FragmentRecyclerviewBinding
import com.smallcake.temp.map.LocationMapActivity
import com.smallcake.temp.ui.*

class PageFragment: BaseBindFragment<FragmentRecyclerviewBinding>() {
    private val mAdapter = PageBeanAdapter()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.recyclerView.apply {
            layoutManager  = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
            adapter = mAdapter
        }
        val list = listOf(
            PageBean("中间凸起导航栏",MainMiddleOutActivity::class.java,"导航"),
            PageBean("首页多布局",MainFragmentsActivity::class.java,"首页"),
            PageBean("城市选择", CitySelectActivity::class.java,"城市选择，字母选择定位"),
            PageBean("地图点击选择位置", LocationMapActivity::class.java,"定位，点击选择位置，高德地图"),
            PageBean("多图选择反馈", ReportRepairActivity::class.java,"图片选择，反馈，报事报修"),
            PageBean("录音", RecordActivity::class.java,"录音音频"),
            PageBean("签到日历", SignListActivity::class.java,"签到日历"),
            PageBean("收缩列表", SafeGoRoundHistoryInfoActivity::class.java,"收缩列表"),
            PageBean("登录", LoginActivity::class.java,"三方登录"),
            PageBean("文本", TextViewActivity::class.java,"伸缩文本"),
        )
        mAdapter.setList(list)
        mAdapter.setOnItemClickListener{ adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            val item = adapter.getItem(position) as PageBean
            goActivity(item.clz)

        }
    }
}