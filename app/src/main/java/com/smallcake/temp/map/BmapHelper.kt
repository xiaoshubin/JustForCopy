package com.smallcake.temp.map

import android.content.Context
import android.graphics.Color
import android.util.Log
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.smallcake.temp.R

/**
 * 百度地图帮助类
 * 注意：
 * 1.要有定位指示，需要开启我的定位，bind.bmapView.map.isMyLocationEnabled = true
 * 并在页面结束onDestroy时关闭bind.bmapView.map.isMyLocationEnabled = false
 * 拿到定位数据BDLocation后转换MyLocationData并设置 bind.bmapView.map.setMyLocationData(locData)
 *
 *
 * 参考：
 * 1.定位：https://lbsyun.baidu.com/index.php?title=android-locsdk
 *
 * 问题1：
 * 只定位一次，加了服务并配置了定位间隔>1000ms
 * 原因：换个手机，重启
 *
 * 问题2：定位经纬度出现：4.9E-324
 * 原因：权限问题，so库错误或没有配置，申请AK时输入的sha错误
  <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
 <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
 <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

 *
 */
object BmapHelper {
    private  val TAG = "BmapHelper"
    /**
     * 开始定位
     * @param context Context
     * @param BDLocation       百度定位数据
     * @param MyLocationData   定位转换的数据
     *
     */
    fun startLocation(context: Context,cb:(location: BDLocation?,locationData:MyLocationData)->Unit){
        //通过LocationClient发起定位
        val mLocationClient = LocationClient(context)
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(2000)//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        option.isLocationNotify = true//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy//基础
        option.setIgnoreKillProcess(true)//关闭后不杀死当前定位服务
        option.setIsNeedAddress(true)
        mLocationClient.locOption = option
        //注册监听器
        mLocationClient.registerLocationListener(object :BDAbstractLocationListener(){
            override fun onReceiveLocation(location: BDLocation?) {

                Log.e(TAG,"定位信息：[lat:${location?.latitude},lng:${location?.longitude}]")
                if (location == null) return
                val locData = MyLocationData.Builder()
                    .accuracy(location.radius) // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.direction).latitude(location.latitude)
                    .longitude(location.longitude).build()
                cb.invoke(location,locData)
            }
        })
        //开启地图定位图层
        mLocationClient.start()
    }

    /**
     * 根据经纬度，更新此坐标到地图中心点
     * @param baiduMap BaiduMap
     * @param latLng LatLng
     */
    fun updateCenter(mapView: MapView, latLng:LatLng){
        mapView.map.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
    }

    /**
     * 设置定位配置
     */
    fun setConfig(mapView: MapView){
        //定位图标
        val bitmapDescriptor= BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_red)
        //NORMAL:方向固定旋转图标会一起旋转，COMPASS会显示一个指南针，放大后圈颜色
        val myLocationConfiguration = MyLocationConfiguration(
            MyLocationConfiguration.LocationMode.FOLLOWING,true,bitmapDescriptor, 0,Color.RED)
        mapView.map.setMyLocationConfiguration(myLocationConfiguration)
    }



}