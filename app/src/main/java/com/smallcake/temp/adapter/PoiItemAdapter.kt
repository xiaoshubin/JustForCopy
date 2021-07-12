package com.smallcake.temp.adapter

import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.PoiItem
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.smallcake.temp.R
import com.smallcake.temp.databinding.ItemPoiBinding
import com.smallcake.temp.utils.ldd

/**
 * Date:2021/6/26 14:14
 * Author:SmallCake
 * Desc:
 **/
class PoiItemAdapter(private val latLngCurrent:LatLng): BaseQuickAdapter<PoiItem, BaseDataBindingHolder<ItemPoiBinding>>(R.layout.item_poi) {

    override fun convert(holder: BaseDataBindingHolder<ItemPoiBinding>, item: PoiItem) {
        holder.dataBinding?.item=item
        val province = item.provinceName
        val city = item.cityName
        val address = item.adName
        val itemStr = item.toString()
        ldd(item.toString())

        holder.setText(R.id.tv_title,"$province$city$address$itemStr")


        val itemLatlng = LatLng(item.latLonPoint.latitude,item.latLonPoint.longitude)
        val distance = AMapUtils.calculateLineDistance(latLngCurrent,itemLatlng).toInt()
        var disStr="0m"
        if (distance>1000){
            val km = distance/1000
            val m = distance%1000
            disStr = "${km}km${m}m"
        }else disStr="${distance}m"

        //距离 + 位置
        holder.setText(R.id.tv_distance_snippet,"$disStr | "+item.snippet)

    }
}