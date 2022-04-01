package com.smallcake.temp.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

// 代码段14

fun main() = runBlocking {

    fun complicatedexpression_r(){
    var x=20
        var y=30
    var  b=false
    b=x>50&&y>60||x>50&&y<-60||x<-50&&y>60||x<-50&&y<-60;
        logX("==$b")
    }

    complicatedexpression_r()

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

