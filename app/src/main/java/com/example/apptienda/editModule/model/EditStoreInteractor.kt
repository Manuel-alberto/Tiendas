package com.example.apptienda.editModule.model

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import com.example.apptienda.StoreApplication
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.common.utils.StoresExcepetion
import com.example.apptienda.common.utils.TypeError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EditStoreInteractor {

    fun getStoreById(id: Long): LiveData<StoreEntity>{
        return StoreApplication.database.storeDao().getStoreById(id)
    }

    suspend fun saveStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        try{
            StoreApplication.database.storeDao().addStore(storeEntity)
        }catch (e: SQLiteConstraintException){
            throw StoresExcepetion(TypeError.INSERT)
        }
    }
    suspend fun updateStore(storeEntity: StoreEntity) = withContext(Dispatchers.IO){
        try{
            val result = StoreApplication.database.storeDao().updateStore(storeEntity)
            if (result == 0) throw StoresExcepetion(TypeError.UPDATE)
        }catch (e: SQLiteConstraintException){
            throw StoresExcepetion(TypeError.UPDATE)
        }
    }
}