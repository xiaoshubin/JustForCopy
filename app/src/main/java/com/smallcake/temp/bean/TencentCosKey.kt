package com.smallcake.temp.bean

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TencentCosKey(
    val credentials: Credentials,
    val expiration: String,
    val expiredTime: Long,
    val requestId: String,
    val startTime: Long
): Parcelable
@Parcelize
data class Credentials(
    val sessionToken: String,
    val tmpSecretId: String,
    val tmpSecretKey: String
):Parcelable