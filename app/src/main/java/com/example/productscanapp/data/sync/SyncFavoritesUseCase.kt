package com.example.productscanapp.data.sync

import com.example.productscanapp.data.local.dao.FavoriteDao
import com.example.productscanapp.data.local.toFavoriteEntity
import com.example.productscanapp.domain.ProductRepository
import javax.inject.Inject

class SyncFavoritesUseCase @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(): Result<Int> {
        val favorites = favoriteDao.getAllFavorites()
        if (favorites.isEmpty()) {
            return Result.success(0)
        }

        var syncedCount = 0
        for (favorite in favorites) {
            val refreshedProduct = productRepository
                .getProductByBarcode(favorite.barcode)
                .getOrElse { error ->
                    // if fail can retry later
                    return Result.failure(error)
                }

            favoriteDao.upsertFavorite(
                refreshedProduct.toFavoriteEntity(favoriteAt = favorite.favoriteAt),
            )
            syncedCount++
        }

        return Result.success(syncedCount)
    }
}

