package com.example.apptienda.editModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptienda.common.entities.StoreEntity
import com.example.apptienda.common.utils.StoresExcepetion
import com.example.apptienda.common.utils.TypeError
import com.example.apptienda.editModule.model.EditStoreInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditStoreViewModel: ViewModel() {

    private var storeId: Long = 0

    private val showFab = MutableLiveData<Boolean>()
    private val result = MutableLiveData<Any>()
    private var typeError: MutableLiveData<TypeError> = MutableLiveData()

    private val interactor: EditStoreInteractor

    init {
        interactor = EditStoreInteractor()
    }

    fun setTypeError(typeError: TypeError){
        this.typeError.value = typeError
    }

    fun setStoreSelected(storeEntity: StoreEntity){
        storeId = storeEntity.id
    }

    fun getTypeError() : MutableLiveData<TypeError> = typeError

    fun getStoreSelected(): LiveData<StoreEntity>{
        return interactor.getStoreById(storeId)
    }

    fun setShowFab(isVisible: Boolean){
        showFab.value = isVisible
    }

    fun getShowFab(): LiveData<Boolean>{
        return showFab
    }

    fun setResult(value: Any){
        result.value = value
    }

    fun getResult():LiveData<Any>{
        return result
    }

    fun saveStore(storeEntity: StoreEntity){
        executeAtion(storeEntity) { interactor.saveStore(storeEntity) }
    }

    fun updateStore(storeEntity: StoreEntity){
        executeAtion(storeEntity) { interactor.updateStore(storeEntity) }
    }

    private fun executeAtion(storeEntity: StoreEntity, block: suspend () -> Unit): Job {
        return viewModelScope.launch {
            try {
                block()
                result.value = storeEntity
            }catch (e: StoresExcepetion){
                typeError.value = e.typeError
            }
        }
    }

}