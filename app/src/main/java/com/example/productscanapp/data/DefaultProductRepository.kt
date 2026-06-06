package com.example.productscanapp.data

import android.util.Log
import com.example.productscanapp.data.local.dao.FavoriteDao
import com.example.productscanapp.data.local.dao.ProductDao
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.toDomain as localToDomain
import com.example.productscanapp.data.remote.toDomain
import com.example.productscanapp.data.local.toEntity

import com.example.productscanapp.data.local.toFavoriteEntity
import com.example.productscanapp.data.local.toProductEntity
import com.example.productscanapp.data.remote.OpenFoodFactsApi

import com.example.productscanapp.data.remote.toProductException
import com.example.productscanapp.domain.Product
import com.example.productscanapp.domain.ProductError
import com.example.productscanapp.domain.ProductException
import com.example.productscanapp.domain.ProductRepository
import javax.inject.Inject

class DefaultProductRepository @Inject constructor(
    private val api: OpenFoodFactsApi,
    private val scanHistoryDao: ScanHistoryDao,
    private val favoriteDao: FavoriteDao,
    private val productDao: ProductDao,
) : ProductRepository {

    override suspend fun getProductByBarcode(barcode: String): Result<Product> {
        val localProduct = getProductFromLocal(barcode)

        if (localProduct != null) {
            Log.d("PRODUCT_SOURCE", "LOCAL")
            return Result.success(localProduct)
        }

        Log.d("PRODUCT_SOURCE", "API")

        return try {
            val response = api.getProduct(barcode)
            val dto = response.product

            if (response.status != 1 || dto == null) {
                Result.failure(ProductException(ProductError.NotFound))
            } else {
                val product = dto.toDomain(barcode)

                productDao.upsert(
                    product.toProductEntity()
                )

                Log.d("PRODUCT_SOURCE", "SAVED_LOCAL ${product.barcode}")

                val saved = productDao.getByBarcode(product.barcode)

                Log.d(
                    "PRODUCT_SOURCE",
                    "CHECK_AFTER_SAVE = ${saved?.barcode}"
                )

                Result.success(product)
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
        return productDao
            .getByBarcode(barcode)
            ?.localToDomain()
    }
}

