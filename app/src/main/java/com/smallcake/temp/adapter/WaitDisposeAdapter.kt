package com.smallcake.temp.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.temp.R
import com.yx.jiading.property.bean.TestBean

/**
 * Date:2021/7/29 8:40
 * Author:SmallCake
 * Desc:
 **/
class WaitDisposeAdapter: BaseQuickAdapter<TestBean, BaseViewHolder>(R.layout.item_wait_dispose),LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: TestBean) {
    }
}