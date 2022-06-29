package com.example.apptienda.mainModule.model

import android.util.Log
import com.android.volley.Request.*
import com.android.volley.toolbox.JsonObjectRequest
import com.example.apptienda.StoreApplication
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.common.utils.Constants
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class MainInteractor {

    fun getStoresRoom(callback: (MutableList<StoreEntity>) -> Unit ){
        doAsync {
            val storeList= StoreApplication.database.storeDao().getAllStore()
            uiThread {

                val json= Gson().toJson(storeList)
                Log.i("Gson", json)
                callback(storeList)
            }
        }
    }

    fun getStores(callback: (MutableList<StoreEntity>) -> Unit){

        val url = Constants.STORES_URL+Constants.GET_ALL_PHAT
        var storeList = mutableListOf<StoreEntity>()
        val jsonObjectRequest = JsonObjectRequest(Method.GET, url, null, {response->

            val status = response.optInt(Constants.STATUS_PROPERTY, Constants.ERROR)

            if(status == Constants.SUCCES){
                val jsonList = response.optJSONArray(Constants.STORES_PROPERTY)?.toString()
                if (jsonList != null){
                    val mutableListType = object : TypeToken<MutableList<StoreEntity>>(){}.type
                    storeList = Gson().fromJson(jsonList, mutableListType)

                    callback(storeList)
                    return@JsonObjectRequest
                }
            }
            callback(storeList)
        },{
            it.printStackTrace()
            callback(storeList)
        })
        StoreApplication.storeAPI.addToRequestQueue(jsonObjectRequest)
    }

    fun deleteStore(storeEntity: StoreEntity, callback: (StoreEntity) -> Unit){
        doAsync {
            StoreApplication.database.storeDao().deleteStore(storeEntity)
            uiThread {
                callback(storeEntity)
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