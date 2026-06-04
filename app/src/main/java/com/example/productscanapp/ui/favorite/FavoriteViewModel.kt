package com.example.productscanapp.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.entity.ScanHistoryEntity
import com.example.productscanapp.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val dao: ScanHistoryDao
) : ViewModel() {

    val favorites = dao.observeFavorites()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    fun removeFromFavorites(barcode: String) {
        viewModelScope.launch {
            dao.updateFavorite(
                barcode = barcode,
                isFavorite = false,
                favoriteAt = null
            )
        }
    }



}