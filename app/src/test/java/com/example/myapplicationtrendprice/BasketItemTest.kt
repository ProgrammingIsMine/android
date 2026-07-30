package com.example.myapplicationtrendprice

import com.example.myapplicationtrendprice.data.BasketItem
import com.example.myapplicationtrendprice.data.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class BasketItemTest {

    @Test
    fun subtotal_isCalculatedFromProductPriceAndQuantity() {
        val product = Product(
            id = "milk-1",
            name = "Milk 3.2% 1L",
            shop = "Magnum",
            price = 500.0,
            category = "Milk"
        )

        val basketItem = BasketItem(
            product = product,
            quantity = 2
        )

        assertEquals(1000.0, basketItem.subtotal, 0.0)
    }
}