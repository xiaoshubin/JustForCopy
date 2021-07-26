package com.smallcake.temp.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.view.MotionEvent
import android.view.View
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.smallcake.smallutils.SpannableStringUtils
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindFragment
import com.smallcake.temp.databinding.FragmentListBinding
import com.smallcake.temp.utils.showToast
import com.willy.ratingbar.BaseRatingBar
import java.util.*

class ListFragment: BaseBindFragment<FragmentListBinding>() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        onEvent()

    }

    private fun initView() {
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


    }

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    private fun onEvent() {
        //评分控件
        bind.scaleRatingBar.setOnRatingChangeListener{ baseRatingBar: BaseRatingBar, rating: Float, fromUser: Boolean ->
            bind.tvScaleRatingBarDesc.text = "$rating 分"
        }
        //声音波纹控件
        bind.audioRecordBtn.setOnTouchListener{ v, event->
            if (event.action == MotionEvent.ACTION_DOWN) {
                bind.lineVoice.start()
            } else if (event.action == MotionEvent.ACTION_CANCEL|| event.action == MotionEvent.ACTION_UP) {
                bind.lineVoice.stop()
                bind.lineVoice.clearAnimation()
            } else if (event.action == MotionEvent.ACTION_MOVE) {
                //用于取消发送语音

            }
            false
        }
        //时间选择器
        bind.tvYmdhm.setOnClickListener{
            showTimePicker()
        }
        bind.tvYmd.setOnClickListener{
            showYMD{year,month,day->
                bind.tvYmd.text = "$year-${month+1}-$day"
            }
        }
        bind.tvHm.setOnClickListener{
            showHM{h,m->
                bind.tvHm.text = "${h}时${m}分"

            }
        }
        //Spinner的显示框
        bind.spinner1.setOnSpinnerItemSelectedListener<String>{
                oldIndex, oldItem, newIndex, newText ->
            showToast("选中了$newText")
        }

    }

    /**
     * 第三方年月日时分秒选择器
     */
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

    /**
     * 原生年月日选择器
     */
    fun showYMD( listener: (Int,Int,Int)->Unit) {
        val ca = Calendar.getInstance()
        val caMax = Calendar.getInstance()
        val mYear = ca[Calendar.YEAR]
        val mMonth = ca[Calendar.MONTH]
        val mDay = ca[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(requireContext(),R.style.dialog_date,
            { view, year, month, dayOfMonth -> listener.invoke(year,month,dayOfMonth)},
            mYear, mMonth, mDay)
        val msg = Message()
        msg.what = DialogInterface.BUTTON_POSITIVE
        val confirmText = SpannableStringUtils.getBuilder("确定").setForegroundColor(Color.RED).create()
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE,confirmText) { dialog, which ->
            listener.invoke(
                datePickerDialog.datePicker.year,
                datePickerDialog.datePicker.month,
                datePickerDialog.datePicker.dayOfMonth
            )
        }
        val cancleText = SpannableStringUtils.getBuilder("取消").setForegroundColor(Color.RED).create()
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,cancleText) { dialog, which ->

        }
        val datePicker = datePickerDialog.datePicker
        //范围控制,最大能选两年前的月份
        caMax.add(Calendar.YEAR, 1)
        datePicker.minDate = ca.timeInMillis //最小为当前
        datePicker.maxDate = caMax.timeInMillis //最大时间为当前年月
        //范围控制
        datePickerDialog.show()
    }

    /**
     * 显示时分
     * @param listener Function4<DatePicker?, Int, Int, Int, Unit>
     */
    fun showHM( listener: (Int, Int) -> Unit) {
        val ca = Calendar.getInstance()
        val mHour = ca[Calendar.HOUR]
        val mMinute = ca[Calendar.MINUTE]
        TimePickerDialog(activity,0,
            { _, hourOfDay, minute ->
                listener.invoke(hourOfDay, minute)
            },mHour,mMinute,true).show()
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