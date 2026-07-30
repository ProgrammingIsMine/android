package com.example.myapplicationtrendprice.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("product-service/api/products")
    suspend fun getProducts(): List<ProductServiceProductDto>

    @POST("product-service/api/products")
    suspend fun createProduct(
        @Body product: ProductServiceProductDto
    ): ProductServiceProductDto

    @POST("product-service/api/products/massCreate")
    suspend fun createProducts(
        @Body products: List<ProductServiceProductDto>
    ): List<ProductServiceProductDto>

    @GET("product-service/api/products/{id}")
    suspend fun getProduct(
        @Path("id") id: String
    ): ProductServiceProductDto

    @PUT("product-service/api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body product: ProductServiceProductDto
    ): ProductServiceProductDto

    @DELETE("product-service/api/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: String
    ): ProductServiceProductDto

    @PUT("product-service/api/products/add_category/{product_id}")
    suspend fun addCategories(
        @Path("product_id") productId: String,
        @Body categoryIds: List<String>
    ): ProductServiceProductDto

    @GET("product-service/api/products/search")
    suspend fun searchProducts(
        @Query("page_number") pageNumber: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("title") title: String? = null,
        @Query("category") category: List<String>? = null,
        @Query("brand") brand: List<String>? = null,
        @Query("type") type: List<String>? = null
    ): ProductServicePageResponse

    @GET("product-service/api/products/page/{page_number}")
    suspend fun getProductsByPage(
        @Path("page_number") pageNumber: Int,
        @Query("page_size") pageSize: Int = 20
    ): ProductServicePageResponse

    @GET("product-service/api/categories")
    suspend fun getCategories(): List<CategoryDto>

    @POST("product-service/api/categories")
    suspend fun createCategory(
        @Body category: CategoryDto
    ): CategoryDto

    @GET("product-service/api/categories/{id}")
    suspend fun getCategory(
        @Path("id") id: String
    ): CategoryDto

    @PUT("product-service/api/categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: String,
        @Body category: CategoryDto
    ): CategoryDto

    @DELETE("product-service/api/categories/{id}")
    suspend fun deleteCategory(
        @Path("id") id: String
    ): CategoryDto

    @GET("product-service/api/categories/{id}/products")
    suspend fun getProductsByCategory(
        @Path("id") id: String
    ): List<ProductServiceProductDto>

    @GET("product-service/api/brands")
    suspend fun getBrands(): List<BrandDto>

    @POST("product-service/api/brands")
    suspend fun createBrand(
        @Body brand: BrandDto
    ): BrandDto

    @GET("product-service/api/brands/{id}")
    suspend fun getBrand(
        @Path("id") id: String
    ): BrandDto

    @PUT("product-service/api/brands/{id}")
    suspend fun updateBrand(
        @Path("id") id: String,
        @Body brand: BrandDto
    ): BrandDto

    @DELETE("product-service/api/brands/{id}")
    suspend fun deleteBrand(
        @Path("id") id: String
    ): BrandDto
}