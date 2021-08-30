package com.smallcake.temp.bean

import com.smallcake.smallutils.TimeUtils


/**
 * Date:2021/7/29 8:37
 * Author:SmallCake
 * Desc:
 **/
data class TestBean(var name:String="")
data class CountDownBean(var time:Int = TimeUtils.currentTime){
    //活动结束时间和现在时间的时间差
    val timeX:Int
        get() {
            return time-TimeUtils.currentTime//时间差
        }
}