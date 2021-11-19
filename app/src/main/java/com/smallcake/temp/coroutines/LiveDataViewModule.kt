package com.smallcake.temp.coroutines


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smallcake.temp.bean.PhoneRespone
import com.smallcake.temp.http.BaseResponse
import com.smallcake.temp.http.DataProvider
import com.smallcake.temp.http.sub
import com.smallcake.temp.module.LoadDialog
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * 【ViewModel】
 *引入
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1'

 * 只要Activity不被销毁，ViewModel会一直存在,并且独立于Activity的配置变化，即旋转屏幕导致的Activity重建，不会影响到ViewModel
 */
class LiveDataViewModule: ViewModel() ,KoinComponent{
    private val dataProvider: DataProvider = get()//注入数据提供者
    //被观察的数据
    val mobileData: MutableLiveData<BaseResponse<PhoneRespone>?> = MutableLiveData<BaseResponse<PhoneRespone>?>()

    fun getMobileData(phoneNum:String,dialog: LoadDialog?=null){
        viewModelScope.launch {
             dataProvider.mobile.mobileGet(phoneNum,"c95c37113391b9fff7854ce0eafe496d").sub({
                mobileData.value = it
            },dialog=dialog)
        }

    }


}

