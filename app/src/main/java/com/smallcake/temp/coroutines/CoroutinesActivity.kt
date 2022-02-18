package com.smallcake.temp.coroutines

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.amap.api.mapcore.util.it
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.base.replaceFragment
import com.smallcake.temp.databinding.ActivityCoroutinesBinding
import com.smallcake.temp.http.sub
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * 参考：
 * Kotlin协程的简单用法：https://blog.csdn.net/yu540135101/article/details/113246177
 *
 * 1.引入协程包：
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0'

使用扩展1：
如何打印对应的协程名字：需要配置-Dkotlinx.coroutines.debug
Run->Edit Configurations点击进入VM options输入-Dkotlinx.coroutines.debug

    viewmodel-ktx：
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
 * 结构化的并发
 * 协程构建器：
 * @sample GlobalScope.launch {}
 * @sample runBlocking{}
 * 作用域构建器
 * @sample coroutineScope{} 声明自己的作用域
 *
 * 协程区域
 *
 * @see lifecycleScope
 */
@SuppressLint("SetTextI18n")
class CoroutinesActivity : BaseBindActivity<ActivityCoroutinesBinding>() {
    private val TAG = "CoroutinesActivity"
    internal val viewModel:LiveDataViewModule by viewModels()

    private val scope = CoroutineScope(SupervisorJob()+Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("协程")


        bind.btnGet.setOnClickListener{
//            viewModel.getMobileData("18324138218",dialog)
            getWeatherAndPhone()
        }


        viewModel.mobileData.observe(this){
            bind.tvDesc.text = "主页面result:${it?.result}"
        }


        bind.btnFragment1.setOnClickListener{
            replaceFragment(Fragment1(), R.id.container)
        }
        bind.btnFragment2.setOnClickListener{
            replaceFragment(Fragment2(), R.id.container)
        }
        bind.btnFragment3.setOnClickListener{
            replaceFragment(Fragment3(), R.id.container)
        }



    }

    /**
     * 协程中请求天气和手机号
     */
    private fun getWeatherAndPhone(){
        scope.launch{
            val stringBuffer = StringBuffer()
            var job1Str=""
            var job2Str=""
            val time = measureTimeMillis {
//                var job1Str=""
//                var job2Str=""
                val job1 = launch {
                        job1Str = dataProvider.mobile.mobileGetSu("18324138218").result.toString()
                }
                val job2 = launch {
                        job2Str = dataProvider.weather.querySu().result.toString()
                }
                job1.join()
                job2.join()
                stringBuffer.append("${job1Str}\n")
                stringBuffer.append("${job2Str}\n")
            }

            Log.e(TAG,"time:${time} Str:${stringBuffer.toString()}")
            bind.tvDesc.text = "time:${time} Str:${stringBuffer.toString()}"
        }

    }



    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}

