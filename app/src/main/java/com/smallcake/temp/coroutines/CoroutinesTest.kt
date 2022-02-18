package com.smallcake.temp.coroutines

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// 代码段14

fun main() = runBlocking {
    suspend fun getResult1(): String {
        delay(1000L) // 模拟耗时操作
        return "Result1"
    }

    suspend fun getResult2(): String {
        delay(1000L) // 模拟耗时操作
        return "Result2"
    }

    suspend fun getResult3(): String {
        delay(1000L) // 模拟耗时操作
        return "Result3"
    }

    val results: List<String>

    val time = measureTimeMillis {
        val result1 = async { getResult1() }
        val result2 = async { getResult2() }
        val result3 = async { getResult3() }

        results = listOf(result1.await(), result2.await(), result3.await())
    }

    println("Time: $time")
    println(results)
}

/*
输出结果：
Time: 1032
[Result1, Result2, Result3]
*/

