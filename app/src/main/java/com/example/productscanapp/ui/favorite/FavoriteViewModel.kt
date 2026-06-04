package com.example.productscanapp.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productscanapp.data.local.dao.FavoriteDao
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.entity.ScanHistoryEntity
import com.example.productscanapp.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.productscanapp.data.local.toDomain
import kotlinx.coroutines.flow.map
@HiltViewModel
class FavoriteViewModel @Inject constructor(
   // private val dao: ScanHistoryDao
    private val favoriteDao: FavoriteDao
) : ViewModel() {


    val favorites = favoriteDao.observeFavorites()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun removeFromFavorites(barcode: String) {
        viewModelScope.launch {
            favoriteDao.deleteFavorite(barcode)
        }
    }



}