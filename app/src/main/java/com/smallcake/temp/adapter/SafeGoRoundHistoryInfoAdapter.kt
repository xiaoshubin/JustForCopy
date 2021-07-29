package com.smallcake.temp.adapter

import android.text.TextUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.temp.R
import com.yx.jiading.property.bean.TestBean

/**
 * Date:2021/7/29 15:55
 * Author:SmallCake
 * Desc:
 *
mAdapter.apply {
        addChildClickViewIds(R.id.tv_time)
        setOnItemChildClickListener{ adapter: BaseQuickAdapter<*, *>, view: View, i: Int ->
            val item =  adapter.getItem(i) as TestBean
            if (view.id==R.id.tv_time){
            val name = item.name
            item.name = if (TextUtils.isEmpty(name)) "show" else null
            notifyItemChanged(i)
        }
    }
}
 **/
class SafeGoRoundHistoryInfoAdapter: BaseQuickAdapter<TestBean, BaseViewHolder>(R.layout.item_safe_go_round_history_info),LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: TestBean) {
        val isShow = TextUtils.isEmpty(item.name)
        holder.setGone(R.id.layout_desc_img,!isShow)
    }
}