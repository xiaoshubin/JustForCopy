package com.smallcake.temp.adapter

import android.text.TextUtils
import android.view.View
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.smallcake.temp.R
import com.smallcake.temp.bean.CityBean

/**
 * Date:2021/6/19 15:21
 * Author:SmallCake
 * Desc:联系人列表
 **/
class CitySelectAdapter: BaseQuickAdapter<CityBean, BaseViewHolder>(R.layout.item_city_select) {
    override fun convert(holder: BaseViewHolder, item: CityBean) {
        holder.setText(R.id.tv_name,item.name)
        var str: String? = null
        val curIndex: String = item.firstLetter
        if (getItemPosition(item) == 0) {
            str = curIndex
        } else {
            val preIndex: String =getItem(getItemPosition(item)-1).firstLetter
            if (!TextUtils.equals(curIndex, preIndex)) str = curIndex
        }
        holder.getView<TextView>(R.id.tv_index).visibility = if (str == null) View.GONE else View.VISIBLE
        holder.setText(R.id.tv_index,if ("z" == curIndex) "#" else curIndex)
    }


}
