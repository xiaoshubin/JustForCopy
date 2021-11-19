package com.smallcake.temp

import android.app.Application
import android.content.Context
import android.net.http.HttpResponseCache
import androidx.multidex.MultiDex
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.lsxiao.apollo.core.Apollo
import com.opensource.svgaplayer.SVGAParser
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.smallcake.smallutils.SmallUtils
import com.smallcake.temp.module.httpModule
import com.smallcake.temp.utils.L
import com.tencent.mmkv.MMKV
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.core.context.startKoin
import org.litepal.LitePal
import java.io.File


/**
 * Date: 2020/1/4
 * author: SmallCake
 */
class MyApplication : Application() {
    companion object{
       lateinit var instance:MyApplication
    }

    /**
     * 获取svga解析器
     * @return SVGAParser
     */
    fun getSVGAParser(): SVGAParser {
        return SVGAParser.shareParser()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //日志打印
        Logger.addLogAdapter(AndroidLogAdapter())
        //模块注入
        startKoin{
            modules(httpModule)
        }
        //事件通知
        Apollo.init(AndroidSchedulers.mainThread(), this)
        //数据存储
        MMKV.initialize(this)
        //小工具初始化
        SmallUtils.init(this)
        //facebook登录
        AppEventsLogger.activateApp(this)
        FacebookSdk.setAutoLogAppEventsEnabled(true)
        FacebookSdk.setIsDebugEnabled(true)
        // 腾讯tbs优化:在调用TBS初始化、创建WebView之前进行如下配置
        val map = HashMap<String, Any>()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
        QbSdk.initX5Environment(this,object :QbSdk.PreInitCallback{
            override fun onCoreInitFinished() {
                L.e("onCoreInitFinished")
            }

            override fun onViewInitFinished(b: Boolean) {
                L.e("onViewInitFinished:$b")
            }

        })
        //数据库
        LitePal.initialize(this)

        //必须在使用 SVGAParser 单例前初始化
        SVGAParser.shareParser().init(this)
        //SVGAParser 依赖 URLConnection, URLConnection 使用 HttpResponseCache 处理缓存
        val cacheDir = File(applicationContext.cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)

    }


    //方法数量过多，合并
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}