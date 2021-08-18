package com.smallcake.temp.fragment

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.google.android.material.appbar.AppBarLayout
import com.smallcake.smallutils.DpUtils
import com.smallcake.temp.R
import com.smallcake.temp.adapter.WaitDisposeAdapter
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.bean.MainTabItem
import com.smallcake.temp.databinding.FragmentHomeTabRecyclerviewBinding
import com.smallcake.temp.utils.AdapterUtils
import com.smallcake.temp.utils.TabUtils
import com.smallcake.temp.utils.setSpaceView
import com.yx.jiading.property.adapter.*
import com.yx.jiading.utils.sizeNull

/**
 *遗留问题：
 * 当已经向上滑动了RecyclerView中内容，然后缓慢向下滑动，
 * RecyclerView中内容没有先滑动下来，而是先滑动了顶部AppBar部分的内容
 * 解决：通过监听RecyclerView列表的滑动距离：mmRvScrollY==0 说明滑动到顶部
 * 同时满足AppBar到顶部，列表到顶部，才触发下拉刷新事件
 */
class HomeTabRecyclerFragment : BaseBindFragment<FragmentHomeTabRecyclerviewBinding>() {

    private val mAdapter = WaitDisposeAdapter()
    private var page = 1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onEvent()
        loadData()
    }

    private fun initView() {
        initViewPager(getData(), 2, 5)
        TabUtils.createSelectBigTabs(bind.tabLayout, listOf("待处理", "待审批")) {

        }
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = mAdapter
//            setHasFixedSize(true)
//            isNestedScrollingEnabled = false
        }
        mAdapter.apply {
            setSpaceView()
            loadMoreModule.setOnLoadMoreListener {
                page++
                loadData()
            }
            setOnItemClickListener { adapter, _, postion ->
            }
        }
    }

    private fun loadData() {
        bind.refreshLayout.isRefreshing = true
        Handler().postDelayed({
            val list = AdapterUtils.createTestDatas()
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
    var mmRvScrollY = 0 // 列表滑动距离
    private fun onEvent() {
        bind.refreshLayout.setOnRefreshListener {
            page = 1
            loadData()
        }
        //解决SwipeRefreshLayout嵌套AppBarLayout下拉刷新冲突
        bind.appbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            bind.refreshLayout.isEnabled = (verticalOffset >= 0&&mmRvScrollY==0)

        })
        //发现折叠+切换Fragment偶尔出现AppBarLayout卡住，无法滑动问题，只有ViewPager的RecyclerView部分可以滑动，后面解决如下
        bind.appbar.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener{
            override fun onPreDraw(): Boolean {
                val params  = bind.appbar.layoutParams as CoordinatorLayout.LayoutParams
                val behavior = params.behavior as AppBarLayout.Behavior
                behavior.setDragCallback(object :AppBarLayout.Behavior.DragCallback(){
                    override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                        return true
                    }

                })
                return true
            }
        })

        //滑动距离监听
        bind.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                mmRvScrollY += dy
            }
        })


    }

    /**
     * @param datas   所有数据
     * @param rowNum  行数
     * @param spanNum 列数
     */
    private fun initViewPager(datas: ArrayList<MainTabItem>, rowNum: Int, spanNum: Int) {
        //1.根据数据的多少来分页，每页的数据为rw
        val singlePageDatasNum = rowNum * spanNum //每个单页包含的数据量：2*4=8；
        var pageNum: Int = datas.size / singlePageDatasNum //算出有几页菜单：20%8 = 3;
        if (datas.size % singlePageDatasNum > 0) pageNum++ //如果取模大于0，就还要多一页出来，放剩下的不满项
        val mList: ArrayList<RecyclerView> = ArrayList()
        for (i in 0 until pageNum) {
            val recyclerView = RecyclerView(requireActivity())
            val gridLayoutManager = GridLayoutManager(requireActivity(), spanNum)
            recyclerView.layoutManager = gridLayoutManager
            val fromIndex = i * singlePageDatasNum
            var toIndex = (i + 1) * singlePageDatasNum
            if (toIndex > datas.size) toIndex = datas.size
            //a.截取每个页面包含数据
            val menuItems = ArrayList(datas.subList(fromIndex, toIndex))
            //b.设置每个页面的适配器数据
            val menuAdapter = MainMenuAdapter()
            menuAdapter.setNewData(menuItems)
            //c.绑定适配器，并添加到list
            recyclerView.adapter = menuAdapter
            mList.add(recyclerView)
            menuAdapter.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>, view: View, i: Int ->
//                if (!UserUtils.isLogin){
//                    goActivity(LoginActivity::class.java)
//                    return@setOnItemClickListener
//                }
                val item = adapter.getItem(i) as MainTabItem
                item.clz?.apply {
                    goActivity(this)
                }

            }
        }
        //2.ViewPager的适配器
        val menuViewPagerAdapter = MenuViewPagerAdapter(mList)
        bind.viewPager.adapter = menuViewPagerAdapter
        //3.动态设置ViewPager的高度，并加载所有页面
        val height = DpUtils.dp2px(80f) //这里的80为MainMenuAdapter中布局文件高度
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            if (datas.size <= spanNum) height else height * rowNum
        )
        bind.viewPager.layoutParams = layoutParams
        bind.viewPager.offscreenPageLimit = pageNum - 1
        //4.创建指示器
//        initMagicIndicator()

    }

    private fun getData(): ArrayList<MainTabItem> {
        return arrayListOf(
            MainTabItem(R.mipmap.ic_logo, "一键开门"),
            MainTabItem(R.mipmap.ic_logo, "打卡签到"),
            MainTabItem(R.mipmap.ic_logo, "社区公共"),
            MainTabItem(R.mipmap.ic_logo, "安保巡逻"),
            MainTabItem(R.mipmap.ic_logo, "数据统计"),

            MainTabItem(R.mipmap.ic_logo, "工单处理"),
            MainTabItem(R.mipmap.ic_logo, "流程审批"),
            MainTabItem(R.mipmap.ic_logo, "投诉建议"),
            MainTabItem(R.mipmap.ic_logo, "信息查询"),
            MainTabItem(R.mipmap.ic_logo, "更多"),
            MainTabItem(R.mipmap.ic_logo, "更少"),
        )
    }
}