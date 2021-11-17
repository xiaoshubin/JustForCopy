package com.smallcake.temp.kotlinflow

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.smallcake.smallutils.text.NavigationBar
import com.smallcake.temp.base.BaseBindActivity
import com.smallcake.temp.databinding.ActivityKotlinFlowBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Kotlin Flow 数据流
 * Flow 是一种 "冷流"(Cold Stream)。"冷流" 是一种数据源，
 * 该类数据源的生产者会在每个监听者开始消费事件的时候执行，
 * 从而在每个订阅上创建新的数据流。一旦消费者停止监听或者生产者的阻塞结束，数据流将会被自动关闭。
 * 参考文档：
 * kotlin编程实践 Flow文档：                       http://www.kotlincn.net/docs/reference/coroutines/flow.html
 * 从 LiveData 迁移到 Kotlin 数据流：              https://juejin.cn/post/6979008878029570055
 * 官方推荐 Flow 取代 LiveData,有必要吗？：         https://juejin.cn/post/6986265488275800072
 * Android Kotlin之Flow数据流                     https://blog.csdn.net/u013700502/article/details/120526170
 * Demo实践：
 *
 * @sample stateIn :是专门将数据流转换为 StateFlow 的运算符
 * @sample switchMap 是数据变换中的一种
 */
class KotlinFlowActivity : BaseBindActivity<ActivityKotlinFlowBinding>() {
    private  val TAG = "KotlinFlowActivity"
    lateinit var flow: Flow<Int>

    override fun onCreate(savedInstanceState: Bundle?, bar: NavigationBar) {
        bar.setTitle("Kotlin Flow")
        setupFlow()
        bind.btnGet.setOnClickListener{
            lifecycleScope.launch {
                flow.collect {
                    Log.d(TAG, "value:$it")
                }
            }

        }

    }
    fun setupFlow(){
        flow = flow {
            Log.d(TAG, "Start flow")
            (0..10).forEach {
                // Emit items with 500 milliseconds delay
                delay(500)
                Log.d(TAG, "Emitting $it")
                emit(it)
            }
        }.flowOn(Dispatchers.Default)
    }
}