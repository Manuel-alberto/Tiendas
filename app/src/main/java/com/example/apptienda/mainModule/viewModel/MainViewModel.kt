package com.example.apptienda.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.common.utils.Constants
import com.example.apptienda.mainModule.model.MainInteractor

class MainViewModel: ViewModel() {

    private var storeList: MutableList<StoreEntity>

    private var interactor: MainInteractor

    init {
        storeList = mutableListOf()
        interactor = MainInteractor()
    }
    private val stores: MutableLiveData<MutableList<StoreEntity>> by lazy {
        MutableLiveData<MutableList<StoreEntity>>().also { loadStores() }
    }

    private val showProgress:MutableLiveData<Boolean> = MutableLiveData()

    fun getStores():LiveData<MutableList<StoreEntity>>{
        return stores
    }

    fun isShowProgress():LiveData<Boolean>{
        return showProgress
    }

    private fun loadStores(){
        showProgress.value = Constants.SHOW
        interactor.getStores {
            showProgress.value = Constants.HIDE
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