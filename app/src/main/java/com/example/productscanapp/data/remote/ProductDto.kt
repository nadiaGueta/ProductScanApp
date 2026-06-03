package com.example.productscanapp.data.remote



import com.squareup.moshi.Json

data class OpenFoodFactsResponse(
    val status: Int,
    val product: ProductDto?
)

data class ProductDto(
    @Json(name = "product_name")
    val productName: String?,

    val brands: String?,

    @Json(name = "nutriscore_grade")
    val nutriScore: String?,

    @Json(name = "image_url")
    val imageUrl: String?
)