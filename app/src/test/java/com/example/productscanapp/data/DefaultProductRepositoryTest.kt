package com.example.productscanapp.data

import com.example.productscanapp.data.local.dao.FavoriteDao
import com.example.productscanapp.data.local.dao.ProductDao
import com.example.productscanapp.data.local.dao.ScanHistoryDao
import com.example.productscanapp.data.local.entity.ProductEntity
import com.example.productscanapp.data.remote.OpenFoodFactsApi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import com.example.productscanapp.data.remote.OpenFoodFactsResponse
import com.example.productscanapp.data.remote.ProductDto
class DefaultProductRepositoryTest {

    private val api: OpenFoodFactsApi = mockk(relaxed = true)
    private val scanHistoryDao: ScanHistoryDao = mockk(relaxed = true)
    private val favoriteDao: FavoriteDao = mockk(relaxed = true)


    private val productDao: ProductDao = mockk(relaxed = true)

    private val repository = DefaultProductRepository(
        api = api,
        scanHistoryDao = scanHistoryDao,
        favoriteDao = favoriteDao,
        productDao = productDao
    )

    @Test
    fun `getProductByBarcode returns local product when product exists locally`() = runTest {
        val barcode = "3017624010701"

        coEvery { productDao.getByBarcode(barcode) } returns ProductEntity(
            barcode = barcode,
            name = "Nutella",
            brand = "Ferrero",
            nutriScore = "E",
            imageUrl = null
        )

        val result = repository.getProductByBarcode(barcode)

        assertEquals("Nutella", result.getOrNull()?.name)

        coVerify(exactly = 1) {
            productDao.getByBarcode(barcode)
        }

        coVerify(exactly = 0) {
            api.getProduct(any())
        }
    }



    @Test
    fun `getProductByBarcode calls api and saves product when product is not local`() = runTest {
        val barcode = "5449000000996"

        coEvery { productDao.getByBarcode(barcode) } returns null

        coEvery { api.getProduct(barcode) } returns OpenFoodFactsResponse(
            status = 1,
            product = ProductDto(
                productName = "Coca-Cola",
                brands = "Coca-Cola",
                nutriScore = "E",
                imageUrl = null
            )
        )

        coEvery { productDao.upsert(any()) } returns Unit

        val result = repository.getProductByBarcode(barcode)

        println("RESULT = ${result.getOrNull()}")
        println("ERROR = ${result.exceptionOrNull()}")

        assertEquals("Coca-Cola", result.getOrNull()?.name)

        coVerify(exactly = 1) {
            api.getProduct(barcode)
        }

        coVerify(exactly = 1) {
            productDao.upsert(any())
        }





    }
    @Test
    fun `removeFromFavorites deletes favorite from dao`() = runTest {
        val barcode = "5449000000996"

        coEvery {
            favoriteDao.deleteFavorite(barcode)
        } returns Unit

        repository.removeFromFavorites(barcode)

        coVerify(exactly = 1) {
            favoriteDao.deleteFavorite(barcode)
        }
    }


    @Test
    fun `isFavorite returns true when product is favorite`() = runTest {
        val barcode = "5449000000996"

        coEvery {
            favoriteDao.isFavorite(barcode)
        } returns true

        val result = repository.isFavorite(barcode)

        assertEquals(true, result)
    }

}