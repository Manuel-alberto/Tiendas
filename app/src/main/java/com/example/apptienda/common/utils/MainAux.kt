package com.example.apptienda.common.utils

import com.example.apptienda.common.entities.StoreEntity

interface MainAux {
    fun hideFab(isVisible:Boolean=false)
    fun addStore(storeEntity: StoreEntity)
    fun updateStore(storeEntity: StoreEntity)
}