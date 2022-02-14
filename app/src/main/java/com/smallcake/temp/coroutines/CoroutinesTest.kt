package com.smallcake.temp.coroutines

import kotlinx.coroutines.*


// 不必关心代码逻辑，关心输出结果即可
fun main() {
    GlobalScope.launch(Dispatchers.IO) {
        println("Coroutine started:${Thread.currentThread().name}")
        delay(1000L)
        println("Hello World!")
    }

    println("After launch:${Thread.currentThread().name}")
    Thread.sleep(2000L)
}

/*
输出结果：
After launch:main
Coroutine started:DefaultDispatcher-worker-1 @coroutine#1
*/


