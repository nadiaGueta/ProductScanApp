package com.example.productscanapp.data

import com.example.productscanapp.data.local.dao.FavoriteDao
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.toDomain as localToDomain
import com.example.productscanapp.data.remote.toDomain
import com.example.productscanapp.data.local.toEntity

import com.example.productscanapp.data.local.toFavoriteEntity
import com.example.productscanapp.data.remote.OpenFoodFactsApi

import com.example.productscanapp.data.remote.toProductException
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.domain.ProductException
import com.example.productscanapp.domain.ProductRepository
import javax.inject.Inject

class DefaultProductRepository @Inject constructor(
    private val api: OpenFoodFactsApi,
    private val scanHistoryDao: ScanHistoryDao ,
    private val favoriteDao: FavoriteDao
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
        favoriteDao.upsertFavorite(
            product.toFavoriteEntity()
        )
    }

    override suspend fun isFavorite(barcode: String): Boolean {
        return favoriteDao.isFavorite(barcode)
    }


    override suspend fun removeFromFavorites(barcode: String) {
        favoriteDao.deleteFavorite(barcode)
    }



    override suspend fun getProductFromLocal(barcode: String): Product? {
        return scanHistoryDao
            .getByBarcode(barcode)
            ?.localToDomain()
    }
}

