package com.smallcake.temp.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.amap.api.fence.GeoFenceClient;
import com.amap.api.location.DPoint;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.smallcake.temp.MyApplication;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class AmapHelper {
    private static final String TAG = "AmapHelper";
    /**
     * 设置显示定位蓝点
     *
     * @param mapView 地图
     */
    public static void showBluePoint(MapView mapView) {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.radiusFillColor(0);//圆形颜色去掉
        myLocationStyle.strokeWidth(0);//圆圈边框去掉
        myLocationStyle.interval(1000 * 10); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
        mapView.getMap().setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        mapView.getMap().getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        mapView.getMap().setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
    }

    /**
     * 更新地图中心
     *
     * @param mapView
     * @param latLng
     */
    public static void updateCenter(MapView mapView, LatLng latLng) {
        //target - 目标位置的屏幕中心点经纬度坐标。
        //zoom - 目标可视区域的缩放级别。
        //tilt - 目标可视区域的倾斜度，以角度为单位。
        //bearing - 可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度。
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 12, 0, 0));
        //mapView.getMap().animateCamera(mCameraUpdate);
        mapView.getMap().animateCamera(mCameraUpdate);
    }
    public static void updateBigCenter(MapView mapView, LatLng latLng) {
        //target - 目标位置的屏幕中心点经纬度坐标。
        //zoom - 目标可视区域的缩放级别。
        //tilt - 目标可视区域的倾斜度，以角度为单位。
        //bearing - 可视区域指向的方向，以角度为单位，从正北向顺时针方向计算，从0度到360度。
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(latLng, 15, 0, 0));
        //mapView.getMap().animateCamera(mCameraUpdate);
        mapView.getMap().animateCamera(mCameraUpdate);
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
    public static Marker addMarkView(Context context, MapView mapView, LatLng latLng, View view) {
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
//        markerOption.rotateAngle(0);
        markerOption.icon(BitmapDescriptorFactory.fromView(view));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        markerOption.setFlat(false);//设置marker平贴地图效果
        return mapView.getMap().addMarker(markerOption);
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
    public static Marker addMark(Context context, MapView mapView, LatLng latLng, int ico) {
        try {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.position(latLng);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), ico)));
            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
            markerOption.setFlat(false);//设置marker平贴地图效果
            return mapView.getMap().addMarker(markerOption);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
    public static Marker addDragMark(Activity context, MapView mapView, LatLng latLng, int ico) {
        try {
            MarkerOptions markerOption = new MarkerOptions();
            markerOption.draggable(true);
            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), ico))).anchor(0.5f, 0.7f);
            Marker marker = mapView.getMap().addMarker(markerOption);
            marker.setPosition(latLng);
            mapView.invalidate();
            return marker;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 绘制直线
     * @param mapView
     * @param from
     * @param to
     */
    public static void drawLine(MapView mapView, LatLng from, LatLng to ) {
            //绘制线段
            PolylineOptions options = new PolylineOptions();
            options.addAll(Arrays.asList(from,to));
            options.width(16);
            options.color(Color.parseColor("#F3A516"));
            mapView.getMap().addPolyline(options);

    }




    public static void setMyLocation(TextView tv){
        AmapLocation.with(MyApplication.instance)
                .onceLocation(true)
                .listener(aMapLocation -> tv.setText(aMapLocation.getAddress()))
                .start();
    }



    /**
     * 是否安装高德地图
     *
     * @return
     */
    public static boolean isInstallAmap() {
        return new File("/data/data/com.autonavi.minimap").exists();
    }

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
    public static void startNavigation(Context context, double originLat, double originLng, String originName, double destinationLat, double destinationLng, String destinationName) {
        String uriString = null;
        StringBuilder builder = new StringBuilder("amapuri://route/plan?sourceApplication=maxuslife");
        if (originLat != 0) {
            builder.append("&sname=").append(originName)
                    .append("&slat=").append(originLat)
                    .append("&slon=").append(originLng);
        }
        builder.append("&dlat=").append(destinationLat)
                .append("&dlon=").append(destinationLng)
                .append("&dname=").append(destinationName)
                .append("&dev=0")
                .append("&t=0");
        uriString = builder.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage("com.autonavi.minimap");
        intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
    }

    /**
     * 添加View Mark
     *
     * @param map    地图
     * @param latLng 经纬度
     * @param view   控件
     */
    public static Marker addViewMarkers(MapView map, LatLng latLng, View view) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.fromView(view));
        return map.getMap().addMarker(options);
    }


    /**
     * 添加电子围栏
     *
     * @param context 上下文对象
     * @param points  围栏
     */
    public static void addGeoFence(Context context, List<DPoint> points) {
        GeoFenceClient mGeoFenceClient = new GeoFenceClient(context);
        //设置希望侦测的围栏触发行为，默认只侦测用户进入围栏的行为
        //public static final int GEOFENCE_IN 进入地理围栏
        //public static final int GEOFENCE_OUT 退出地理围栏
        //public static final int GEOFENCE_STAYED 停留在地理围栏内10分钟
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN | GeoFenceClient.GEOFENCE_OUT | GeoFenceClient.GEOFENCE_STAYED);
        mGeoFenceClient.addGeoFence(points, "自有业务ID");
    }

    /**
     * 绘制多边形电子围栏
     *
     * @param mapView
     * @param latLngs
     */
    public static void drawGeoFence(MapView mapView, List<LatLng> latLngs) {
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.strokeColor(Color.parseColor("#8BDAFD"));
        polygonOptions.fillColor(Color.parseColor("#8098DCF7"));
        mapView.getMap().addPolygon(polygonOptions.addAll(latLngs));
    }



}
