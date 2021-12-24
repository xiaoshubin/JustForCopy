package com.smallcake.temp.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smallcake.smallutils.custom.GridItemDecoration
import com.smallcake.temp.adapter.PageBeanAdapter
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.bean.PageBean
import com.smallcake.temp.chart.ChartActivity
import com.smallcake.temp.coroutines.CoroutinesActivity
import com.smallcake.temp.coroutines.LiveDataViewModule
import com.smallcake.temp.databinding.FragmentRecyclerviewBinding
import com.smallcake.temp.kotlinflow.KotlinFlowActivity
import com.smallcake.temp.map.BaiduMapActivity
import com.smallcake.temp.map.LocationMapActivity
import com.smallcake.temp.music.ExoMusicActivity
import com.smallcake.temp.pay.GooglePayActivity
import com.smallcake.temp.service.ServiceActivity
import com.smallcake.temp.ui.*

class PageFragment: BaseBindFragment<FragmentRecyclerviewBinding>() {
    private val mAdapter = PageBeanAdapter()
    internal val viewModel: LiveDataViewModule by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.recyclerView.apply {
            addItemDecoration(GridItemDecoration(4))
            layoutManager  = GridLayoutManager(requireContext(),4)
            adapter = mAdapter
        }
        val list = listOf(
            PageBean("中间凸起导航栏",MainMiddleOutActivity::class.java,"导航"),
            PageBean("首页多布局",MainFragmentsActivity::class.java,"首页"),
            PageBean("城市选择", CitySelectActivity::class.java,"城市选择，字母选择定位"),
            PageBean("高德地图点击选择位置", LocationMapActivity::class.java,"定位，点击选择位置，高德地图"),
            PageBean("百度地图", BaiduMapActivity::class.java,"定位，点击选择位置，百度地图"),
            PageBean("多图选择反馈", ReportRepairActivity::class.java,"图片选择，反馈，报事报修"),
            PageBean("录音", RecordActivity::class.java,"录音音频"),
            PageBean("签到日历", SignListActivity::class.java,"签到日历"),
            PageBean("收缩列表", SafeGoRoundHistoryInfoActivity::class.java,"收缩列表"),
            PageBean("登录", LoginActivity::class.java,"三方登录"),
            PageBean("文本", TextViewActivity::class.java,"伸缩文本"),
            PageBean("ShapeView", ShapeViewActivity::class.java,"ShapeView,ShapeButton,ShapeTextView"),
            PageBean("倒计时列表", CountDownListActivity::class.java,"倒计时列表"),
            PageBean("下载", DownloadDataActivity::class.java,"下载apk"),
            PageBean("视频相关", VideoActivity::class.java,"视频压缩，视频播放"),
            PageBean("多布局列表", MoreLayoutListActivity::class.java,"多布局列表"),
            PageBean("相机", CameraCustomActivity::class.java,"自定义相机"),
            PageBean("PDF", PdfActivity::class.java,"PDF"),
            PageBean("手写签名", SignActivity::class.java,"手写签名"),
            PageBean("雷达图", ChartActivity::class.java,"雷达图，曲线图，饼状图"),
            PageBean("音乐播放", ExoMusicActivity::class.java,"音乐播放"),
            PageBean("谷歌支付", GooglePayActivity::class.java,"谷歌支付,Google Pay"),
            PageBean("Kotlin Flow", KotlinFlowActivity::class.java,"Kotlin Flow"),
            PageBean("协程", CoroutinesActivity::class.java,"协程，Coroutines"),
            PageBean("Svga", SvgaActivity::class.java,"Svga"),
            PageBean("service服务", ServiceActivity::class.java,"service，后台服务"),
            PageBean("轮播图", BannerActivity::class.java,"轮播图"),
            PageBean(".9图片", NinePatchActivity::class.java,".9图片"),
            PageBean("聊天界面", P2PChatActivity::class.java,"聊天界面"),
            PageBean("联系人", ContactActivity::class.java,"联系人"),
        )
        mAdapter.setList(list)
        mAdapter.setOnItemClickListener{ adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            val item = adapter.getItem(position) as PageBean
            goActivity(item.clz)
        }

         bind.lifecycleOwner = this
         bind.mobileMoudle = viewModel
    }
}