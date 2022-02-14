package com.smallcake.temp.map

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.mapcore.util.it
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.smallcake.smallutils.KeyboardUtils
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.adapter.PoiItemAdapter
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityLocationMapBinding
import com.smallcake.temp.utils.ldd
import com.smallcake.temp.utils.showToast


/**
 * 定位当前位置显示

1.【高德sdk引入】

    //高德定位功能
    implementation 'com.amap.api:location:5.6.2'
    //高德地图
    implementation 'com.amap.api:3dmap:9.0.0'
    //高德检索
    implementation 'com.amap.api:search:8.1.0'


2.【高德sdk密钥配置】

    <!--AndroidManifest.xml中配置高德服务和key,(替换自己的key)-->
    <service android:name="com.amap.api.location.APSService" />
    <meta-data
    android:name="com.amap.api.v2.apikey"
    android:value="替换自己的key" />



3.权限申请和gps开启

    3.1在AndroidManifest.xml中配置
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    3.2 代码中申请
    XXPermissions.with(activity)
        .permission(listOf(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION))
            .request{ _, all->
            if (all) {
                //执行定位相关操作
            }
    }
    3.3.打开gps和网络
    val gpsIsOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) //GPS定位是否打开
    未打开提示去打开


高德地图黑屏：神奇的bug
最开始以为是kotlin的问题，用了java写页面，问题依旧，最后发现三个条件导致页面黑屏，a.文本数据设置中包含了特殊字符：()或· b.使用了高德地图mapView.onCreate(savedInstanceState)，c.红米k30手机（目前测试其他手机正常）
- 单独使用地图，没有问题（说明高德没有）
- 单独对文本设置包含了特殊字符的文本没有问题（说明可以设置特殊字符的文本：重庆市渝中区菜袁路渝中区旭庆·江湾国际花都(菜袁路西)）
- 不使用红米手机K30,设置了地图也设置了包含了特殊字符的文本也没问题
最终结论：手机有问题

解决方案一：去掉特殊文本中的特殊字符：重庆市渝中区菜袁路渝中区旭庆江湾国际花都菜袁路西（不合理，显示的内容缺少）
解决方案二：不使用红米手机（测试不干）
解决方案三：猜想黑屏是渲染导致，那么可以延迟处理其中一方，地图因为要跟随onCreate的创建而绑定，没法弄，于是考虑延迟300豪秒来设置特殊文本到文本控件中

从地图8.1.0版本起对旧版本SDK不兼容，请务必确保调用SDK任何接口前先调用更新隐私合规updatePrivacyShow、updatePrivacyAgree两个接口
 *
 */
class LocationMapActivity : BaseBindActivity<ActivityLocationMapBinding>() {
    private val TAG = "LocationMapActivity"
    private var city = "重庆"
    private var latitude = 0.0
    private var longitude = 0.0
    private var mAdapter: PoiItemAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bind.mapView.onCreate(savedInstanceState)
        bar.setTitle("地址选择")
        XXPermissions.with(this)
            .permission(listOf(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION))
            .request{ _, all->
                if (all) initView()
            }

    }

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        bind.mapView.onSaveInstanceState(outState)
//    }

    private fun initView() {
        bind.mapView.map.setOnMapClickListener {
            bind.mapView.map.clear()
            AmapHelper.addMark(this, bind.mapView, it, R.mipmap.ic_location_red)
            AmapHelper.updateBigCenter(bind.mapView, it)
            getAddress(it)
        }
         AmapLocation.onceLocation(this){
            city = it.city
            latitude = it.latitude
            longitude = it.longitude
            initRecy(LatLng(latitude, longitude))
            val address = it.address
            ldd("当前位置：$address")
            if (address.isNullOrEmpty()) {
                showToast("定位失败，请确定开启了GPS和网络！")
               return@onceLocation
            }
            AmapHelper.addDragMark(
                this,
                bind.mapView,
                LatLng(latitude, longitude),
                R.mipmap.ic_location_red
            )

            AmapHelper.updateCenter(bind.mapView, LatLng(latitude, longitude))

            bind.mapView.map.apply {
                setOnMarkerClickListener { marker ->
                    showToast("点击了${marker.title}")
                    if (marker?.isInfoWindowShown == true) {
                        marker.hideInfoWindow()
                    } else {
                        marker?.showInfoWindow()
                    }
                    false
                }

                setOnMarkerDragListener(object : AMap.OnMarkerDragListener {
                    override fun onMarkerDragStart(p0: Marker?) {
                    }

                    override fun onMarkerDrag(p0: Marker?) {
                    }

                    override fun onMarkerDragEnd(marker: Marker?) {
                        marker?.position?.let { latlng ->
                            getAddress(latlng)
                        }

                    }

                })

            }
            poiSearch(it.poiName)

        }
        bind.searchView.setButtonClick {
            KeyboardUtils.hintKeyboard(this@LocationMapActivity)
            poiSearch(it)
        }
    }


    /**
     * 根据经纬度得到地址
     */
    fun getAddress(latLng: LatLng){
        val query = RegeocodeQuery(LatLonPoint(latLng.latitude,latLng.longitude), 500f, GeocodeSearch.AMAP)
        val geo = GeocodeSearch(this)
        geo.getFromLocationAsyn(query)
        geo.setOnGeocodeSearchListener(object :GeocodeSearch.OnGeocodeSearchListener{
            override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
                if (rCode == 1000) {
                    if (result?.regeocodeAddress != null
                        && result.regeocodeAddress.formatAddress != null) {
                        val addressName = result.regeocodeAddress.formatAddress
                        Log.e(TAG,"逆地理编码回调  得到的地址：$addressName"  )
                        poiSearch(addressName)


                    }
                }

            }

            override fun onGeocodeSearched(result: GeocodeResult?, rCode: Int) {
            }

        })
    }



    private fun initRecy(latLng: LatLng) {
        mAdapter = PoiItemAdapter(latLng)
        bind.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@LocationMapActivity)
            adapter = mAdapter
        }
        mAdapter?.setOnItemClickListener { adapter, view, potion ->
            val item = adapter.getItem(potion) as PoiItem
            val intent = Intent().apply {
                putExtra("poiItem", item)
            }
            setResult(RESULT_OK, intent)
            finish()

//            XPopup.Builder(this)
//                .asConfirm("", "确定发送当前定位?") {
//                    val intent = Intent().apply {
//                        putExtra("poiItem", item)
//                    }
//                    setResult(RESULT_OK, intent)
//                    finish()
//                }.show()

        }
    }

    /**
     * 关键字检索
     * @param keyWork String
     */
    private fun poiSearch(keyWork: String){
        val query = PoiSearch.Query(keyWork, "", "")
        query.pageSize=10
        val poiSearch =  PoiSearch(this, query)
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiSearched(p0: PoiResult?, p1: Int) {

                p0?.apply {
//                    pois[0]?.let {
//                        val latLng = LatLng(it.latLonPoint.latitude,it.latLonPoint.longitude)
//                        bind.mapView.map.clear()
//                        AmapHelper.addMark(
//                            this@LocationMapActivity,
//                            bind.mapView,
//                            latLng,
//                            R.mipmap.ic_red_location
//                        )
//                        AmapHelper.updateBigCenter(bind.mapView, latLng)
//                    }
                    mAdapter?.setList(pois)
                }
            }

            override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {
                ldd("onPoiItemSearched:$p1")
            }

        })
        poiSearch.searchPOIAsyn()
    }



    override fun onResume() {
        super.onResume()
        bind.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        bind.mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        bind.mapView.onDestroy()
    }


}