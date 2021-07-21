package com.smallcake.temp.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.smallcake.smallutils.KeyboardUtils
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.databinding.FragmentListBinding
import com.willy.ratingbar.BaseRatingBar
import java.util.*

class ListFragment: BaseBindFragment<FragmentListBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onEvent()

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onEvent() {
        //评分控件
        bind.scaleRatingBar.setOnRatingChangeListener{ baseRatingBar: BaseRatingBar, rating: Float, fromUser: Boolean ->
            bind.tvScaleRatingBarDesc.text = "$rating 分"
        }
        //声音波纹控件
        bind.lineVoice.apply {
            duration = 150
            lineWidth = 3f
            addBody(32)
            addBody(16)
            addBody(32)
            addBody(48)
            addBody(32)
            addBody(16)
            addBody(32)
            addBody(48)
            addBody(32)
            addBody(16)
            addBody(32)
            addBody(48)
            addBody(32)
            addBody(16)
            addBody(32)
            addBody(48)
            addBody(32)
        }
        bind.audioRecordBtn.setOnTouchListener{ v, event->

            if (event.action == MotionEvent.ACTION_DOWN) {
                bind.lineVoice.start()

            } else if (event.action == MotionEvent.ACTION_CANCEL|| event.action == MotionEvent.ACTION_UP) {
                bind.lineVoice.stop()
                bind.lineVoice.clearAnimation()
            } else if (event.action == MotionEvent.ACTION_MOVE) {

            }
            false
        }
        //时间选择器
        bind.tvYmdhm.setOnClickListener{
            showTimePicker()
        }
    }

    private fun showTimePicker() {
        val showTypes = booleanArrayOf(true,true,true,true,true,false)
        val types = BooleanArray(6)
        types[0] = true
        types[1] = true
        types[2] = true
        types[3] = true
        types[4] = true
        types[5] = false
        val picker = TimePickerBuilder(requireContext()) { date, _ ->
            val showStr = TimeUtils.timeToStr((date.time/1000).toInt(),"yyyy-MM-dd HH:mm")
            bind.tvYmdhm.text = showStr

        }.setType(showTypes)
            .setupDefault()
            .build()

        picker.setTitleText("时间选择")
        picker.show(true)
    }


}

/**
 * 范围设定 ：从当前时间 到 一年后的当前时间
 */
fun TimePickerBuilder.setupDefault(): TimePickerBuilder {
    val currentCalendar = TimeUtils.getTimeCalender(System.currentTimeMillis())
    val currentYear = currentCalendar.get(Calendar.YEAR)
    val currentMonth = currentCalendar.get(Calendar.MONTH)
    val currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH)
    val startDate: Calendar = Calendar.getInstance()
    startDate.set(currentYear, currentMonth, currentDay)
    val endDate: Calendar = Calendar.getInstance()
    endDate.set(currentYear + 1, currentMonth, currentMonth)
    return setCancelText("取消")
        .setSubmitText("确定")
        .setCancelColor(Color.parseColor("#666666"))
        .setSubmitColor(Color.parseColor("#D5462B"))
        .setTitleColor(Color.parseColor("#999999"))
        .setTitleSize(18)
        .setContentTextSize(18)
        .setSubCalSize(18)
        .isCyclic(false)
        .setLineSpacingMultiplier(1.8F)
        .setOutSideCancelable(true)
        .isDialog(false)
        .setItemVisibleCount(5)
        .setRangDate(startDate, endDate)
        .setDate(currentCalendar)
}