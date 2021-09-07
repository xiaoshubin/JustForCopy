package com.smallcake.smallutils

import android.util.Base64
import android.util.Log
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * Base64加密解密
 */
object Base64Utils {

    /**
     * BASE64加密
     * 你好啊！
     * 5L2g5aW95ZWK77yB
     */
    fun encryptBase64(key: String): String {
        return  String(Base64.encode(key.toByteArray(),Base64.DEFAULT))
    }
    private fun encryptBase64(key: ByteArray?): String {
        return  String(Base64.encode(key,Base64.DEFAULT))
    }

    /**
     * BASE64解密
     * 5L2g5aW95ZWK77yB
     * 你好啊！
     */
    fun decryptBase64(key: String): String {
        return String(Base64.decode(key, Base64.DEFAULT))
    }

    fun decryptBase64ByteArray(key: String):ByteArray {
       return Base64.decode(key, Base64.DEFAULT)
    }


    /**
     * 采用AES加密算法
     */
    private const val KEY_ALGORITHM = "AES"
    private const val CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding"
    /**
     * AES 加密
     *
     * @param secretKey 加密密码，长度：16 或 32 个字符
     * @param data      待加密内容
     * @return 返回Base64转码后的加密数据
     */
    fun encryptAES(secretKey: String, data: String): String {
        try {
            // 创建AES秘钥
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), KEY_ALGORITHM)
            // 创建密码器
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            // 初始化加密器
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
            val encryptByte = cipher.doFinal(data.toByteArray(Charsets.UTF_8))
            // 将加密以后的数据进行 Base64 编码
            return encryptBase64(encryptByte)
        } catch (e: Exception) {
            e.message?.let { Log.e("encrypt", it) }
        }
        return ""
    }

    /**
     * AES 解密
     *
     * @param secretKey  解密的密钥，长度：16 或 32 个字符
     * @param base64Data 加密的密文 Base64 字符串
     */
    fun decryptAES(secretKey: String, base64Data: String): String {
        try {
            val data: ByteArray = decryptBase64ByteArray(base64Data)
            // 创建AES秘钥
            val secretKeySpec = SecretKeySpec(secretKey.toByteArray(Charsets.UTF_8), KEY_ALGORITHM)
            // 创建密码器
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            // 初始化解密器
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            // 执行解密操作
            val result = cipher.doFinal(data)
            return String(result, Charsets.UTF_8)
        } catch (e: java.lang.Exception) {
            e.message?.let { Log.e("decrypt", it) }
        }
        return ""
    }




}