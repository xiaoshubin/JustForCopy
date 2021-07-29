package com.smallcake.temp.utils

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.smallcake.temp.R
import com.yx.jiading.property.bean.TestBean

/**
 * 对BaseQuickAdapter设置占位视图
 * @emptyView 视图View
 * 注意设置的视图一定要包含有@+id/tv的TextView，以便于设置文字描述
 */
fun BaseQuickAdapter<*,*>.setSpaceView(emptyView:View?=null,desc:String?="暂无数据"): View {
    val view = emptyView?:LayoutInflater.from(this.context).inflate(R.layout.empty_view, null)
    view.findViewById<TextView>(R.id.tv).text = desc
    setEmptyView(view)
    return view
}
/**
 * 对BaseQuickAdapter设置占位视图layout
 * @resource 视图资源layout
 */
fun BaseQuickAdapter<*,*>.setSpaceRes(@LayoutRes resource:Int?=null): View {
    val view = LayoutInflater.from(this.context).inflate(resource?:R.layout.empty_view, null)
    setEmptyView(view)
    return view
}

object AdapterUtils {
    fun setEmptyView(mAdapter: BaseQuickAdapter<*,*>){
        mAdapter.setEmptyView(LayoutInflater.from(mAdapter.context).inflate(R.layout.empty_view,null))
    }

    fun createTestDatas(num:Int=10):ArrayList<TestBean>{
        val list = ArrayList<TestBean>()
        for(index in 0..num)list.add(TestBean())
        return  list
    }
    fun intoTestDatas(mAdapter: BaseQuickAdapter<TestBean,*>,num:Int=10){
        val list = ArrayList<TestBean>()
        for(index in 0..num)list.add(TestBean())
        mAdapter.setList(list)
    }

}
