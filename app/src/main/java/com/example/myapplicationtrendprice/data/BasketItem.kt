package com.example.myapplicationtrendprice.data

data class BasketItem(
    val product: Product,
    val quantity: Int
) {
    val subtotal: Double
        get() = product.price * quantity
}