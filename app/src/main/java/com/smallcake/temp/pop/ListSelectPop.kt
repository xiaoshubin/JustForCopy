package com.smallcake.temp.pop

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.animation.AnimationSet
import android.widget.HorizontalScrollView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.mapcore.util.ho
import com.amap.api.mapcore.util.it
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.android.material.tabs.TabLayout
import com.haibin.calendarview.BaseView
import com.lxj.xpopup.core.BottomPopupView
import com.smallcake.smallutils.Screen
import com.smallcake.smallutils.px
import com.smallcake.temp.R
import com.smallcake.temp.databinding.PopListSelectBinding
import com.smallcake.temp.utils.showToast
import com.smallcake.temp.utils.sizeNull
import org.litepal.util.LitePalLog.level

class ListSelectPop(context: Context,val cb:(String)->Unit) : BottomPopupView(context) {

    private var selectStr = ""//选择的项
    private val tabList: ArrayList<String> = ArrayList<String>()
    private var currentItemsIndex=0//当前正在显示的列表项索引值
    override fun getImplLayoutId(): Int {
        return R.layout.pop_list_select
    }

    override fun onCreate() {
        super.onCreate()
        val bind = DataBindingUtil.bind<PopListSelectBinding>(popupImplView)
        bind?.apply {
            tvCancle.setOnClickListener { dismiss() }
            tvConfirm.setOnClickListener {
                if (TextUtils.isEmpty(selectStr)){
                    showToast("请选择")
                    return@setOnClickListener
                }
                cb(selectStr)
                dismiss()
            }
            //禁止手动滑动
            bind.scrollView.setOnTouchListener { v, event ->
                return@setOnTouchListener true
            }
            bind.scrollView.isSmoothScrollingEnabled = true
            val list = (0..20).map { "项目$it" }
            createAddView(list,this)


            val mAdapter = ProjectSelectAdapter()
            val layoutManager = LinearLayoutManager(context)
            layoutManager.orientation = RecyclerView.HORIZONTAL
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = mAdapter

            mAdapter.addData(ProjectBaen(0))
            //子项点击事件
            mAdapter.setListener { level, s ->
                //如果点击的前面级别的项目，清空后面的项目
                if (level<mAdapter.data.sizeNull()-1){
                    for (i in level+1 until layoutTabs.childCount){
                        layoutTabs.removeViewAt(i)
                    }
                    (layoutTabs.getChildAt(level) as TextView).text = s
                    val subList = mAdapter.data.subList(level,mAdapter.data.sizeNull())
                    mAdapter.data.removeAll(subList)
                    recyclerView.smoothScrollToPosition(level)
                    return@setListener
                }
                //添加Tab
                val tv = TextView(context)
                tv.setTextColor(Color.BLACK)
                tv.setPadding(8.px,8.px,8.px,8.px)
                tv.text = s
                layoutTabs.addView(tv)
//                mAdapter.addData(ProjectBaen(level+1))
                Handler().postDelayed({
                    scrollViewTabs.fullScroll(FOCUS_RIGHT)
                },300)
                recyclerView.smoothScrollToPosition(mAdapter.data.sizeNull())
                tv.setOnClickListener {
                    scrollViewTabs.smoothScrollTo(tv.x.toInt(),0)
                    recyclerView.smoothScrollToPosition(level)
                }
            }


        }
    }
    private fun scrollAnim(bind: PopListSelectBinding,index:Int){
        if (index==0)return
        val animationSet = AnimatorSet()
        val objectAnimator = ObjectAnimator.ofFloat(bind.layoutItems,"translationX",0f,(Screen.width/2f)*(index))
        animationSet.duration = 300
        animationSet.play(objectAnimator)
        animationSet.start()

    }
    private fun createAddView(list: List<String>, bind: PopListSelectBinding){
        val layoutParams = LinearLayoutCompat.LayoutParams(Screen.width/2,Screen.height/2)
        val view = LayoutInflater.from(context).inflate(R.layout.item_projects,null)
        view.layoutParams = layoutParams
        val layoutTxt = view.findViewById<LinearLayoutCompat>(R.id.layout_txt)
        list.forEachIndexed {i, txt->
            val tv = TextView(context)
            tv.setPadding(8.px,8.px,8.px,8.px)
            tv.text = txt
            layoutTxt.addView(tv)
            tv.setOnClickListener {
                clearTvBg(layoutTxt)
                //如果点击的前面的项
                if (level<bind.layoutItems.childCount){
                val level = txt.split("==").sizeNull()
                    currentItemsIndex = level
                    tv.setBackgroundColor(Color.parseColor("#666666"))
                    bind.layoutItems.removeViews(level,bind.layoutItems.childCount-level)
                    removeFrom(tabList,level-1)
                    if (!tabList.contains(txt)){
                        tabList.add(txt)
                        initTabCreate(bind.tabLayout,tabList.toList()){tabIndex->
                            scrollAnim(bind,tabIndex-currentItemsIndex)
                            currentItemsIndex = tabIndex
                        }
                        createAddView((0..20).map{ j->"$txt==$j"}, bind)
                        bind.tabLayout.post {
                            bind.tabLayout.getTabAt(tabList.sizeNull()-1)?.select()
                        }

                        bind.scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                    }
                }else if (!tabList.contains(txt)){
                    tv.setBackgroundColor(Color.parseColor("#666666"))
                    tabList.add(txt)
                    initTabCreate(bind.tabLayout,tabList.toList()){tabIndex->
                        scrollAnim(bind,tabIndex-currentItemsIndex)
                        currentItemsIndex = tabIndex
                    }
                    createAddView((0..20).map{ j->"$txt==$j"}, bind)
                    bind.tabLayout.post {
                        bind.tabLayout.getTabAt(tabList.sizeNull()-1)?.select()
                    }
                    bind.scrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                }

            }
        }
        bind.layoutItems.addView(view)
    }

