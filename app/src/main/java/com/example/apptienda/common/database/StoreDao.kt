package com.example.apptienda.common.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.apptienda.common.entities.StoreEntity

@Dao
interface StoreDao {

    @Query("SELECT * FROM StoreEntity")
    fun getAllStore(): LiveData<MutableList<StoreEntity>>

    @Query("SELECT * FROM StoreEntity WHERE id=:id")
    fun getStoreById(id : Long) : LiveData<StoreEntity>

    @Insert
    suspend fun addStore(storeEntity: StoreEntity) : Long

    @Update
    suspend fun updateStore(storeEntity: StoreEntity): Int

    @Delete
    suspend fun deleteStore(storeEntity: StoreEntity): Int

}