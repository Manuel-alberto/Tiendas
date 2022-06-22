package com.example.apptienda.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.mainModule.model.MainInteractor

class MainViewModel: ViewModel() {

    private var storeList: MutableList<StoreEntity>

    private var interactor: MainInteractor

    init {
        storeList = mutableListOf()
        interactor = MainInteractor()
    }
    private val stores: MutableLiveData<List<StoreEntity>> by lazy {
        MutableLiveData<List<StoreEntity>>().also { loadStores() }
    }

    fun getStores():LiveData<List<StoreEntity>>{
        return stores
    }

    private fun loadStores(){

        interactor.getStores {
            stores.value = it
            storeList = it
        }
    }

    fun deleteStore(storeEntity: StoreEntity){
        interactor.deleteStore(storeEntity) {
            val index = storeList.indexOf(storeEntity)
            if (index != -1) {
                storeList.removeAt(index)
                stores.value = storeList
            }
        }
    }

    fun updateStore(storeEntity: StoreEntity){
        storeEntity.isFasvorite = !storeEntity.isFasvorite
        interactor.deleteStore(storeEntity) {
            val index = storeList.indexOf(storeEntity)
            if (index != -1) {
                storeList.set(index, storeEntity)
                stores.value = storeList
            }
        }
    }

}