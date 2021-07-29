package com.yx.jiading.property.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.temp.R
import com.smallcake.temp.bean.MainTabItem

/**
 * Date:2021/7/9 15:04
 * Author:SmallCake
 * Desc:
 **/
class MainMenuAdapter:BaseQuickAdapter<MainTabItem,BaseViewHolder>(R.layout.item_main_menu) {
    override fun convert(holder: BaseViewHolder, item: MainTabItem) {
        holder.setImageResource(R.id.iv,item.res)
        holder.setText(R.id.tv_name,item.name)
    }
}