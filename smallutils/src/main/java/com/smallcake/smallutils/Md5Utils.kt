package com.smallcake.smallutils

import java.security.MessageDigest
import java.util.*

object Md5Utils {
    //公盐(前缀+后缀)
    private val SALT_START = "a0bjd35ff4kk9t6"
    private val SALT_END = "h3m8sh3l3s5lls"

    //十六进制下数字到字符的映射数组
    private val hexDigits = arrayOf(
        "0", "1", "2", "3", "4",
        "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
    )

    /**
     *
     * @param source 需要加密的字符串
     * @return MD5加密字符串
     */
    fun encryptInfo(source: String): String? {
        // 需要加密字符串+公盐
        return md5(SALT_START + source + SALT_END)
    }

    /**
     * md5加密算法
     *
     * @param encryptStr
     * @return 32位大写
     */
    fun md5(encryptStr: String): String {
        try {
            // 创建具有指定算法名称的信息摘要
            val md = MessageDigest.getInstance("MD5")
            // 获取二进制
            val bytes = encryptStr.toByteArray()
            // 执行加密并获得加密的结果,结果为byte字节数组
            val results = md.digest(bytes)
            // 将得到的字节数组变成字符串返回
            val resultString = byteArrayToHexString(results)
            return resultString.toUpperCase(Locale.ROOT)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return ""
    }
    /**
     * md5加密算法
     * @param encryptStr
     * @return 16位大写
     */
    fun encrypt16(encryptStr: String): String {
        return md5(encryptStr).substring(8, 24)
    }

    /**
     * 转换字节数组为十六进制字符串
     *
     * @param字节数组
     * @return 十六进制字符串
     */
    private fun byteArrayToHexString(b: ByteArray): String {
        val resultSb = StringBuffer()
        for (i in b.indices) {
            resultSb.append(byteToHexString(b[i]))
        }
        return resultSb.toString()
    }

    /**
     * 将一个字节转化成十六进制形式的字符串
     */
    private fun byteToHexString(b: Byte): String? {
        var n = b.toInt()
        if (n < 0) n = 256 + n
        val d1 = n / 16
        val d2 = n % 16
        return hexDigits[d1] + hexDigits[d2]
    }

}