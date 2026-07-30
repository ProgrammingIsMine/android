package com.example.myapplicationtrendprice.remote

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ImageApi {

    @Multipart
    @POST("image-service/api/images/user")
    suspend fun uploadUserImage(
        @Query("userId") userId: Long,
        @Part file: MultipartBody.Part
    ): ImageUploadResponse

    @Multipart
    @POST("image-service/api/images/product")
    suspend fun uploadProductImage(
        @Query("productId") productId: String,
        @Part file: MultipartBody.Part
    ): ImageUploadResponse

    @GET("image-service/api/images/user/{userId}")
    suspend fun getUserImage(
        @Path("userId") userId: Long
    ): ResponseBody

    @DELETE("image-service/api/images/user/{userId}")
    suspend fun deleteUserImage(
        @Path("userId") userId: Long
    )

    @GET("image-service/api/images/product/{productId}")
    suspend fun getProductImage(
        @Path("productId") productId: String
    ): ResponseBody

    @DELETE("image-service/api/images/product/{productId}")
    suspend fun deleteProductImage(
        @Path("productId") productId: String
    )
}