package com.smallcake.temp.map

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.mapapi.animation.Animation
import com.baidu.mapapi.animation.ScaleAnimation
import com.baidu.mapapi.animation.Transformation
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.geocode.*
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.temp.R
import com.smallcake.temp.map.BmapHelper.onceLocation
import com.smallcake.temp.utils.showToast
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

/**
 *
 * 需要给每个marker设置id,为了实现maker的点击事件
 * 添加一个视图View到地图中
 * @param mapView MapView
 * @param latLng LatLng
 * @param view View
 * @return Marker
 */
fun MapView.addView(latLng:LatLng,view:View):Marker{
    val bimapDesc = BitmapDescriptorFactory.fromView(view)
    val markerOptions = MarkerOptions()
        .position(latLng)
        .icon(bimapDesc)
    return map.addOverlay(markerOptions) as Marker
}
/**
 * 添加一个marker
 * @receiver MapView
 * @param latLng LatLng
 * @param icon 图片资源id
 */
fun MapView.addMarker(latLng:LatLng,icon:Int=R.mipmap.ic_location_red,id: String?=null):Marker{
    val bimapDesc = BitmapDescriptorFactory.fromResource(icon)
    val markerOptions = MarkerOptions()
        .position(latLng) //Marker经纬度
        .animateType(MarkerOptions.MarkerAnimateType.grow)
        .icon(bimapDesc)
    val marker = map.addOverlay(markerOptions) as Marker
    marker.setId(id)
    return marker
}
/**
 * 移动到地图中心点，根据经纬度
 * @receiver MapView
 * @param latLng LatLng
 */
fun MapView.toCenter(latLng:LatLng){
    map.animateMapStatus(MapStatusUpdateFactory.newLatLng(latLng))
}
/**
 * 把一个marker从它本身位置移动到指定的新的经纬度
 * @receiver Marker
 * @param latLng LatLng
 */
fun Marker?.moveTo(latLng:LatLng){
    if (this==null)return
    val transformation = Transformation(this.position, latLng)
    transformation.setDuration(1000)
    this.setAnimation(transformation)
    this.startAnimation()
}
/**
 * 给Marker设置id
 * @param id Int
 */
fun Marker?.setId(id:String?){
    if (TextUtils.isEmpty(id))return
    val bundle = Bundle()
    bundle.putString("id",id)
    this?.extraInfo = bundle
}

/**
 * 百度定位信息转经纬度类
 * @see BDLocation?
 * @see LatLng?
 */
fun BDLocation?.toLatLng():LatLng{
    if (this==null)return LatLng(0.0,0.0)
    return LatLng(this.latitude,this.longitude)
}


/**
 * 百度地图帮助类
一·项目配置
*@see BaiduMapActivity
二·使用帮助
1.单次定位
 * @see onceLocation
 * 注意：
 * 开启地图的定位图层
 * mBaiduMap.setMyLocationEnabled(true);
 * 并在onDestory中关闭
 * mBaiduMap.setMyLocationEnabled(false);
 *
 * 参考：
 * 1.官方定位：https://lbsyun.baidu.com/index.php?title=android-locsdk
 *
 *
 */
object BmapHelper: KoinComponent {
    private  val TAG = "BmapHelper"
    /**
     * 申请定位相关权限
     * @param context Context
     * @param block Function0<Unit>
     */
    fun requestPermiss(context: Context,block:()->Unit){
        XXPermissions.with(context)
            .permission(listOf(Permission.ACCESS_COARSE_LOCATION, Permission.ACCESS_FINE_LOCATION))
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                    if (all)block()
                }

