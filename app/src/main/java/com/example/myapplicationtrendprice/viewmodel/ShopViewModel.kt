package com.example.myapplicationtrendprice.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplicationtrendprice.data.BasketItem
import com.example.myapplicationtrendprice.data.Product
import com.example.myapplicationtrendprice.data.ReceiptAnalysis
import com.example.myapplicationtrendprice.remote.ApiClient
import com.example.myapplicationtrendprice.remote.CatalogProductDto
import com.example.myapplicationtrendprice.remote.PriceDto
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class ShopViewModel : ViewModel() {

    var products = mutableStateOf<List<Product>>(emptyList())
        private set

    val basket = mutableStateListOf<BasketItem>()

    var receiptAnalysis = mutableStateOf<ReceiptAnalysis?>(null)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    var selectedCity = mutableStateOf("almaty")
        private set

    var priceHistory = mutableStateOf<List<Double>>(emptyList())
        private set

    private var searchJob: Job? = null

    init {
        loadProducts()
    }

    fun changeCity(city: String) {
        selectedCity.value = city.lowercase()
        loadProducts(city = selectedCity.value)
    }

    fun loadProducts(
        search: String? = null,
        city: String = selectedCity.value
    ) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null

                val response = ApiClient.catalogApi.searchProducts(
                    pageNumber = 1,
                    pageSize = 30,
                    title = search?.trim()?.takeIf { it.isNotBlank() },
                    city = city,
                    dayAmount = 7
                )

                products.value = response.content.map { item ->
                    item.toProduct()
                }
            } catch (e: Exception) {
                products.value = demoProducts()
                errorMessage.value = "Backend is unavailable. Demo catalog is shown for presentation."
            } finally {
                isLoading.value = false
            }
        }
    }

    fun searchProducts(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(350)
            loadProducts(search = query)
        }
    }

    fun loadPriceHistory(productId: String, days: Int = 7) {
        viewModelScope.launch {
            try {
                val prices = ApiClient.priceApi.getPricesByProductIdLastDays(
                    productId = productId,
                    daysAmount = days
                )

                priceHistory.value = prices
                    .mapNotNull { it.actualPrice() }
                    .ifEmpty { fallbackPriceHistory(productId) }
            } catch (_: Exception) {
                priceHistory.value = fallbackPriceHistory(productId)
            }
        }
    }

    fun addToBasket(product: Product) {
        val existingItem = basket.find { item ->
            item.product.id == product.id
        }

        if (existingItem == null) {
            basket.add(
                BasketItem(
                    product = product,
                    quantity = 1
                )
            )
        } else {
            val index = basket.indexOf(existingItem)
            basket[index] = existingItem.copy(
                quantity = existingItem.quantity + 1
            )
        }
    }

    fun decreaseQuantity(product: Product) {
        val existingItem = basket.find { item ->
            item.product.id == product.id
        } ?: return

        if (existingItem.quantity == 1) {
            basket.remove(existingItem)
        } else {
            val index = basket.indexOf(existingItem)
            basket[index] = existingItem.copy(
                quantity = existingItem.quantity - 1
            )
        }
    }

    fun removeFromBasket(product: Product) {
        basket.removeAll { item ->
            item.product.id == product.id
        }
    }

    fun clearBasket() {
        basket.clear()
    }

    fun basketTotal(): Double {
        return basket.sumOf { item ->
            item.subtotal
        }
    }

    fun basketCount(): Int {
        return basket.sumOf { item ->
            item.quantity
        }
    }

    fun analyzeReceiptQr(qrText: String) {
        analyzeScannedReceipt(qrText)
    }

    fun analyzeScannedReceipt(qrContent: String) {
        viewModelScope.launch {
            if (qrContent.isBlank()) {
                receiptAnalysis.value = ReceiptAnalysis(
                    qrText = "",
                    shopName = "Unknown shop",
                    totalPrice = 0.0,
                    items = emptyList(),
                    advice = "The QR code is empty. Please scan another receipt."
                )
                return@launch
            }

            val detectedShop = detectShop(qrContent)
            val receiptItems = products.value.take(5)
            val total = receiptItems.sumOf { product ->
                product.currentPrice ?: product.price
            }

            receiptAnalysis.value = ReceiptAnalysis(
                qrText = qrContent,
                shopName = detectedShop,
                totalPrice = total,
                items = receiptItems,
                advice = buildSavingsAdvice(receiptItems)
            )
        }
    }

    private fun detectShop(qrText: String): String {
        return when {
            qrText.contains("magnum", ignoreCase = true) -> "Magnum"
            qrText.contains("small", ignoreCase = true) -> "Small"
            qrText.contains("dina", ignoreCase = true) -> "Dina"
            qrText.contains("toimart", ignoreCase = true) -> "Toimart"
            qrText.contains("galmart", ignoreCase = true) -> "Galmart"
            else -> "Unknown shop"
        }
    }

    private fun demoProducts(): List<Product> {
        return listOf(
            Product(
                id = "demo-1",
                name = "Milk 3.2% 1L",
                shop = "Magnum",
                price = 489.0,
                oldPrice = 559.0,
                currentPrice = 489.0,
                discount = 12.5,
                category = "Milk",
                brand = "FoodMaster",
                imageUrl = ApiClient.productImageUrl("demo-1")
            ),
            Product(
                id = "demo-2",
                name = "Bread sliced classic",
                shop = "Small",
                price = 189.0,
                oldPrice = 220.0,
                currentPrice = 189.0,
                discount = 14.0,
                category = "Bread",
                brand = "Aksai",
                imageUrl = ApiClient.productImageUrl("demo-2")
            ),
            Product(
                id = "demo-3",
                name = "Apple red 1 kg",
                shop = "Dina",
                price = 640.0,
                oldPrice = 710.0,
                currentPrice = 640.0,
                discount = 9.8,
                category = "Fruits",
                brand = "KazFresh",
                imageUrl = ApiClient.productImageUrl("demo-3")
            ),
            Product(
                id = "demo-4",
                name = "Chicken breast 1 kg",
                shop = "Magnum",
                price = 2190.0,
                oldPrice = 2490.0,
                currentPrice = 2190.0,
                discount = 12.0,
                category = "Meat",
                brand = "Halal",
                imageUrl = ApiClient.productImageUrl("demo-4")
            ),
            Product(
                id = "demo-5",
                name = "Rice long grain 900 g",
                shop = "Galmart",
                price = 799.0,
                oldPrice = 899.0,
                currentPrice = 799.0,
                discount = 11.0,
                category = "Groceries",
                brand = "Asia",
                imageUrl = ApiClient.productImageUrl("demo-5")
            ),
            Product(
                id = "demo-6",
                name = "Cola drink 1L",
                shop = "Toimart",
                price = 395.0,
                oldPrice = 450.0,
                currentPrice = 395.0,
                discount = 12.0,
                category = "Drinks",
                brand = "Coca-Cola",
                imageUrl = ApiClient.productImageUrl("demo-6")
            )
        )
    }

    private fun buildSavingsAdvice(items: List<Product>): String {
        if (items.isEmpty()) {
            return "No products loaded yet. Refresh the catalog and scan the receipt again."
        }

        val cheapestItem = items.minByOrNull { product ->
            product.currentPrice ?: product.price
        }

        val mostExpensiveItem = items.maxByOrNull { product ->
            product.currentPrice ?: product.price
        }

        return buildString {
            append("Savings advice: ")

            if (cheapestItem != null) {
                append("${cheapestItem.name} has a good price: ")
                append("${formatPrice(cheapestItem.currentPrice ?: cheapestItem.price)} KZT in ${cheapestItem.shop}. ")
            }

            if (mostExpensiveItem != null && cheapestItem != null && mostExpensiveItem.id != cheapestItem.id) {
                append("Before buying ${mostExpensiveItem.name}, compare it with other stores because it is one of the expensive items. ")
            }

            append("Use TrendPrice before checkout to reduce daily grocery expenses.")
        }
    }

    private fun fallbackPriceHistory(productId: String): List<Double> {
        val product = products.value.firstOrNull { item ->
            item.id == productId
        }

        val currentPrice = product?.currentPrice ?: product?.price ?: 0.0
        if (currentPrice <= 0.0) return emptyList()

        return listOf(
            currentPrice + 95,
            currentPrice + 74,
            currentPrice + 60,
            currentPrice + 41,
            currentPrice + 28,
            currentPrice + 12,
            currentPrice
        )
    }

    private fun PriceDto.actualPrice(): Double? {
        return finalPrice ?: pricePerUnit
    }

    private fun CatalogProductDto.toProduct(): Product {
        val category = categories.firstOrNull()?.title ?: type ?: "Other"

        val storeObject = store
            ?.takeIf { it.isJsonObject }
            ?.asJsonObject

        val shop = storeObject?.get("title")?.asStringOrNull()
            ?: storeObject?.get("name")?.asStringOrNull()
            ?: bestPrice?.city
            ?: "Unknown store"

        val oldPrice = bestPrice?.pricePerUnit
        val currentPrice = bestPrice?.finalPrice ?: bestPrice?.pricePerUnit ?: 0.0
        val discountPercent = normalizeDiscount(bestPrice?.discount)

        return Product(
            id = id,
            name = title ?: "Unnamed product",
            shop = shop,
            price = currentPrice,
            oldPrice = oldPrice,
            currentPrice = currentPrice,
            discount = discountPercent,
            category = category,
            brand = brand,
            imageUrl = ApiClient.productImageUrl(id),
            difference = difference
        )
    }

    private fun normalizeDiscount(value: Double?): Double? {
        if (value == null) return null

        return if (value in 0.0..1.0) {
            value * 100.0
        } else {
            value
        }
    }

    private fun com.google.gson.JsonElement?.asStringOrNull(): String? {
        return try {
            this
                ?.takeIf { element ->
                    element.isJsonPrimitive && !element.isJsonNull
                }
                ?.asString
                ?.takeIf { value ->
                    value.isNotBlank()
                }
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        fun formatPrice(value: Double): String {
            return if (value == value.roundToInt().toDouble()) {
                value.roundToInt().toString()
            } else {
                "%.2f".format(value)
            }
        }

        fun formatDiscount(value: Double): String {
            val normalized = if (value == value.roundToInt().toDouble()) {
                value.roundToInt().toString()
            } else {
                "%.1f".format(value)
            }

            return "-$normalized%"
        }
    }
}