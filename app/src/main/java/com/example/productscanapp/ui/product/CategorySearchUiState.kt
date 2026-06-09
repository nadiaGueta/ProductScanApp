package com.example.productscanapp.ui.product

import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError

data class CategorySearchUiState(
    val products: List<Product> = emptyList(),
    val isInitialLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val canLoadMore: Boolean = true,
    val error: ProductError? = null
)