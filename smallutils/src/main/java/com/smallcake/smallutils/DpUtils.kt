package com.smallcake.smallutils

import java.math.BigDecimal


/**
 * 单位转换
 * 1.dp 转 px
 * 2.px 转 dp
 */


/**
 * dp 转 px
 * @receiver Int 1
 * @return Int 3
 */
val Int.px: Int
    get() {
        val scale = SmallUtils.context?.applicationContext?.resources
            ?.displayMetrics?.density ?: 0.0f
        return (this * scale + 0.5f).toInt()
    }
val Float.px: Float
    get() {
        val scale = SmallUtils.context?.applicationContext?.resources
            ?.displayMetrics?.density ?: 0.0f
        return BigDecimal((this * scale + 0.5f).toString()).setScale(2, BigDecimal.ROUND_HALF_UP)
            .toFloat()
    }
val Float.pxInt: Int
    get() {
        val scale = SmallUtils.context?.applicationContext?.resources
            ?.displayMetrics?.density ?: 0.0f
        return (this * scale + 0.5f).toInt()

    }

/**
 * px 转 dp
 * @receiver Int 3
 * @return Int 1
 */
val Int.dp: Int
    get() {
        val scale =
            SmallUtils.context?.resources?.displayMetrics?.density ?: 0.0f
        return (this / scale + 0.5f).toInt()
    }
val Float.dp: Float
    get() {
        val scale =
            SmallUtils.context?.resources?.displayMetrics?.density ?: 0.0f
        return BigDecimal((this / scale + 0.5f).toString()).setScale(2, BigDecimal.ROUND_HALF_UP)
            .toFloat()
    }

/**
 * 保留两位小数(四舍五入)
 * @receiver Float 1.891565454f  1.895565454f
 * @return   Float 1.89          1.9
 */
val Float.twoDecimals: Float
    get() {
        return BigDecimal((this).toString()).setScale(2, BigDecimal.ROUND_HALF_UP)
            .toFloat()
    }