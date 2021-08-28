package com.smallcake.smallutils

import java.math.BigDecimal


object ComputeUtils {
    /**
     * 相乘
     * 两个float的高精度算法
     * @param f1
     * @param f2
     * @return 四舍五入取2位
     * @see BigDecimal.ROUND_HALF_UP 四舍五入
     */
    fun multiply(f1: Float, f2: Float): Float {
        val b1 = BigDecimal(f1.toString())
        val b2 = BigDecimal(f2.toString())
        return b1.multiply(b2).setScale(2,BigDecimal.ROUND_HALF_UP).toFloat()
    }

    /**
     * 相除
     * @param f1
     * @param f2
     * @return
     */
    fun divide(f1: Float, f2: Float): Float {
        val b1 = BigDecimal(f1.toString())
        val b2 = BigDecimal(f2.toString())
        return b1.divide(b2).setScale(2,BigDecimal.ROUND_HALF_UP).toFloat()
    }
}