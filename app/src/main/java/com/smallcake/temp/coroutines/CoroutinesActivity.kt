package com.smallcake.temp.coroutines

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.R
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.base.replaceFragment
import com.smallcake.temp.databinding.ActivityCoroutinesBinding
import com.smallcake.temp.http.sub
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
@SuppressLint("SetTextI18n")
class CoroutinesActivity : BaseBindActivity<ActivityCoroutinesBinding>() {
    private val TAG = "CoroutinesActivity"
    internal val viewModel:LiveDataViewModule by viewModels()

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("协程")


        bind.btnGet.setOnClickListener{
            viewModel.getMobileData("18324138218")
        }
        Handler().postDelayed({ replaceFragment(Fragment1(), R.id.container)},1000)
        Handler().postDelayed({viewModel.getMobileData("18324138218")},3000)

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
}

