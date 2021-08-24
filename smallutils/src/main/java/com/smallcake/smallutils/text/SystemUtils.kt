package com.smallcake.smallutils.text

import android.os.Build
import android.provider.Settings
import com.smallcake.smallutils.Md5Utils
import com.smallcake.smallutils.SmallUtils

/**
 * https://www.jianshu.com/p/ca869aa2fd72
 */
object SystemUtils {

    /**
     * 获取当前手机系统版本号
     * @return   11
     */
    val systemVersion:String
    get() =  Build.VERSION.RELEASE

    /**
     * 获取当前手机SDK版本
     * @return   30
     */
    val systemSdk: Int
    get() =  Build.VERSION.SDK_INT

    /**
     * 获取手机型号
     * @return  Redmi K30 5G
     */
    val model:String
    get() = Build.MODEL

    /**
     * 获取手机品牌
     * @return  Redmi
     */
    val deviceBrand:String
    get()= Build.BRAND

    /**
     * 获取手机主板名
     * @return  picasso
     */
    val deviceBoard:String
    get()= Build.BOARD


    /**
     * 设备名
     * @return picasso
     */
    val deviceName:String
    get() =  Build.DEVICE


    /**
     * fingerprit 信息
     */
    val fingerprit:String
    get() =  Build.FINGERPRINT

    /**
     * 获取产品名
     * @return  picasso
     */
    val deviceProduct:String
    get() = Build.PRODUCT


    /**
     * 获取AndroidId 刷机或恢复出厂设置会变更
     * @return ca1b6a79867a207a
     */
    val androidId:String
    get()= Settings.System.getString(SmallUtils.context?.contentResolver, Settings.Secure.ANDROID_ID)

    /**
     * 获取手机唯一标识
     * @return 通过MD5加密的字符串
     */
    val imei:String
    get()= Md5Utils.md5(androidId)



}