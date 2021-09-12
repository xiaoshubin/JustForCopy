package com.smallcake.temp.utils

import com.smallcake.temp.bean.TencentCosKey

object UserUtils {

    /**
     * token
     */
    var token: String
        get() = MMKVUtils.mmkv.decodeString("token")?:""
        set(value) {
            MMKVUtils.mmkv.encode("token", value)
        }

    /**
     * 腾讯云获取临时秘钥
     {
    "expiredTime":  1631460490,
    "expiration":  "2021-09-12T15:28:10Z",
    "credentials":  {
        "sessionToken":  "uCvJt412GwO7bLVZO7qI1RTmWT1Zm4naf63fab2dbd2c4a07c21352b615c7b055rn8lz",
        "tmpSecretId":  "AKIDMPATdNwbhDPfd50rKHn0S6P4Gp4OZm1PoKtA4SY5qilNwdly0467sRat1UjDU6AH",
        "tmpSecretKey":  "lfimtMQEm2Wd9SFGBFHoeoxIC+7dAxEVNmk9LlVGJFA="
    },
    "requestId":  "b4ef73a7-0ae8-46eb-a6b4-44b3a17fe9a9",
    "startTime":  1631458690
    }
     */
    var txCos: TencentCosKey?
        get() = MMKVUtils.decodeParcelable("txCos", TencentCosKey::class.java)
        set(value) {
            MMKVUtils.encodeParcelable("txCos", value)
        }


}