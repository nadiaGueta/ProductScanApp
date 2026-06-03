package com.example.productscanapp.data.remote

import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.domain.ProductException
import java.io.IOException
import retrofit2.HttpException

fun Throwable.toProductException(): ProductException {
    if (this is ProductException) return this

    val mappedError = when (this) {
        is IOException -> ProductError.Network
        is HttpException -> if (code() == 404) ProductError.NotFound else ProductError.Network
        else -> ProductError.Unknown
    }

    return ProductException(error = mappedError, cause = this)
}

