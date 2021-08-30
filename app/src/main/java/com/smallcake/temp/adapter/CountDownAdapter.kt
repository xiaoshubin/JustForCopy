package com.smallcake.temp.adapter

import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.R
import com.smallcake.temp.bean.CountDownBean
import com.smallcake.temp.databinding.ItemCountDownBinding

class CountDownAdapter: BaseQuickAdapter<CountDownBean, BaseDataBindingHolder<ItemCountDownBinding>>(R.layout.item_count_down) {
    override fun convert(holder: BaseDataBindingHolder<ItemCountDownBinding>, item: CountDownBean) {
        holder.dataBinding?.item = item
        val timeX  = item.timeX//时间差
        val timeStr = TimeUtils.timeToDhms(timeX)
        holder.dataBinding?.tvDeadline?.apply {
            text = if (timeX>0)"截止时间：$timeStr" else "已结束"
            setTextColor(ContextCompat.getColor(context,if (timeX>0)R.color.text_blue else R.color.text_gray))
        }
        holder.dataBinding?.tvCount?.apply {
            setStb_solidColor(ContextCompat.getColor(context,if (timeX>0)R.color.text_red else R.color.text_gray) )
        }
    }
}