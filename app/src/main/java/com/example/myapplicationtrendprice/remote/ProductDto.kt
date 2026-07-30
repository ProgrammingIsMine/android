package com.example.myapplicationtrendprice.remote

import com.google.gson.JsonObject

data class CatalogProductsResponse(
    val content: List<CatalogProductDto> = emptyList(),
    val number: Int? = null,
    val pageNumber: Int? = null,
    val size: Int? = null,
    val totalPages: Int? = null,
    val totalElements: Long? = null,
    val first: Boolean? = null,
    val last: Boolean? = null
)


data class ProductPriceViewWithCategory(
    val product: ProductInfoDto? = null,
    val categories: List<CategoryDto> = emptyList(),
    val prices: List<PriceDto> = emptyList(),
    val bestPrice: PriceDto? = null
)

data class ProductInfoDto(
    val id: String? = null,
    val title: String? = null,
    val type: String? = null,
    val brand: BrandDto? = null,
    val barcode: String? = null,
    val categories: List<CategoryDto> = emptyList()
)

data class BrandDto(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null
)

data class CategoryDto(
    val id: String? = null,
    val title: String? = null
)

data class PriceDto(
    val id: String? = null,
    val productId: String? = null,
    val storeId: String? = null,
    val unitAmount: Double? = null,
    val unit: String? = null,
    val pricePerUnit: Double? = null,
    val currency: String? = null,
    val city: String? = null,
    val discount: Double? = null,
    val finalPrice: Double? = null,
    val time: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)