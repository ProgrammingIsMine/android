package com.example.myapplicationtrendprice.data

data class Product(
    val id: String,
    val name: String,
    val shop: String,
    val price: Double,              // текущая цена для совместимости
    val oldPrice: Double? = null,   // старая цена
    val currentPrice: Double? = null, // текущая цена
    val discount: Double? = null,   // скидка в %
    val category: String,
    val brand: String? = null,
    val imageUrl: String? = null,
    val difference: Double? = null
)