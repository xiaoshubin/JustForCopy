package com.smallcake.temp.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.temp.R
import com.smallcake.temp.bean.PageBean

class PageBeanAdapter:BaseQuickAdapter<PageBean,BaseViewHolder>(R.layout.item_page_bean) {
    override fun convert(holder: BaseViewHolder, item: PageBean) {
        holder.setText(R.id.tv_name,item.name)
    }
}