package com.smallcake.temp.map

import android.content.Context
import android.content.Intent
import android.icu.lang.UCharacter.GraphemeClusterBreak.L
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
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
 * 1.权限申请
1.1在AndroidManifest.xml中配置
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
1.2 代码中申请
XXPermissions.with(activity)
    .permission(listOf(Permission.ACCESS_FINE_LOCATION, Permission.ACCESS_COARSE_LOCATION))
        .request{ _, all->
        if (all) {
            //执行定位相关操作
        }
}

  2.AndroidManifest.xml中配置高德服务和key,(替换自己的key)
    <service android:name="com.amap.api.location.APSService" />
    <meta-data
    android:name="com.amap.api.v2.apikey"
    android:value="937f5f48be1d2b39c1af407482e59ac5" />
 3.打开gps和网络
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

    private fun initView() {
        bind.mapView.map.setOnMapClickListener {
            bind.mapView.map.clear()
            AmapHelper.addMark(this, bind.mapView, it, R.mipmap.ic_location_red)
            AmapHelper.updateBigCenter(bind.mapView, it)
            getAddress(it)
        }

        AmapLocation.with(this).onceLocation(true).listener {
            city = it.city
            latitude = it.latitude
            longitude = it.longitude
            initRecy(LatLng(latitude, longitude))
            val address = it.address
            ldd("当前位置：$address")
            if (address.isNullOrEmpty()) {
                showToast("定位失败，请确定开启了GPS和网络！")
                return@listener
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

        }.start()
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