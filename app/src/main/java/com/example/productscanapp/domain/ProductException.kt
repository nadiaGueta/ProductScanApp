package com.example.productscanapp.domain

class ProductException(
    val error: ProductError,
    cause: Throwable? = null,
) : Exception(error.toString(), cause)

