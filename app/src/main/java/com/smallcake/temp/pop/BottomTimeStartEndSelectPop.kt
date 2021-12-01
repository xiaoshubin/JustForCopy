package com.smallcake.temp.pop

import android.content.Context
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.lxj.xpopup.core.BottomPopupView
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.R
import com.smallcake.temp.databinding.PopTimeStartEndSelectBinding

/**
 * 底部弹起的开始和结束时间选择器
 */
class BottomTimeStartEndSelectPop(
    context: Context,
    val cb: (String, String) -> Unit
) : BottomPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.pop_time_start_end_select
    }
    var startTime = TimeUtils.today()
    var endTime = TimeUtils.today()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        val bind = DataBindingUtil.bind<PopTimeStartEndSelectBinding>(popupImplView)
        bind?.apply {
            dateStart.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS
            dateEnd.descendantFocusability = DatePicker.FOCUS_BLOCK_DESCENDANTS
            tvCancel.setOnClickListener { dismiss() }
            tvConfirm.setOnClickListener {
                cb.invoke(startTime,endTime)
                dismiss()
            }
            dateStart.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                startTime = "$year-${monthOfYear+1}-$dayOfMonth"

            }
            dateEnd.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
                endTime = "$year-${monthOfYear+1}-$dayOfMonth"

            }
        }
    }
}