package com.example.myapplicationtrendprice.data

data class ReceiptAnalysis(
    val qrText: String,
    val shopName: String,
    val totalPrice: Double,
    val items: List<Product>,
    val advice: String
)