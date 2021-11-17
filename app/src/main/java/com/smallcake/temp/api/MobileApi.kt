package com.smallcake.temp.api

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.smallcake.temp.bean.PhoneRespone
import com.smallcake.temp.http.BaseResponse
import com.smallcake.temp.http.im
import io.reactivex.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.http.GET
import retrofit2.http.Query

interface MobileApi {
    @GET("mobile/get")
    fun mobileGet(@Query("phone")phone: String, @Query("key")key:String="c95c37113391b9fff7854ce0eafe496d"): Observable<BaseResponse<PhoneRespone>>

    @GET("mobile/get")
    suspend fun mobileGetSu(@Query("phone")phone: String, @Query("key")key:String="c95c37113391b9fff7854ce0eafe496d"): BaseResponse<PhoneRespone>


}

@KoinApiExtension
class MobileImpl :MobileApi , KoinComponent {
    private val api: MobileApi by inject()
    override fun mobileGet(mobile: String, key: String): Observable<BaseResponse<PhoneRespone>>
            = api.mobileGet(mobile).im()

    override suspend fun mobileGetSu(phone: String, key: String): BaseResponse<PhoneRespone>  = api.mobileGetSu(phone,key)


}