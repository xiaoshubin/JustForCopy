package com.smallcake.temp.kotlinflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smallcake.temp.http.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * @see viewModelScope  页面的视图区域
 */
class MyViewModule: ViewModel(),KoinComponent {
    val flow1: Flow<Int> = (1..3).asFlow()
    val flow2: Flow<Int> = (4..6).asFlow()
    val result = combine(flow1, flow2) { a, b -> a + b }
    val dataProvider: DataProvider = get()//注入数据提供者
    fun test(){
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO){dataProvider.mobileApi.mobileGetSu("18324138218","c95c37113391b9fff7854ce0eafe496d")}

        }
    }
}