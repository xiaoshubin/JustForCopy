package com.smallcake.temp.map

import android.content.Context
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import org.koin.core.component.inject

object AmapLocation : KoinComponent {
    /**
     * 定位一次
     * 从定位5.6.0版本起对旧版本SDK不兼容，
     * 请务必确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口，
     * 否则可能导致编译不通过等异常情况
     * AMapLocationClient.updatePrivacyShow(context,true,true)
     * AMapLocationClient.updatePrivacyAgree(context,true)
     */
    fun onceLocation(context: Context,listener: AMapLocationListener) {
//        val client : AMapLocationClient by inject { parametersOf(context) }
        AMapLocationClient.updatePrivacyShow(context,true,true)
        AMapLocationClient.updatePrivacyAgree(context,true)
        val option = AMapLocationClientOption()
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        option.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        option.interval = 2000
        option.isOnceLocation = true
        val client = AMapLocationClient(context)

        client.setLocationOption(option)
        client.setLocationListener(listener)
        client.startLocation()
    }
}