package com.smallcake.temp.map

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
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
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.temp.MyApplication
import com.smallcake.temp.utils.showToast


/**
 * 官方文档：
 * 获取ak:     https://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-project/ak
 * 显示定位：   https://lbsyun.baidu.com/index.php?title=androidsdk/guide/create-map/location
 *
  1.创建应用，得到AK>>
  https://lbsyun.baidu.com/apiconsole/key#/home

  2.通过Gradle 集成sdk,需要添加mavenCentral仓库地址，选择自己需要的部分
    //地图组件
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Map:7.4.0'
    //检索组件
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Search:7.4.0'
    //工具组件
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Util:7.4.0'
    //步骑行组件
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-BWNavi:7.4.0'
    //基础定位组件
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Location:9.1.8'
    //全量定位组件
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Location_All:9.1.8'
    //驾车导航组件
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-Navi:7.4.0'
    //驾车导航+步骑行导航
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Map-AllNavi:7.4.0'
    //TTS组件
    implementation 'com.baidu.lbsyun:NaviTts:2.5.5'
    //全景组件
    implementation 'com.baidu.lbsyun:BaiduMapSDK_Panorama:2.9.0'

  3.配置AndroidManifest.xml文件，配置密钥，配置定位
      <!-- 百度地图【开始】 -->
        <meta-data
            android:name="com.baidu.lbsapi.API_KEY"
            android:value="开发者 key" />

        <service android:name="com.baidu.location.f"
            android:enabled="true"
            android:process=":remote"/>
     <!--百度地图【结束】 -->

  4.布局中添加
    <com.baidu.mapapi.map.MapView
    android:id="@+id/bmapView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:clickable="true" />

 5.在MyApplication初始化
    //百度地图SDK初始化
   SDKInitializer.initialize(this)

 6.在页面Activity对应的生命周期中添加对应的方法

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("百度地图")
        bind.bmapView.onCreate(this,savedInstanceState)
        //开启地图的定位图层
        bind.bmapView.map.isMyLocationEnabled = true
        //开始定位，并设置到中心点
        BmapHelper.startLocation(this){ location: BDLocation?, locData: MyLocationData ->
            bind.bmapView.map.setMyLocationData(locData)
            BmapHelper.updateCenter(bind.bmapView,LatLng(locData.latitude,locData.longitude))
        }
        //定位图标配置
        BmapHelper.setConfig(bind.bmapView)

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
 7.注意开启对应的ndk支持，在Moudle的build.gradle中的android下的defaultConfig中添加
    ndk {
        abiFilters "armeabi", "armeabi-v7a", "arm64-v8a", "x86","x86_64"
    }
8.注意权限配置和申请：
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
 */
class BaiduMapActivity : BaseBindActivity<ActivityBaiduMapBinding>() {

    private val TAG = "BaiduMapActivity"

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("百度地图")
//        bind.bmapView.onCreate(this,savedInstanceState)
        //开启地图的定位图层
        bind.bmapView.map.isMyLocationEnabled = true
        //定位图标配置
        BmapHelper.setConfig(bind.bmapView)

        XXPermissions.with(this)
            .permission(listOf(Permission.ACCESS_COARSE_LOCATION,Permission.ACCESS_FINE_LOCATION))
            .request(object :OnPermissionCallback{
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all)startLocation()
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    if (never){
                        showToast("需要开启相关定位权限:$permissions")
                        XXPermissions.startPermissionActivity(this@BaiduMapActivity,permissions)
                    }
                }
            })


    }
    fun startLocation(){
        //开始定位，并设置到中心点
        BmapHelper.startLocation(this){ location: BDLocation?, locData: MyLocationData ->
            bind.bmapView.map.setMyLocationData(locData)
            BmapHelper.updateCenter(bind.bmapView, LatLng(locData.latitude,locData.longitude))
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