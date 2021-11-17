   package com.smallcake.temp.kotlinflow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

/**
 * Sequence:同步计算值会使用
 * Flow:异步计算
 * flow { ... } 构建块中的代码可以挂起
 * 流使用 emit 函数 发射 值
 * 流使用 collect 函数 收集 值
 * @sample buffer()         缓冲
 * @sample conflate()       合并发射项，不对每个值进行处理
 * @sample zip()            组合多个流
 * 过渡操作符
 * @sample onEach()        每次
 * @sample combine()       组合
 * @sample onCompletion() 它在流完全收集时调用
 * 展平流
 * @sample flatMapConcat() 等待内部流完成之前开始收集下一个值
 * @sample flatMapMerge()  并发收集所有传入的流
 * @sample flatMapLatest() “最新”展平模式
 * 末端操作符
 * @sample launchIn()      替换collect单独的协程中启动流的收集:必要的参数 CoroutineScope 指定了用哪一个协程来启动流的收集
 *
 * 携程构建器
 * @sample runBlocking()
 *
 */
   fun log(msg: String) = println("[${Thread.currentThread().name} @ ] $msg")

   fun foo(): Flow<Int> = flow {
       for (i in 1..5) {
           println("Emitting $i")
           emit(i)
       }
   }

   fun main() = runBlocking<Unit> {
       (1..5).asFlow().cancellable().collect { value ->
           if (value == 3) cancel()
           println(value)
       }
   }