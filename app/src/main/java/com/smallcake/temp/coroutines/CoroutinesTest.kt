package com.smallcake.temp.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

// 代码段14

fun main() = runBlocking {
    // 1，创建管道
    val channel = Channel<Int>()

    launch {
        // 2，在一个单独的协程当中发送管道消息
        (1..3).forEach {
            channel.send(it) // 挂起函数
            logX("Send: $it")
        }
        channel.close() // 变化在这里
    }

    launch {
        // 3，在一个单独的协程当中接收管道消息
        for (i in channel) {  // 挂起函数
            logX("Receive: $i")
        }
    }

    logX("end")
}

/** * 控制台输出带协程信息的log */fun logX(any: Any?) {    println("""================================$any Thread:${Thread.currentThread().name}================================""".trimIndent())}

/*
================================
end
Thread:main @coroutine#1
================================
================================
Receive: 1
Thread:main @coroutine#3
================================
================================
Send: 1
Thread:main @coroutine#2
================================
================================
Send: 2
Thread:main @coroutine#2
================================
================================
Receive: 2
Thread:main @coroutine#3
================================
================================
Receive: 3
Thread:main @coroutine#3
================================
================================
Send: 3
Thread:main @coroutine#2
================================
// 4，程序不会退出
*/

/*
输出结果：
Time: 1032
[Result1, Result2, Result3]
*/

