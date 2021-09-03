package com.smallcake.temp.adapter

import android.os.Handler
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
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
class CountDownAdapter: BaseQuickAdapter<CountDownBean, BaseDataBindingHolder<ItemCountDownBinding>>(R.layout.item_count_down),LoadMoreModule {

    private var mHolders: HashSet<Int> = HashSet()
    private var cb: (()->Unit)? = null//倒计时结束回调
    fun setOnTimeOverListener(endCallback:()->Unit){
        cb = endCallback
    }
    private val mHandler:Handler = Handler{
        it.target.sendEmptyMessageDelayed(0,1000)
        for (position in mHolders) {
            val item = data[position]
            val timeX = item.timeX
            if(timeX==0)cb?.invoke()
            if (timeX>=0){
                val timeStr = TimeUtils.timeToDhms(timeX)
                val view = getViewByPosition(position,R.id.tv_deadline) ?: continue
                (view as TextView).apply {
                        text = if (timeX>0)"截止时间：$timeStr" else "已结束"
                        setTextColor(ContextCompat.getColor(context,if (timeX>0)R.color.text_blue else R.color.text_gray))
                }

            }
        }

        false
    }

    override fun setList(list: Collection<CountDownBean>?) {
        super.setList(list)
        mHolders.clear()
        val listIndex = list?.mapIndexed{index,it->index}
        listIndex?.let { mHolders.addAll(it) }
        mHandler.removeMessages(0)
        mHandler.sendEmptyMessageDelayed(0,1000)
    }

    override fun addData(newData: Collection<CountDownBean>) {
        super.addData(newData)
        mHandler.removeMessages(0)
        mHandler.sendEmptyMessageDelayed(0,1000)
    }

    fun destoryTimer(){
        mHandler.removeMessages(0)
    }

    override fun convert(holder: BaseDataBindingHolder<ItemCountDownBinding>, item: CountDownBean) {
        holder.setText(R.id.tv_title,(holder.adapterPosition+1).toString())

        val timeX  = item.timeX//时间差
        holder.dataBinding?.tvDeadline?.apply {
            val timeStr = TimeUtils.timeToDhms(timeX)
            text = if (timeX>0)"截止时间：$timeStr" else "已结束"
            setTextColor(ContextCompat.getColor(context,if (timeX>0)R.color.text_blue else R.color.text_gray))
        }

    }


}