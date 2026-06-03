package com.example.productscanapp.ui.product

import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError

sealed interface ProductUiState {
    data object Idle : ProductUiState
    data object Loading : ProductUiState
    data class Success(val product: Product) : ProductUiState
    data class Error(val error: ProductError) : ProductUiState
}

