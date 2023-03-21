package com.smallcake.smallutils

import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

/**
 *
 * generateKey方法可以很便捷的在Android上生成秘钥，
 * 但实际开发中因为安全性问题很少在Android端生成秘钥（不排除特殊需求），
 * 通常是服务器端将公钥发送给Android端，Android端用公钥进行加密后发送给服务器端，
 * 服务器用私钥进行解密。秘钥的保存和传输通常会转换成一串ASCII码如下：
-----BEGIN PUBLIC KEY-----
AOCAQ8AMIIBCgKCAQEAxlSpJwBEM/ia2P5jLTAGSxMexRxSKlmF
gIrPX7g0DsPIrqhMZMleTSLXOMT8D+1+T1UlHfwsJOybpVoliLo
vdcrDawQsypTXIPIpk62TrJwDMPoXAZXP8AHSDsPIrqhMZMleTS
-----END PUBLIC KEY-----
 */
object RsaUtil {

    /**
     * 加密算法
     */
    private var ENCRYPT_ALGORITHM = "RSA/ECB/PKCS1Padding"

    /**
     * 字符
     */
    private var CHARSET = "UTF-8"

    /**
     * 2048位rsa单次最大加密长度
     */
    private var MAX_ENCRYPT_BLOCK = 234

    /**
     * RSA加密
     * @param param 要加密的内容
     * @return 加密后的内容
     */
    fun encrypt(param: String, publicKeyStr: String): String {
        val keyFactory = KeyFactory.getInstance("RSA")
        val publicKey = keyFactory.generatePublic(X509EncodedKeySpec(Base64.decode(publicKeyStr,Base64.DEFAULT)))
        try {
            ByteArrayOutputStream().use { out ->
                val cipher = Cipher.getInstance(ENCRYPT_ALGORITHM)
                cipher.init(Cipher.ENCRYPT_MODE, publicKey)
                val data = param.toByteArray(charset(CHARSET))
                val inputLen = data.size
                var offSet = 0
                var cache: ByteArray
                var i = 0
                // 对数据分段加密
                while (inputLen - offSet > 0) {
                    cache = if (inputLen > MAX_ENCRYPT_BLOCK + offSet) {
                        cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK)
                    } else {
                        cipher.doFinal(data, offSet, inputLen - offSet)
                    }
                    out.write(cache, 0, cache.size)
                    i++
                    offSet = i * MAX_ENCRYPT_BLOCK
                }
                return Base64.encodeToString(
                    out.toByteArray(),
                    Base64.DEFAULT
                )
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return ""
    }


}