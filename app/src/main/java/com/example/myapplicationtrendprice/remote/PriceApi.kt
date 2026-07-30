package com.example.myapplicationtrendprice.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PriceApi {

    @GET("price-service/api/prices")
    suspend fun getPrices(): List<PriceDto>

    @POST("price-service/api/prices")
    suspend fun createPrice(
        @Body price: PriceDto
    ): PriceDto

    @POST("price-service/api/prices/massCreate")
    suspend fun createPrices(
        @Body prices: List<PriceDto>
    ): List<PriceDto>

    @GET("price-service/api/prices/{id}")
    suspend fun getPrice(
        @Path("id") id: String
    ): PriceDto

    @PUT("price-service/api/prices/{id}")
    suspend fun updatePrice(
        @Path("id") id: String,
        @Body price: PriceDto
    ): PriceDto

    @DELETE("price-service/api/prices/{id}")
    suspend fun deletePrice(
        @Path("id") id: String
    )

    @GET("price-service/api/prices/updated/stores")
    suspend fun getUpdatedStores(): Double

    @GET("price-service/api/prices/updated/products")
    suspend fun getUpdatedProducts(): Double

    @GET("price-service/api/prices/store/{store_id}")
    suspend fun getPricesByStoreId(
        @Path("store_id") storeId: String
    ): List<PriceDto>

    @GET("price-service/api/prices/store/{store_id}/product/{product_id}")
    suspend fun getPricesByStoreIdAndProductId(
        @Path("store_id") storeId: String,
        @Path("product_id") productId: String
    ): List<PriceDto>

    @GET("price-service/api/prices/product/{product_id}")
    suspend fun getPricesByProductId(
        @Path("product_id") productId: String
    ): List<PriceDto>

    @GET("price-service/api/prices/product/{product_id}/days/{days_amount}")
    suspend fun getPricesByProductIdLastDays(
        @Path("product_id") productId: String,
        @Path("days_amount") daysAmount: Int
    ): List<PriceDto>

    @GET("price-service/api/prices/best/{product_id}/{city}")
    suspend fun getBestPriceByProductIdAndCity(
        @Path("product_id") productId: String,
        @Path("city") city: String
    ): PriceDto

    @GET("price-service/api/prices/difference/{product_id}/{city}")
    suspend fun getPriceDifferenceByProductIdAndCity(
        @Path("product_id") productId: String,
        @Path("city") city: String,
        @Query("day_amount") dayAmount: Int = 7
    ): Double
}