package com.smallcake.temp.map

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.animation.Animation
import com.baidu.mapapi.animation.ScaleAnimation
import com.baidu.mapapi.animation.Transformation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.geocode.*
import com.smallcake.smallutils.px
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
        option.isOpenGps = true //打开gps
        option.setCoorType("bd09ll") // 设置坐标类型
        option.setScanSpan(0)//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
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
     * 设置定位配置，包含方向信息
     */
    fun setConfig(mapView: MapView){
        //定位图标
        val bitmapDescriptor= BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_red)
        //NORMAL:方向固定旋转图标会一起旋转，COMPASS会显示一个指南针，放大后圈颜色
        val myLocationConfiguration = MyLocationConfiguration(
            MyLocationConfiguration.LocationMode.FOLLOWING,true,bitmapDescriptor, 0,Color.RED)
        mapView.map.setMyLocationConfiguration(myLocationConfiguration)
    }

    /**
     * 隐藏放大缩小按钮
     * @param mMapView MapView
     */
    fun setZoomControlsGone(mMapView: MapView) {
        mMapView.showZoomControls(false)
    }

    /**
     * 添加一个标记Marker
     * @param mapView MapView
     * @param latLng LatLng
     * @return Marker
     */
    fun addMarker(mapView: MapView,latLng:LatLng,icon:Int=R.mipmap.ic_location_red):Marker{
        val bimapDesc = BitmapDescriptorFactory.fromResource(icon)
        val markerOptions = MarkerOptions()
            .position(latLng) //Marker经纬度
            .animateType(MarkerOptions.MarkerAnimateType.grow)
            .icon(bimapDesc)
        return mapView.map.addOverlay(markerOptions) as Marker
    }

    /**
     *
     * 需要给每个marker设置id,为了实现maker的点击事件
     *
    val bundle = Bundle()
    bundle.putInt("id",10086)
    markerOptions.extraInfo(bundle)
    //地图设置点击Marker事件
    bind.bmapView.map.setOnMarkerClickListener {marker->
    val bundle = marker.extraInfo
    val id = bundle.getInt("id")
    if (id==10086){
    marker.remove()
    }
    false
    }
     * 添加一个视图View到地图中
     * @param mapView MapView
     * @param latLng LatLng
     * @param view View
     * @return Marker
     */
    fun addView(mapView: MapView,latLng:LatLng,view: View):Marker{
        val bimapDesc = BitmapDescriptorFactory.fromView(view)
        val markerOptions = MarkerOptions()
            .position(latLng) //Marker经纬度
            .icon(bimapDesc)
        return mapView.map.addOverlay(markerOptions) as Marker
    }

    /**
     * 创建动画
     * @return Animation
     * //设置动画
     * marker.setAnimation(createAnim())
     * marker.startAnimation()
     */
    fun createAnim(): Animation {
        val scaleAnimation = ScaleAnimation(1f, 1.2f, 1f)
        scaleAnimation.setDuration(1000) // 动画播放时间
        scaleAnimation.setRepeatCount(1000)
        scaleAnimation.setRepeatMode(Animation.RepeatMode.RESTART)
        return scaleAnimation
    }

    /**
     * 放大1.2倍动画
     * @return Animation
     */
    fun scaleAnim1_2f(): Animation {
        val scaleAnimation = ScaleAnimation(1f, 1.2f)
        scaleAnimation.setDuration(300) // 动画播放时间
        return scaleAnimation
    }
    fun scaleAnim1_2to1f(): Animation {
        val scaleAnimation = ScaleAnimation(1.2f, 1f)
        scaleAnimation.setDuration(300) // 动画播放时间
        return scaleAnimation
    }

    /**
     * 发起逆地理编码请求
     * 务必在Activity的onDestroy函数里，调用MapView和GeoCoder的销毁方法，否则会有内存泄露。
     * @param latLng
     */
    fun reverseRequest(lifecycleOwner: LifecycleOwner, latLng: LatLng) {
        val reverseGeoCodeOption: ReverseGeoCodeOption = ReverseGeoCodeOption().location(latLng)
            .newVersion(1)
            .radius(100)
            .pageNum(1)
        val mGeoCoder = GeoCoder.newInstance()
        mGeoCoder.setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener {
            override fun onGetGeoCodeResult(result: GeoCodeResult?) {
                Log.e(TAG,"result:$result")
            }
            override fun onGetReverseGeoCodeResult(result: ReverseGeoCodeResult?) {
                Log.e(TAG,"ReverseResult:$result")
            }
        })
        mGeoCoder.reverseGeoCode(reverseGeoCodeOption)
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestory(){
                mGeoCoder.destroy()
            }
        })
    }

    /**
     * 把一个标记从一个位置移动到另一个位置
     * @param marker Marker
     * @param latLng LatLng
     */
    fun transMove(marker: Marker,latLng: LatLng){
        val transformation = Transformation(marker.position, latLng)
        transformation.setDuration(1000)
        marker.setAnimation(transformation)
        marker.startAnimation()
    }

    /**
     * 给Marker设置id
     * @param marker Marker
     * @param id Int
     */
    fun setMarkerId(marker: Marker,id:Int){
        val bundle = Bundle()
        bundle.putInt("id",id)
        marker.extraInfo = bundle
    }

    /**
     * 显示InfoWindow
     * @param mapView MapView
     * @param latLng LatLng
     *
     * val view = LayoutInflater.from(context).inflate(R.layout.item_event_marker,null)
     * val infoWindow = BmapHelper.showInfoWindow(bind.bmapView,marker.position,view)
     */
    fun showInfoWindow(mapView: MapView,latLng: LatLng,view: View):InfoWindow{
        val infoWindow = InfoWindow(view,latLng, 100.px)
        mapView.map.showInfoWindow(infoWindow)
        return infoWindow
    }


}