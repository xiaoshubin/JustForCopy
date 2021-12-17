package com.smallcake.temp.map

import android.graphics.Color
import android.os.Bundle
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityBaiduMapBinding
import com.baidu.mapapi.map.MyLocationData
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BitmapDescriptor
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MyLocationConfiguration
import com.luck.picture.lib.tools.BitmapUtils
import com.smallcake.temp.R
import com.baidu.mapapi.map.MapStatusUpdateFactory

import com.baidu.mapapi.map.MapStatusUpdate
import com.baidu.mapapi.model.LatLng


/**
 * 官方文档：
 * https://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-project/ak
 *
 * 显示定位：https://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-map/location
 *
 * 1.创建应用，得到AK>>
 * https://lbsyun.baidu.com/apiconsole/key#/home
 *
 * 2.通过Gradle 集成sdk,需要添加mavenCentral仓库地址，选择自己需要的部分
地图组件
implementation 'com.baidu.lbsyun:BaiduMapSDK_Map:7.4.0'
检索组件
implementation 'com.baidu.lbsyun:BaiduMapSDK_Search:7.4.0'
工具组件
implementation 'com.baidu.lbsyun:BaiduMapSDK_Util:7.4.0'
步骑行组件
implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-BWNavi:7.4.0'
基础定位组件
implementation 'com.baidu.lbsyun:BaiduMapSDK_Location:9.1.8'
全量定位组件
implementation 'com.baidu.lbsyun:BaiduMapSDK_Location_All:9.1.8'
驾车导航组件
implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-Navi:7.4.0'
驾车导航+步骑行导航
implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-AllNavi:7.4.0'
TTS组件
implementation 'com.baidu.lbsyun:NaviTts:2.5.5'
全景组件
implementation 'com.baidu.lbsyun:BaiduMapSDK_Panorama:2.9.0'

 * 3.配置AndroidManifest.xml文件，配置密钥，配置定位
    <meta-data
        android:name="com.baidu.lbsapi.API_KEY"
        android:value="开发者 key" />

    <service android:name="com.baidu.location.f"
        android:enabled="true"
        android:process=":remote"/>
 *
 * 4.布局中添加
    <com.baidu.mapapi.map.MapView
    android:id="@+id/bmapView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clickable="true" />

 5.在MyApplication初始化
   SDKInitializer.initialize(this)

 6.在页面Activity对应的生命周期中添加对应的方法
    override fun onResume() {
        super.onResume()
        bind.bmapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        bind.bmapView.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        bind.bmapView.onDestroy()
    }

 7.定位的生命周期管理


 */
class BaiduMapActivity : BaseBindActivity<ActivityBaiduMapBinding>() {

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("百度地图")
        //开启地图的定位图层
        bind.bmapView.map.isMyLocationEnabled = true
        //通过LocationClient发起定位
        val mLocationClient = LocationClient(this)
        val option = LocationClientOption()
        option.isOpenGps = true // 打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(1000)
        mLocationClient.locOption = option
        //注册监听器
        mLocationClient.registerLocationListener(bdLocationListener)
        //开启地图定位图层
        mLocationClient.start()



        //定位图标配置
        val bitmapDescriptor=BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher)
        val myLocationConfiguration = MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL,true,bitmapDescriptor,Color.BLUE,Color.RED)
        bind.bmapView.map.setMyLocationConfiguration(myLocationConfiguration)
    }

    private val bdLocationListener =  object :BDAbstractLocationListener(){
        override fun onReceiveLocation(location: BDLocation?) {
            if (location == null) return
            val locData = MyLocationData.Builder()
                .accuracy(location.radius) // 此处设置开发者获取到的方向信息，顺时针0-360
                .direction(location.direction).latitude(location.latitude)
                .longitude(location.longitude).build()
            //设置定位数据
            bind.bmapView.map.setMyLocationData(locData)
            //更新到最近定位位置
            val latLng = LatLng(locData.latitude,locData.longitude)
            bind.bmapView.map.setMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
        }

    }

    override fun onResume() {
        super.onResume()
        bind.bmapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        bind.bmapView.onPause()
    }
    override fun onDestroy() {
        super.onDestroy()
        bind.bmapView.map.isMyLocationEnabled = false
        bind.bmapView.onDestroy()
    }
}