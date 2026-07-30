package com.example.myapplicationtrendprice.remote

data class StoreBranchDto(
    val id: String? = null,
    val store: StoreDto? = null,
    val status: StoreStatusDto? = null,
    val openHours: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)