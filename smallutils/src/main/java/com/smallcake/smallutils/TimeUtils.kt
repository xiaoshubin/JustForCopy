package com.smallcake.smallutils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间工具类
 * 12小时 - 时：分：秒  HH改为hh
 * H12_MM_SS = "hh:mm:ss"
 */
object TimeUtils {
    /**
     * 年-月-日
     */
    const val YYYY_MM_DD = "yyyy-MM-dd"

    /**
     * 24小时 - 时：分：秒
     */
    const val H24_MM_SS = "HH:mm:ss"

    /**
     * 12小时 - 时：分：秒
     */
    const val H12_MM_SS = "hh:mm:ss"

    /**
     * 24小时，年-月-日 时：分：秒
     */
    const val YYYY_MM_DD_H24_MM_SS = "yyyy-MM-dd HH:mm:ss"

    /**
     * 时间戳 转 String
     * @param time 时间戳
     * @param timeFormat 时间戳 默认yyyy-MM-dd
     * @return 2021-09-24
     */
    fun timeToStr(time: Int, timeFormat: String = YYYY_MM_DD): String {
        val fm = SimpleDateFormat(timeFormat, Locale.CHINA)
        val time1000 = time.toString().toLong() * 1000
        return fm.format(time1000)
    }
    /**
     * 将字符串转为时间戳
     * @param dateFormat 时间戳格式
     * @param dateTime       时间
     * @return 时间戳(秒) 2021-12-07 14:21:33 >> 1638858093
     */
    fun strToTime(dateTime: String, dateFormat: String = YYYY_MM_DD_H24_MM_SS): Int {
        val timeLong = SimpleDateFormat(dateFormat, Locale.CHINA).parse(dateTime)?.time?:0
        return (timeLong/1000L).toInt()
    }

    /**
     * 获取今天年月日
     * @param timeFormat 时间戳 默认yyyy-MM-dd
     * @return 2017-08-14
     */
    fun today(timeFormat: String = YYYY_MM_DD): String {
        val timeInt = (Date().time/1000).toInt()
        return timeToStr(timeInt,timeFormat)
    }


    /**
     * 获取当前系统的时间戳(10位)
     * @return 1502697135
     */
    val currentTime: Int
        get() = (System.currentTimeMillis() / 1000).toInt()
    /**
     * 获取当前系统的时间戳(10位)
     * @return 2021-09-24 09:21:33
     */
    val currentTimeYmdhms: String
        get() = timeToStr((System.currentTimeMillis() / 1000).toInt(),YYYY_MM_DD_H24_MM_SS)






    /**
     * 获取日期是星期几
     * @param dateTime       时间
     * @param dateFormat 时间戳格式
     *
     * 注意：dateTime格式要为 yyyy-MM-dd
     */
    fun dayOfWeek(dateTime: String, dateFormat: String = YYYY_MM_DD): String {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat(dateFormat, Locale.CHINA)//这里的格式要和传进来的一样，否则会转换错误
        var date: Date? = null
        try {
            date = sdf.parse(dateTime)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        cal.time = Date(date?.time ?: 0)
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            1 -> "星期日"
            2 -> "星期一"
            3 -> "星期二"
            4 -> "星期三"
            5 -> "星期四"
            6 -> "星期五"
            7 -> "星期六"
            else -> ""
        }
    }
    fun dayOfWeek(date: Date?): String {
        val cal = Calendar.getInstance()
        cal.time = Date(date?.time ?: 0)
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            1 -> "星期日"
            2 -> "星期一"
            3 -> "星期二"
            4 -> "星期三"
            5 -> "星期四"
            6 -> "星期五"
            7 -> "星期六"
            else -> ""
        }
    }
    /**
     * 获取几天后的时间戳（秒）
     * @param day 几天后
     */
    fun afterDays(day: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + (day * 60 * 60 * 24)) //秒
        return (calendar.time.time/1000L).toInt()
    }
    /**
     * 获取几个月后的今天
     * 例如今天是6月12日（2021-06-12），那么如果addMonth传入2后就是（2021-08-12）
     * @param addMonth Int 追加的月份
     * @return String 2021-08-12
     */
    fun afterMonths(addMonth: Int, timeFormat: String = YYYY_MM_DD): String {
        val sdf = SimpleDateFormat(timeFormat, Locale.CHINA)
        val now = Date()
        val calendar = Calendar.getInstance()
        calendar.time = now
        calendar.add(Calendar.MONTH, addMonth)
        return sdf.format(calendar.time)
    }
    /**
     * 将时间戳转化成Calendar对象(获取时间详情)
     * val year = calendar.get(Calendar.YEAR)
     * val month = calendar.get(Calendar.MONTH)
     * val day = calendar.get(Calendar.DAY_OF_MONTH)
     * val hours = calendar.get(Calendar.HOUR_OF_DAY)
     * val minute = calendar.get(Calendar.MINUTE)
     * val seconds = calendar.get(Calendar.SECOND)
     * @param time 时间戳
     * @return calendar对象
     */
    fun getTimeCalender(time: Long): Calendar {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return calendar
    }

    /**
     * 时间转换为1天00：00：00
     * @param timeX Int
     * @return String
     */
    fun timeToDhms(timeX: Int): String {
        val dayUnit = 24 * 60 * 60
        val hourUnit = 60 * 60
        val dayInt = timeX / (dayUnit)
        val hourInt = (timeX - dayInt * dayUnit) / (hourUnit)
        val minutesInt = (timeX - dayInt * dayUnit - hourInt * hourUnit) / 60
        val secoundInt = timeX - dayInt * dayUnit - hourInt * hourUnit - minutesInt * 60

        val dayStr = if (dayInt > 0) "${dayInt}天" else ""
        val hourStr = if (hourInt==0)"00" else (if (hourInt < 10) "0${hourInt}" else "$hourInt")

        val minutesStr = if (minutesInt==0)"00" else(if (minutesInt < 10) "0${minutesInt}" else "$minutesInt")
        val secoundStr = if (secoundInt==0)"00" else(if (secoundInt < 10) "0${secoundInt}" else "$secoundInt")

        return "$dayStr$hourStr:$minutesStr:$secoundStr"
    }

    /**
     * 获取这个月有多少天
     * @param year Int
     * @param month Int
     * @return Int
     */
    fun getMonthOfDay(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 ->  31
            4, 6, 9, 11 ->  30
            2 ->  if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) 29 else 28
            else -> 0
        }

    }

    /**
     * 当前月份有多少天
     * @return Int
     */
    fun getCurrentMonthOfDay(): Int {
        val ca = Calendar.getInstance()
        val year = ca.get(Calendar.YEAR)
        val month = ca.get(Calendar.MONTH)+1
        return getMonthOfDay(year,month)
    }
}

/**
 * 时间相关的扩展函数
 * @receiver Int
 * @return [ERROR : <ERROR FUNCTION RETURN TYPE>]
 */
