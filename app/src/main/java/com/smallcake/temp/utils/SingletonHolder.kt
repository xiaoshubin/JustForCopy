package com.smallcake.temp.utils

/**
 * 为了使该逻辑的线程安全: 双重检查锁定算法
 * @param out T
 * @param in A
 * @property creator Function1<A, T>?
 * @property instance T?
 * @constructor
 */
open class SingletonHolder<out T, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    fun getInstance(arg: A): T {
        val i = instance
        if (i != null) {
            return i
        }

        return synchronized(this) {
            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}