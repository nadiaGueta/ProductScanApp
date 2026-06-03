package com.example.productscanapp.domain

sealed interface ProductError {
    data object Network : ProductError
    data object NotFound : ProductError
    data object Unknown : ProductError
}

