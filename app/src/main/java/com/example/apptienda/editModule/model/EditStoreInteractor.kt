package com.example.apptienda.editModule.model

import com.example.apptienda.StoreApplication
import com.example.apptienda.common.entities.StoreEntity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EditStoreInteractor {
    fun saveStore(storeEntity: StoreEntity, callback: (Long) -> Unit){
        doAsync {
            val newId = StoreApplication.database.storeDao().addStore(storeEntity)
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(newId)
            }
        }
    }
    fun updateStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.database.storeDao().updateStore(storeEntity)
            uiThread {
                callback(storeEntity)
            }
        }
    }
}