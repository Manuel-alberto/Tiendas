package com.example.apptienda.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.common.utils.Constants
import com.example.apptienda.common.utils.StoresExcepetion
import com.example.apptienda.common.utils.TypeError
import com.example.apptienda.mainModule.model.MainInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private var storeList: MutableList<StoreEntity>

    private var interactor: MainInteractor

    init {
        storeList = mutableListOf()
        interactor = MainInteractor()
    }

    /*private val stores: MutableLiveData<MutableList<StoreEntity>> by lazy {
        MutableLiveData<MutableList<StoreEntity>>().also { loadStores() }
    }*/

    private val stores = interactor.stores

    private val showProgress:MutableLiveData<Boolean> = MutableLiveData()

    private var typeError: MutableLiveData<TypeError> = MutableLiveData()

    fun getStores():LiveData<MutableList<StoreEntity>>{
        return stores
    }

    fun getTypeError() : MutableLiveData<TypeError> = typeError

    fun isShowProgress():LiveData<Boolean>{
        return showProgress
    }

    /*private fun loadStores(){
        showProgress.value = Constants.SHOW
        interactor.getStores {
            showProgress.value = Constants.HIDE
            stores.value = it
            storeList = it
        }
    }*/

    fun deleteStore(storeEntity: StoreEntity){
        executeAtion { interactor.deleteStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity){
        storeEntity.isFasvorite = !storeEntity.isFasvorite
        executeAtion { interactor.updateStore(storeEntity) }
    }

    private fun executeAtion(block: suspend () -> Unit): Job{
        return viewModelScope.launch {
            showProgress.value = Constants.SHOW
            try {
                block()
            }catch (e: StoresExcepetion){
                typeError.value = e.typeError
            }finally {
                showProgress.value = Constants.HIDE
            }
        }
    }

}