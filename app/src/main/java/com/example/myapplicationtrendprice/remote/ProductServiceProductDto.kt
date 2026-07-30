package com.example.myapplicationtrendprice.remote

data class ProductServiceProductDto(
    val id: String? = null,
    val title: String? = null,
    val type: String? = null,
    val brand: BrandDto? = null,
    val categories: List<CategoryDto> = emptyList(),
    val barcode: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val checked: Boolean? = null
)