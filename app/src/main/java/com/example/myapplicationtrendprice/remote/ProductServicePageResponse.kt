package com.example.myapplicationtrendprice.remote

data class ProductServicePageResponse(
    val content: List<ProductServiceProductDto> = emptyList(),
    val number: Int? = null,
    val pageNumber: Int? = null,
    val size: Int? = null,
    val totalPages: Int? = null,
    val totalElements: Long? = null,
    val first: Boolean? = null,
    val last: Boolean? = null
)