package com.smallcake.temp.map;

import android.content.Context;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class AmapLocation {

    private Context context;
    private int interval = 2000;
    private AMapLocationClient client;
    private boolean onceLocation = true;
    private AMapLocationClientOption option;//定位参数
    private AMapLocationListener listener;
    private static AmapLocation location;

    private AmapLocation(Context context) {
        this.context = context;
        initClient();
    }

    private void initClient() {
        client = new AMapLocationClient(context);
        client.setLocationListener(listener);
        option = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setInterval(interval);
        option.setOnceLocation(onceLocation);
    }

    /**
     * 设置定位间隔,单位毫秒,默认为2000ms
     *
     * @param interval
     * @return
     */
    public AmapLocation interval(int interval) {
        option.setInterval(interval);
        return this;
    }

    /**
     * 定位一次
     *
     * @param onceLocation
     * @return
     */
    public AmapLocation onceLocation(boolean onceLocation) {
        option.setOnceLocation(onceLocation);
        return this;
    }

    /**
     * 设置定位监听
     *
     * @param listener
     * @return
     */
    public AmapLocation listener(AMapLocationListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * 单列模式对象
     *
     * @param context
     * @return
     */
    public static AmapLocation with(Context context) {
        if (location == null) {
            synchronized (AmapLocation.class) {
                if (location == null) {
                    location = new AmapLocation(context);
                }
            }
        }
        return location;
    }


    /**
     * 开始定位
     */
    public AmapLocation start() {
        stop();
        initClient();
        client.setLocationOption(option);
        client.startLocation();
        return this;
    }

    /**
     * 停止定位
     */
    public AmapLocation stop() {
        if (client != null) {
            client.stopLocation();
        }
        return this;
    }

}
