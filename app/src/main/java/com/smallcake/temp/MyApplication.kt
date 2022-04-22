package com.smallcake.temp

import android.app.Application
import android.content.Context
import android.net.http.HttpResponseCache
import android.util.Log
import androidx.multidex.MultiDex
import com.baidu.mapapi.SDKInitializer
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.lsxiao.apollo.core.Apollo
import com.opensource.svgaplayer.SVGAParser
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.smallcake.smallutils.SmallUtils
import com.smallcake.temp.bean.DaoMaster
import com.smallcake.temp.bean.DaoSession
import com.smallcake.temp.module.httpModule
import com.smallcake.temp.module.mapModule
import com.tencent.mmkv.MMKV
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.koin.androidContext
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
       lateinit var daoSession : DaoSession
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
            androidContext(this@MyApplication)
            modules(httpModule, mapModule)
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
                Log.e("TAG","onCoreInitFinished")
            }

            override fun onViewInitFinished(b: Boolean) {
                Log.e("TAG","onViewInitFinished:$b")
            }

        })
        //数据库
        LitePal.initialize(this)

        //必须在使用 SVGAParser 单例前初始化
        SVGAParser.shareParser().init(this)
        //SVGAParser 依赖 URLConnection, URLConnection 使用 HttpResponseCache 处理缓存
        val cacheDir = File(applicationContext.cacheDir, "http")
        HttpResponseCache.install(cacheDir, 1024 * 1024 * 128)

        //百度地图
        SDKInitializer.initialize(this)
        initDao()

    }

    /**
     * 初始化GreenDao数据库
     */
    private fun initDao(){
        val helper = DaoMaster.DevOpenHelper(this,"smallcake.db")//创建的数据库名。
        val db = helper.writableDb
        daoSession = DaoMaster(db).newSession()
    }


    //方法数量过多，合并
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}