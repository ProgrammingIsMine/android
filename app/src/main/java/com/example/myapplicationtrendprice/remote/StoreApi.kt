package com.example.myapplicationtrendprice.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StoreApi {

    @GET("store-service/api/stores")
    suspend fun getStores(): List<StoreDto>

    @POST("store-service/api/stores")
    suspend fun createStore(
        @Body store: StoreDto
    ): StoreDto

    @GET("store-service/api/stores/{id}")
    suspend fun getStore(
        @Path("id") id: String
    ): StoreDto

    @PUT("store-service/api/stores/{id}")
    suspend fun updateStore(
        @Path("id") id: String,
        @Body store: StoreDto
    ): StoreDto

    @DELETE("store-service/api/stores/{id}")
    suspend fun deleteStore(
        @Path("id") id: String
    )

    @GET("store-service/api/stores/search")
    suspend fun searchStores(
        @Query("title") title: String
    ): List<StoreDto>

    @GET("store-service/api/statuses")
    suspend fun getStatuses(): List<StoreStatusDto>

    @POST("store-service/api/statuses")
    suspend fun createStatus(
        @Body status: StoreStatusDto
    ): StoreStatusDto

    @GET("store-service/api/statuses/{id}")
    suspend fun getStatus(
        @Path("id") id: String
    ): StoreStatusDto

    @PUT("store-service/api/statuses/{id}")
    suspend fun updateStatus(
        @Path("id") id: String,
        @Body status: StoreStatusDto
    ): StoreStatusDto

    @DELETE("store-service/api/statuses/{id}")
    suspend fun deleteStatus(
        @Path("id") id: String
    )

    @GET("store-service/api/statuses/search")
    suspend fun searchStatuses(
        @Query("title") title: String
    ): List<StoreStatusDto>

    @GET("store-service/api/store-branches")
    suspend fun getBranches(): List<StoreBranchDto>

    @POST("store-service/api/store-branches")
    suspend fun createBranch(
        @Body branch: StoreBranchDto
    ): StoreBranchDto

    @GET("store-service/api/store-branches/{id}")
    suspend fun getBranch(
        @Path("id") id: String
    ): StoreBranchDto

    @PATCH("store-service/api/store-branches/{id}")
    suspend fun updateBranch(
        @Path("id") id: String,
        @Body branch: StoreBranchDto
    ): StoreBranchDto

    @DELETE("store-service/api/store-branches/{id}")
    suspend fun deleteBranch(
        @Path("id") id: String
    )

    @GET("store-service/api/store-branches/search")
    suspend fun searchBranches(
        @Query("title") title: String? = null,
        @Query("status") status: String? = null,
        @Query("openHours") openHours: String? = null
    ): List<StoreBranchDto>
}