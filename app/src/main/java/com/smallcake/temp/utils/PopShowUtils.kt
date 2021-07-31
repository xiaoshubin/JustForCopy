package com.smallcake.temp.utils

import android.content.Context
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.smallcake.smallutils.TimeUtils
import com.smallcake.temp.fragment.setupDefault
import java.util.*

object PopShowUtils {
    /**
     * 第三方年月日时分秒选择器
     */
     fun showTimePicker(context: Context,cb:(Date)->Unit) {
        val showTypes = booleanArrayOf(true,true,true,true,true,false)
        val types = BooleanArray(6)
        types[0] = true
        types[1] = true
        types[2] = true
        types[3] = true
        types[4] = true
        types[5] = false
        val picker = TimePickerBuilder(context) { date, _ ->
            cb.invoke(date)
        }.setType(showTypes)
            .setupDefault()
            .build()
        picker.setTitleText("时间选择")
        picker.show(true)
    }
}