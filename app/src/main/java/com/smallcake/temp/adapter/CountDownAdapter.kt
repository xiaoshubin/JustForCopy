package com.smallcake.temp.adapter

import android.os.Handler
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.R
import com.smallcake.temp.bean.CountDownBean
import com.smallcake.temp.databinding.ItemCountDownBinding

/**
 * 参考：
 * 定时任务ScheduledThreadPoolExecutor的使用详解
 * https://blog.csdn.net/wenzhi20102321/article/details/78681379
 * 注意：在页面关闭后移除mHandler消息
 * @see destoryTimer
 *
 */
class CountDownAdapter(val cb:()->Unit): BaseQuickAdapter<CountDownBean, BaseDataBindingHolder<ItemCountDownBinding>>(R.layout.item_count_down) {

    private var mHolders: HashSet<Int> = HashSet()
    private val mHandler:Handler = Handler{
        it.target.sendEmptyMessageDelayed(0,1000)
        for (position in mHolders) {
            val item = data[position]
            val timeX = item.timeX
            if(timeX==0)cb.invoke()
            if (timeX>=0){
                val timeStr = TimeUtils.timeToDhms(timeX)
                val view = getViewByPosition(position,R.id.tv_deadline)
                if (view!=null){
                    (view as TextView).apply {
                        text = if (timeX>0)"截止时间：$timeStr" else "已结束"
                        setTextColor(ContextCompat.getColor(context,if (timeX>0)R.color.text_blue else R.color.text_gray))
                    }
                }
            }
        }

        false
    }

    override fun setList(list: Collection<CountDownBean>?) {
        super.setList(list)
        mHandler.removeMessages(0)
        mHandler.sendEmptyMessageDelayed(0,1000)
    }

    override fun addData(data: CountDownBean) {
        super.addData(data)
        mHandler.removeMessages(0)
        mHandler.sendEmptyMessageDelayed(0,1000)
    }
    fun destoryTimer(){
        mHandler.removeMessages(0)
    }

    override fun convert(holder: BaseDataBindingHolder<ItemCountDownBinding>, item: CountDownBean) {
        val timeX  = item.timeX//时间差
        val timeStr = TimeUtils.timeToDhms(timeX)
        holder.setText(R.id.tv_title,(holder.adapterPosition+1).toString())
        mHolders.add(holder.adapterPosition)
        holder.dataBinding?.tvDeadline?.apply {
            text = if (timeX>0)"截止时间：$timeStr" else "已结束"
            setTextColor(ContextCompat.getColor(context,if (timeX>0)R.color.text_blue else R.color.text_gray))
        }

    }


}