package com.example.productscanapp.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {

    @GET("api/v2/product/{barcode}.json")
    suspend fun getProduct(
        @Path("barcode") barcode: String
    ): OpenFoodFactsResponse

    @GET("api/v2/search")
    suspend fun searchAlternatives(
        @Query("categories_tags_en") category: String,
        @Query("page_size") pageSize: Int = 20,
        @Query("fields") fields: String =
            "code,product_name,brands,nutriscore_grade,image_url,categories_tags_en"
    ): OpenFoodFactsSearchResponse
}