                override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                    super.onDenied(permissions, never)
                    if (never){
                        showToast("需要开启相关定位权限:$permissions")
                        XXPermissions.startPermissionActivity(context,permissions)
                    }
                }
            })
    }

    /**
     * 单次定位，拿到结果后不再监听
     */
    fun onceLocation(context: Context,cb:(BDLocation)->Unit){
        val mLocationClient :LocationClient by inject { parametersOf(context) }
        mLocationClient.registerLocationListener(object :BDAbstractLocationListener(){
            override fun onReceiveLocation(location: BDLocation?) {
                mLocationClient.stop()
                mLocationClient.unRegisterLocationListener(this)
                if (location != null) cb.invoke(location)
            }
        })
        mLocationClient.start()
    }



    /**
     * 将定位信息转换为有方向的导航箭头信息
     * 注意要使用此信息需要：
     * 1.在onCreate中开启 bind.bmapView.map.isMyLocationEnabled = true
     * 2.在onDestory中关闭 bind.bmapView.map.isMyLocationEnabled = false
     * 3.在onCreate中配置显示的信息
     * @see setMyLocationConfig
     * @param location BDLocation? 定位结果
     * @return MyLocationData?
     */
    private fun transMyLocationData(location: BDLocation?):MyLocationData?{
        if (location == null) return null
        return MyLocationData.Builder()
            .accuracy(location.radius) // 此处设置开发者获取到的方向信息，顺时针0-360
            .direction(location.direction).latitude(location.latitude)
            .longitude(location.longitude).build()
    }

    /**
     * 定位图层定位信息显示
     * @param mapView MapView
     * @param latLng LatLng
     * mBaiduMap.setMyLocationEnabled(true);
     * 并在onDestory中关闭
     * mBaiduMap.setMyLocationEnabled(false);
     */
    fun toCenterMyLocation(mapView: MapView, location:BDLocation){
        val myLocationData= transMyLocationData(location)
        mapView.map.setMyLocationData(myLocationData)
    }

    /**
     * 设置定位配置，包含方向信息
     * FOLLOWING:此模式会移动到当前定位信息点
     */
    fun setMyLocationConfig(mapView: MapView){
        //定位图标
        val bitmapDescriptor= BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_red)
        //NORMAL:方向固定旋转图标会一起旋转，COMPASS会显示一个指南针，放大后圈颜色
        val myLocationConfiguration = MyLocationConfiguration(
            MyLocationConfiguration.LocationMode.FOLLOWING,false,null, 0,Color.RED)
        mapView.map.setMyLocationConfiguration(myLocationConfiguration)
    }



    /**
     * 创建动画
     * @return Animation
     * //设置动画
     * marker.setAnimation(createAnim())
     * marker.startAnimation()
     */
    fun createAnim():Animation{
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
    fun scaleAnim1_2f():Animation{
        val scaleAnimation = ScaleAnimation(1f, 1.2f)
        scaleAnimation.setDuration(300) // 动画播放时间
        return scaleAnimation
    }
    fun scaleAnim1_2to1f():Animation{
        val scaleAnimation = ScaleAnimation(1.2f, 1f)
        scaleAnimation.setDuration(300) // 动画播放时间
        return scaleAnimation
    }

    /**
     * 发起逆地理编码请求
     * 务必在Activity的onDestroy函数里，调用MapView和GeoCoder的销毁方法，否则会有内存泄露。
     * @param latLng
     */
    fun reverseRequest(lifecycleOwner: LifecycleOwner,latLng: LatLng,cb:(ReverseGeoCodeResult)->Unit) {
        val reverseGeoCodeOption: ReverseGeoCodeOption = ReverseGeoCodeOption().location(latLng)
            .newVersion(1)
            .radius(100)
            .pageNum(1)
        val mGeoCoder = GeoCoder.newInstance()
        mGeoCoder.setOnGetGeoCodeResultListener(object : OnGetGeoCoderResultListener{
            override fun onGetGeoCodeResult(result: GeoCodeResult?) {
                Log.e(TAG,"result:$result")
            }
            override fun onGetReverseGeoCodeResult(result: ReverseGeoCodeResult) {
                Log.e(TAG,"ReverseResult:$result")
                cb.invoke(result)
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
     * @param coord_type     坐标类型  允许的值为bd09ll、bd09mc、gcj02、wgs84。
     * bd09ll表示百度经纬度坐标，bd09mc表示百度墨卡托坐标，gcj02表示经过国测局加密的坐标，wgs84表示gps获取的坐标
     * @param mode           导航类型导航模式 可选transit（公交）、 driving（驾车）、 walking（步行）和riding（骑行）.
     * @param src            必选参数，格式为：appName  不传此参数，不保证服务
     * @param context Context
     * @param destinationLat String 目的地维度
     * @param destinationLng String 目的地经度
     * @param coord_type String
     * @param mode String
     * @param src String  例如 andr.baidu.openAPIdemo
     * 参考
     * https://lbsyun.baidu.com/index.php?title=uri/api/android
     */
    fun openBmapNavi(context: Context,destinationLat: Double,destinationLng: Double) {
        val intent = Intent()
        intent.data = Uri.parse(
            "baidumap://map/direction?destination=" +
                    destinationLat + "," + destinationLng + "&coord_type=bd09mc" +
                    "&mode=driving" + "&src=andr.baidu.openAPIdemo"  + "#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end"
        )
        context.startActivity(intent)
    }

}