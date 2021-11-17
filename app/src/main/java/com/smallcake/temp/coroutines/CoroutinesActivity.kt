package com.smallcake.temp.coroutines

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityCoroutinesBinding
import kotlinx.coroutines.*

/**
 * 参考：
 * Kotlin协程的简单用法：https://blog.csdn.net/yu540135101/article/details/113246177
 *
 * 协程包：
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0'
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0'
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
class CoroutinesActivity : BaseBindActivity<ActivityCoroutinesBinding>() {
    private val TAG = "CoroutinesActivity"
    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("协程")
        bind.btnGet.setOnClickListener{
            lifecycleScope.launch {
              delay(3000)
              val result = withContext(Dispatchers.IO){dataProvider.mobileApi.mobileGetSu("18324138218","c95c37113391b9fff7854ce0eafe496d")}
                   bind.tvDesc.text = result.result.toString()
          }
        }


    }
}

