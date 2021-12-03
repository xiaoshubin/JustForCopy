package com.smallcake.temp.utils

/**
 * 坐标转换工具类
 * WGS84: Google Earth采用，Google Map中国范围外使用
 * GCJ02: 火星坐标系，中国国家测绘局制定的坐标系统，由WGS84加密后的坐标。Google Map中国和搜搜地图使用，高德
 * BD09:百度坐标，GCJ02加密后的坐标系
 * 搜狗坐标系，图吧坐标等，估计也是在GCJ02基础上加密而成的
 */
object GPSConverterUtils {
    const val BAIDU_LBS_TYPE = "bd09ll"
    var pi = 3.1415926535897932384626
    var a = 6378245.0
    var ee = 0.00669342162296594323

    /**
     * 84 to 火星坐标系 (GCJ-02) World Geodetic System ==> Mars Geodetic System
     * @param lat
     * @param lon
     */
    fun gps84_To_Gcj02(lat: Double, lon: Double): GPS? {
        if (outOfChina(lat, lon)) {
            return null
        }
        var dLat = transformLat(lon - 105.0, lat - 35.0)
        var dLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * pi
        var magic = Math.sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = Math.sqrt(magic)
        dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
        dLon = dLon * 180.0 / (a / sqrtMagic * Math.cos(radLat) * pi)
        val mgLat = lat + dLat
        val mgLon = lon + dLon
        return GPS(mgLat, mgLon)
    }

    /**
     * * 火星坐标系 (GCJ-02) to 84
     * @param lon
     * @param lat
     * @return
     */
    fun gcj_To_Gps84(lat: Double, lon: Double): GPS {
        val gps = transform(lat, lon)
        val lontitude = lon * 2 - gps.lon
        val latitude = lat * 2 - gps.lat
        return GPS(latitude, lontitude)
    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法 将 GCJ-02 坐标转换成 BD-09 坐标
     *
     * @param gg_lat
     * @param gg_lon
     */
    fun gcj02_To_Bd09(gg_lat: Double, gg_lon: Double): GPS {
        val z = Math.sqrt(gg_lon * gg_lon + gg_lat * gg_lat) + 0.00002 * Math.sin(
            gg_lat * pi
        )
        val theta = Math.atan2(gg_lat, gg_lon) + 0.000003 * Math.cos(gg_lon * pi)
        val bd_lon = z * Math.cos(theta) + 0.0065
        val bd_lat = z * Math.sin(theta) + 0.006
        return GPS(bd_lat, bd_lon)
    }

    /**
     * * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换算法  将 BD-09 坐标转换成GCJ-02 坐标
     * @param bd_lat
     * @param bd_lon
     */
    fun bd09_To_Gcj02(bd_lat: Double, bd_lon: Double): GPS {
        val x = bd_lon - 0.0065
        val y = bd_lat - 0.006
        val z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi)
        val theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi)
        val gg_lon = z * Math.cos(theta)
        val gg_lat = z * Math.sin(theta)
        return GPS(gg_lat, gg_lon)
    }

    /**
     * (BD-09)-->84
     * @param bd_lat
     * @param bd_lon
     * @return
     */
    fun bd09_To_Gps84(bd_lat: Double, bd_lon: Double): GPS {
        val gcj02 = bd09_To_Gcj02(bd_lat, bd_lon)
        return gcj_To_Gps84(
            gcj02.lat,
            gcj02.lon
        )
    }

    /**
     * is or not outOfChina
     * @param lat
     * @param lon
     * @return
     */
    fun outOfChina(lat: Double, lon: Double): Boolean {
        if (lon < 72.004 || lon > 137.8347) return true
        return if (lat < 0.8293 || lat > 55.8271) true else false
    }

    fun transform(lat: Double, lon: Double): GPS {
        if (outOfChina(lat, lon)) {
            return GPS(lat, lon)
        }
        var dLat = transformLat(lon - 105.0, lat - 35.0)
        var dLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * pi
        var magic = Math.sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = Math.sqrt(magic)
        dLat = dLat * 180.0 / (a * (1 - ee) / (magic * sqrtMagic) * pi)
        dLon = dLon * 180.0 / (a / sqrtMagic * Math.cos(radLat) * pi)
        val mgLat = lat + dLat
        val mgLon = lon + dLon
        return GPS(mgLat, mgLon)
    }

    fun transformLat(x: Double, y: Double): Double {
        var ret =
            -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x))
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0
        return ret
    }

    fun transformLon(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + (0.1
                * Math.sqrt(Math.abs(x)))
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(
            x / 30.0
                    * pi
        )) * 2.0 / 3.0
        return ret
    }
}
class GPS(var lat: Double, var lon: Double) {
    override fun toString(): String {
        return "lat:$lat,lon:$lon"
    }
}