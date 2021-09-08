package com.smallcake.smallutils

import java.math.BigDecimal


object ComputeUtils {
    /**
     * 加
     * 两个float的高精度算法
     * @return 四舍五入取2位
     */
    fun add(f1: Float, f2: Float): Float {
        val b1 = BigDecimal(f1.toString())
        val b2 = BigDecimal(f2.toString())
        return b1.add(b2).setScale(2,BigDecimal.ROUND_HALF_UP).toFloat()
    }
    /**
     * 减
     */
    fun subtract(f1: Float, f2: Float): Float {
        val b1 = BigDecimal(f1.toString())
        val b2 = BigDecimal(f2.toString())
        return b1.subtract(b2).setScale(2,BigDecimal.ROUND_HALF_UP).toFloat()
    }
    /**
     * 乘
     */
    fun multiply(f1: Float, f2: Float): Float {
        val b1 = BigDecimal(f1.toString())
        val b2 = BigDecimal(f2.toString())
        return b1.multiply(b2).setScale(2,BigDecimal.ROUND_HALF_UP).toFloat()
    }



    /**
     * 除
     */
    fun divide(f1: Float, f2: Float): Float {
        val b1 = BigDecimal(f1.toString())
        val b2 = BigDecimal(f2.toString())
        return b1.divide(b2).setScale(2,BigDecimal.ROUND_HALF_UP).toFloat()
    }
}