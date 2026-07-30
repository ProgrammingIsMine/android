package com.example.myapplicationtrendprice.remote

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface CatalogApi {

    @GET("catalog-service/catalog/isAlive")
    suspend fun isAlive(): Map<String, String>

    @GET("catalog-service/catalog/products/search")
    suspend fun searchProducts(
        @Query("page_number") pageNumber: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("title") title: String? = null,
        @Query("category") category: List<String>? = null,
        @Query("brand") brand: List<String>? = null,
        @Query("type") type: List<String>? = null,
        @Query("city") city: String = "almaty",
        @Query("day_amount") dayAmount: Int = 7
    ): CatalogProductsResponse

    @GET("catalog-service/catalog/products/page/{page_number}/size/{page_size}/city/{city}")
    suspend fun getProductsPage(
        @Path("page_number") pageNumber: Int,
        @Path("page_size") pageSize: Int,
        @Path("city") city: String
    ): CatalogProductsResponse

    @GET("catalog-service/catalog/products/{productId}/prices/{dayAmount}")
    suspend fun getProductPrices(
        @Path("productId") productId: String,
        @Path("dayAmount") dayAmount: Int = 7
    ): ProductPriceViewWithCategory

    @GET("catalog-service/debug/jwt/parse")
    suspend fun parseJwt(
        @Header("Authorization") authorization: String
    ): Map<String, Any>

    @GET("catalog-service/debug/jwt/admin-check")
    suspend fun adminCheck(
        @Header("Authorization") authorization: String
    ): Map<String, Any>
}