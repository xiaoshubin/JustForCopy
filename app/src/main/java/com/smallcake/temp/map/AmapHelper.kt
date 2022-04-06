package com.smallcake.temp.map

import android.graphics.BitmapFactory
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.View
import com.amap.api.fence.GeoFenceClient
import com.amap.api.location.DPoint
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.*
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder
import java.util.*

object AmapHelper {
    private const val TAG = "AmapHelper"

    /**
     * 设置显示定位蓝点
     *
     * @param mapView 地图
     */
    fun showBluePoint(mapView: MapView) {
        val myLocationStyle = MyLocationStyle() //初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.radiusFillColor(0) //圆形颜色去掉
        myLocationStyle.strokeWidth(0f) //圆圈边框去掉
        myLocationStyle.interval((1000 * 10).toLong()) //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        mapView.map.myLocationStyle = myLocationStyle //设置定位蓝点的Style
        mapView.map.uiSettings.isMyLocationButtonEnabled = false //设置默认定位按钮是否显示，非必需设置。
        mapView.map.isMyLocationEnabled = true // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
    }

    /**
     * 更新地图中心
     *
     * @param mapView
     * @param latLng
     */
    fun updateCenter(mapView: MapView, latLng: LatLng?) {
        //target - 目标位置的屏幕中心点经纬度坐标。
        //zoom - 目标可视区域的缩放级别。
        //tilt - 目标可视区域的倾斜度，以角度为单位。
        //bearing - 可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度。
        val mCameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, 12f, 0f, 0f))
        //mapView.getMap().animateCamera(mCameraUpdate);
        mapView.map.animateCamera(mCameraUpdate)
    }

    fun updateBigCenter(mapView: MapView, latLng: LatLng?) {
        //target - 目标位置的屏幕中心点经纬度坐标。
        //zoom - 目标可视区域的缩放级别。
        //tilt - 目标可视区域的倾斜度，以角度为单位。
        //bearing - 可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度。
        val mCameraUpdate = CameraUpdateFactory.newCameraPosition(CameraPosition(latLng, 15f, 0f, 0f))
        //mapView.getMap().animateCamera(mCameraUpdate);
        mapView.map.animateCamera(mCameraUpdate)
    }

    /**
     * 添加Mark
     *
     * @param context 上下文
     * @param mapView 地图
     * @param latLng  经纬度
     * @param view    视图
     * @return
     */
    fun addMarkView(context: Context?, mapView: MapView, latLng: LatLng?, view: View?): Marker {
        val markerOption = MarkerOptions()
        markerOption.position(latLng)
        //        markerOption.rotateAngle(0);
        markerOption.icon(BitmapDescriptorFactory.fromView(view))
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.isFlat = false //设置marker平贴地图效果
        return mapView.map.addMarker(markerOption)
    }

    /**
     * 添加Mark
     *
     * @param context 上下文
     * @param mapView 地图
     * @param latLng  经纬度
     * @param ico     图标
     * @return
     */
    fun addMark(context: Context, mapView: MapView, latLng: LatLng?, ico: Int): Marker? {
        try {
            val markerOption = MarkerOptions()
            markerOption.position(latLng)
            markerOption.icon(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(
                        context.resources,
                        ico
                    )
                )
            )
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.isFlat = false //设置marker平贴地图效果
            return mapView.map.addMarker(markerOption)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 添加可拖拽的Mark
     *
     * @param context 上下文
     * @param mapView 地图
     * @param latLng  经纬度
     * @param ico     图标
     * @return
     */
    fun addDragMark(context: Activity, mapView: MapView, latLng: LatLng?, ico: Int): Marker? {
        try {
            val markerOption = MarkerOptions()
            markerOption.draggable(true)
            markerOption.icon(
                BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(
                        context.resources,
                        ico
                    )
                )
            ).anchor(0.5f, 0.7f)
            val marker = mapView.map.addMarker(markerOption)
            marker.position = latLng
            mapView.invalidate()
            return marker
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 绘制直线
     * @param mapView
     * @param from
     * @param to
     */
    fun drawLine(mapView: MapView, from: LatLng?, to: LatLng?) {
        //绘制线段
        val options = PolylineOptions()
        options.addAll(Arrays.asList(from, to))
        options.width(16f)
        options.color(Color.parseColor("#F3A516"))
        mapView.map.addPolyline(options)
    }

    /**
     * 是否安装高德地图
     *
     * @return
     */
    val isInstallAmap: Boolean
        get() = File("/data/data/com.autonavi.minimap").exists()

    /**
     * 打开高德地图导航功能
     *
     * @param context
     * @param originLat       起点纬度
     * @param originLng       起点经度
     * @param originName      起点名称 可不填
     * @param destinationLat  终点纬度
     * @param destinationLng  终点经度
     * @param destinationName 终点名称 必填
     */
    fun startNavigation(
        context: Context,
        originLat: Double,
        originLng: Double,
        originName: String?,
        destinationLat: Double,
        destinationLng: Double,
        destinationName: String?
    ) {
        var uriString: String? = null
        val builder = StringBuilder("amapuri://route/plan?sourceApplication=maxuslife")
        if (originLat != 0.0) {
            builder.append("&sname=").append(originName)
                .append("&slat=").append(originLat)
                .append("&slon=").append(originLng)
        }
        builder.append("&dlat=").append(destinationLat)
            .append("&dlon=").append(destinationLng)
            .append("&dname=").append(destinationName)
            .append("&dev=0")
            .append("&t=0")
        uriString = builder.toString()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setPackage("com.autonavi.minimap")
        intent.data = Uri.parse(uriString)
        context.startActivity(intent)
    }

    /**
     * 打开高德地图导航功能
     * 默认以自己位置为起点
     * @param destinationLat  终点纬度
     * @param destinationLng  终点经度
     * @param destinationName 终点名称 必填
     */
    fun startNavigation(
        context: Context,
        destinationLat: Double,
        destinationLng: Double,
        destinationName: String?
    ) {
        var uriString: String? = null
        val builder = StringBuilder("amapuri://route/plan?sourceApplication=maxuslife")
        builder.append("&dlat=").append(destinationLat)
            .append("&dlon=").append(destinationLng)
            .append("&dname=").append(destinationName)
            .append("&dev=0")
            .append("&t=0")
        uriString = builder.toString()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setPackage("com.autonavi.minimap")
        intent.data = Uri.parse(uriString)
        context.startActivity(intent)
    }

    /**
     * 添加View Mark
     *
     * @param map    地图
     * @param latLng 经纬度
     * @param view   控件
     */
    fun addViewMarkers(map: MapView, latLng: LatLng?, view: View?): Marker {
        val options = MarkerOptions()
        options.position(latLng)
        options.icon(BitmapDescriptorFactory.fromView(view))
        return map.map.addMarker(options)
    }

    /**
     * 添加电子围栏
     *
     * @param context 上下文对象
     * @param points  围栏
     *
     * 设置希望侦测的围栏触发行为，默认只侦测用户进入围栏的行为
     * GEOFENCE_IN 进入地理围栏
     * GEOFENCE_OUT 退出地理围栏
     * GEOFENCE_STAYED 停留在地理围栏内10分钟
     */
    fun addGeoFence(context: Context?, points: List<DPoint?>?) {
        val mGeoFenceClient = GeoFenceClient(context)
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN or GeoFenceClient.GEOFENCE_OUT or GeoFenceClient.GEOFENCE_STAYED)
        mGeoFenceClient.addGeoFence(points, "自有业务ID")
    }

    /**
     * 绘制多边形电子围栏
     * @param mapView
     * @param latLngs
     */
    fun drawGeoFence(mapView: MapView, latLngs: List<LatLng?>?) {
        val polygonOptions = PolygonOptions()
        polygonOptions.strokeColor(Color.parseColor("#8BDAFD"))
        polygonOptions.fillColor(Color.parseColor("#8098DCF7"))
        mapView.map.addPolygon(polygonOptions.addAll(latLngs))
    }
}