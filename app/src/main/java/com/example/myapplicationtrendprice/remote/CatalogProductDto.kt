package com.example.myapplicationtrendprice.remote

import com.google.gson.JsonElement

data class CatalogProductDto(
    val id: String,
    val title: String? = null,
    val type: String? = null,
    val brand: String? = null,
    val barcode: String? = null,
    val categories: List<CategoryDto> = emptyList(),
    val bestPrice: PriceDto? = null,
    val difference: Double? = null,
    val store: JsonElement? = null
)