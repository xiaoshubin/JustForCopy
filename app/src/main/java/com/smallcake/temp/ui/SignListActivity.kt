package com.smallcake.temp.ui

import android.os.Bundle
import androidx.core.content.ContextCompat
import com.haibin.calendarview.CalendarView
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivitySignListBinding
import com.smallcake.temp.utils.showToast
import java.util.*

/**
 *
 * 日历采用CalendarView
 * 文档：https://github.com/huanghaibin-dev/CalendarView/blob/master/QUESTION_ZH.md
 * 1.切换月份后，不选中每个月的第一天
 * app:select_mode="single_mode"
 *
 */
class SignListActivity : BaseBindActivity<ActivitySignListBinding>() {
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("考勤统计")
        initView()
        onEvent()
        loadData()
    }


    private fun initView() {
        //时间范围控制
        setTimeRange()


    }

    private fun setTimeRange() {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH] + 1
        val mDay = c[Calendar.DAY_OF_MONTH]
        updateMonth(mMonth)
        //范围控制，查询最近一年的日历
        bind.calendarView.setRange(mYear - 1, mMonth, mDay, mYear, mMonth, mDay)
        //默认滚动到当前日期
        bind.calendarView.scrollToCurrent()
    }

    private fun onEvent() {
        //日期点击改变监听
        bind.calendarView.setOnCalendarSelectListener(object :CalendarView.OnCalendarSelectListener {
            override fun onCalendarOutOfRange(calendar: com.haibin.calendarview.Calendar) {}
            override fun onCalendarSelect(calendar: com.haibin.calendarview.Calendar, isClick: Boolean) {
                if (isClick) {
                    val year = calendar.year
                    val month = calendar.month
                    val day = calendar.day
                    val monthStr = if (month < 10) "0$month" else month.toString() + ""
                    val dayStr = if (day < 10) "0$day" else day.toString() + ""
                    val selectTime = "$year-$monthStr-$dayStr"
                    showToast("你选中了$selectTime")

                }
            }
        })
        //日历月份滚动改变监听
        bind.calendarView.setOnMonthChangeListener { year: Int, month: Int ->
            updateMonth(month)
        }
        //点击收起
        bind.layoutExpand.setOnClickListener{

        }
    }
    private fun loadData() {
        initItems()
    }

    /**
     * 更新月份改变
     * @param month Int
     */
    private fun updateMonth(month:Int) {
        bind.tvMonth.text = "(${month}月)"
        bind.tvMonthDesc.text = "${month}月汇总"
    }
    /**
     * 有记录的信息，以标签形式写入
     */
    private fun initItems() {
        //测试数据
        val calendars: HashMap<String?, com.haibin.calendarview.Calendar?> = HashMap()
        //标签颜色
        val colorOrange = ContextCompat.getColor(this, R.color.text_orange)
        val colorGrass = ContextCompat.getColor(this, R.color.text_grass)
        val colorRed = ContextCompat.getColor(this, R.color.text_red)

        //标签日期，颜色，文字
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH] + 1
        val calendar1 = createSchemeCalendar(mYear, mMonth, 1, colorOrange,"●")
        val calendar2 = createSchemeCalendar(mYear, mMonth, 2, colorGrass,"●")
        val calendar5 = createSchemeCalendar(mYear, mMonth, 5, colorGrass,"●")
        val calendar6 = createSchemeCalendar(mYear, mMonth, 6, colorRed,"●")
        val calendar7 = createSchemeCalendar(mYear, mMonth, 7, colorGrass,"●")
        //装载标签
        calendars[calendar1.toString()] = calendar1
        calendars[calendar2.toString()] = calendar2
        calendars[calendar5.toString()] = calendar5
        calendars[calendar6.toString()] = calendar6
        calendars[calendar7.toString()] = calendar7
        //设置标签
        bind.calendarView.setSchemeDate(calendars)
    }

    /**
     * 获取单个有事件的标记
     * @param year Int
     * @param month Int
     * @param day Int
     * @param color Int
     * @param text String
     * @return com.haibin.calendarview.Calendar
     */
    private fun createSchemeCalendar(year: Int, month: Int, day: Int, color: Int, text: String): com.haibin.calendarview.Calendar {
        val calendar = com.haibin.calendarview.Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = color //如果单独标记颜色、则会使用这个颜色
        calendar.scheme = text
        return calendar
    }


}