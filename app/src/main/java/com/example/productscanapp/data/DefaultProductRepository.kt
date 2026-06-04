package com.example.productscanapp.data

import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.toEntity
import com.example.productscanapp.data.remote.OpenFoodFactsApi
import com.example.productscanapp.data.remote.toDomain
import com.example.productscanapp.data.remote.toProductException
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.domain.ProductException
import com.example.productscanapp.domain.ProductRepository
import javax.inject.Inject

class DefaultProductRepository @Inject constructor(
    private val api: OpenFoodFactsApi,
    private val scanHistoryDao: ScanHistoryDao
) : ProductRepository {

    override suspend fun getProductByBarcode(barcode: String): Result<Product> {
        return try {
            val response = api.getProduct(barcode)
            val dto = response.product

            if (response.status != 1 || dto == null) {
                Result.failure(ProductException(ProductError.NotFound))
            } else {
                Result.success(dto.toDomain(barcode))
            }
        } catch (throwable: Throwable) {
            Result.failure(throwable.toProductException())
        }
    }

    override suspend fun addToFavorites(product: Product) {
        scanHistoryDao.upsert(
            product.toEntity(
                scannedAt = System.currentTimeMillis(),
                isFavorite = true ,
                favoriteAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun isFavorite(barcode: String): Boolean {
        return scanHistoryDao.isFavorite(barcode) == true
    }


    override suspend fun removeFromFavorites(barcode: String) {
        scanHistoryDao.updateFavorite(
            barcode = barcode,
            isFavorite = false,
            favoriteAt = null
        )
    }
}