    private fun clearTvBg(layoutTxt: LinearLayoutCompat) {
        val childCount = layoutTxt.childCount
        for (i in 0 until childCount){
            val tv = layoutTxt.get(i) as TextView
            tv.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    private fun removeFrom(arrayList: ArrayList<String>,pos:Int){
        val subList = arrayList.subList(pos,arrayList.sizeNull())
        arrayList.removeAll(subList)
    }

    private fun initTabCreate(tabLayout: TabLayout, list:List<String>, cb:(Int)->Unit){
        tabLayout.removeAllTabs()
        list.forEachIndexed {i,it->
            val tab = tabLayout.newTab()
            tab.text = it
            tabLayout.addTab(tab)
        }
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                cb(tab.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {
            }
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }
    class ProjectSelectAdapter:BaseQuickAdapter<ProjectBaen,BaseViewHolder>(R.layout.item_project_select){
        var cb:((Int,String)->Unit)?=null
        fun setListener(listener:(Int,String)->Unit){
            cb=listener
        }
        override fun convert(holder: BaseViewHolder, item: ProjectBaen) {
            val layoutTxt = holder.getView<LinearLayoutCompat>(R.id.layout_root)
            layoutTxt.layoutParams =  LinearLayoutCompat.LayoutParams(Screen.width/2,LinearLayoutCompat.LayoutParams.MATCH_PARENT)
            (0..20).forEach {index->
                val tv = TextView(context)
                tv.setPadding(8.px,8.px,8.px,8.px)
                tv.text = "${item.level}级别项目$index"
                layoutTxt.addView(tv)
                tv.setOnClickListener {
                    cb?.invoke(item.level,"${item.level}级别项目$index")
                    this.addData(ProjectBaen(item.level+1))
                }
            }
        }

    }

    data class ProjectBaen(val level:Int)
}