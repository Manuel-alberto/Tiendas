package com.example.apptienda.mainModule.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.apptienda.StoreApplication
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.common.utils.StoresExcepetion
import com.example.apptienda.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class MainInteractor {

    val stores: LiveData<MutableList<StoreEntity>> = liveData {
        val storesLiveData = StoreApplication.database.storeDao().getAllStore()
        emitSource(storesLiveData.map { stores->
            stores.sortedBy { it.name }.toMutableList()
        })
    }

    suspend fun deleteStore(storeEntity: StoreEntity) = with(Dispatchers.IO){
        val result = StoreApplication.database.storeDao().deleteStore(storeEntity)
        if(result == 0) throw StoresExcepetion(TypeError.DELETE)
    }

    suspend fun updateStore(storeEntity: StoreEntity) = with(Dispatchers.IO) {
        val result = StoreApplication.database.storeDao().updateStore(storeEntity)
        if(result == 0) throw StoresExcepetion(TypeError.UPDATE)
    }

}