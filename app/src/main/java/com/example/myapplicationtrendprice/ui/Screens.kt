package com.example.myapplicationtrendprice.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplicationtrendprice.data.BasketItem
import com.example.myapplicationtrendprice.data.Product
import com.example.myapplicationtrendprice.viewmodel.ShopViewModel

@Composable
fun HomeScreen(
    viewModel: ShopViewModel,
    currentRoute: String,
    onOpenBasket: () -> Unit,
    onOpenScanner: () -> Unit,
    onOpenAnalytics: (String) -> Unit,
    onNavigate: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var message by remember {
        mutableStateOf("Ready. Choose a product or scan receipt.")
    }

    val allProducts = viewModel.products.value

    val categories = listOf("All") + allProducts
        .map { it.category }
        .distinct()
        .take(8)

    val products = allProducts.filter { product ->
        val categoryOk = selectedCategory == "All" || product.category == selectedCategory
        val queryOk = query.isBlank() ||
                product.name.contains(query, ignoreCase = true) ||
                product.category.contains(query, ignoreCase = true)

        categoryOk && queryOk
    }

    AppScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppHeader(
                    title = "TrendPrice",
                    subtitle = "professional grocery comparison",
                    rightText = "Basket ${viewModel.basketCount()}",
                    onRightClick = onOpenBasket
                )
            }

            item {
                SearchCard(
                    query = query,
                    onQueryChange = {
                        query = it
                        viewModel.searchProducts(it)
                    },
                    onSearchClick = {
                        message = "Search completed: ${products.size} products found."
                    },
                    onScannerClick = onOpenScanner
                )
            }

            item {
                HeroCard(
                    onScannerClick = onOpenScanner
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Products",
                        value = allProducts.size.toString(),
                        note = "catalog"
                    )

                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Basket",
                        value = viewModel.basketCount().toString(),
                        note = "items"
                    )

                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Stores",
                        value = allProducts.map { it.shop }.distinct().size.toString(),
                        note = "active"
                    )
                }
            }

            item {
                InfoCard(
                    title = "Result",
                    message = message
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    items(categories) { category ->
                        Chip(
                            text = category,
                            selected = selectedCategory == category,
                            onClick = {
                                selectedCategory = category
                                message = "Category selected: $category"
                            }
                        )
                    }
                }
            }

            when {
                viewModel.isLoading.value -> {
                    item {
                        InfoCard(
                            title = "Loading",
                            message = "Products are loading from backend..."
                        )
                    }
                }

                products.isEmpty() -> {
                    item {
                        InfoCard(
                            title = "No products",
                            message = viewModel.errorMessage.value ?: "Try another search or category.",
                            action = "Reload",
                            onAction = {
                                viewModel.loadProducts()
                            }
                        )
                    }
                }

                else -> {
                    item {
                        SectionTitle(
                            title = "Catalog",
                            action = "Reload",
                            onAction = {
                                viewModel.loadProducts()
                            }
                        )
                    }

                    items(products.chunked(2)) { row ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            row.forEach { product ->
                                ProductCard(
                                    product = product,
                                    modifier = Modifier.weight(1f),
                                    onAdd = {
                                        viewModel.addToBasket(product)
                                        message = "${product.name} added to basket."
                                    },
                                    onOpenAnalytics = {
                                        onOpenAnalytics(product.id)
                                    }
                                )
                            }

                            if (row.size == 1) {
                                Spacer(
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen(
    viewModel: ShopViewModel,
    selectedProductId: String?,
    currentRoute: String,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    BackHandler {
        onBack()
    }

    val products = viewModel.products.value

    var selectedProduct by remember(selectedProductId, products) {
        mutableStateOf(
            products.firstOrNull { it.id == selectedProductId }
                ?: products.firstOrNull()
        )
    }

    val product = selectedProduct

    AppScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppHeader(
                    title = "Analytics",
                    subtitle = "price history and store comparison",
                    rightText = "Back",
                    onRightClick = onBack
                )
            }

            if (products.isEmpty() || product == null) {
                item {
                    InfoCard(
                        title = "No analytics",
                        message = "Products are not loaded yet.",
                        action = "Load products",
                        onAction = {
                            viewModel.loadProducts()
                        }
                    )
                }
            } else {
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(products.take(15)) { item ->
                            Chip(
                                text = item.name.take(18),
                                selected = item.id == product.id,
                                onClick = {
                                    selectedProduct = item
                                }
                            )
                        }
                    }
                }

                item {
                    ProductAnalyticsSummary(product)
                }

                item {
                    PriceChart(product)
                }

                item {
                    StoreComparison(product)
                }
            }
        }
    }
}

@Composable
private fun ProductAnalyticsSummary(
    product: Product
) {
    val price = product.currentPrice ?: product.price
    val old = product.oldPrice ?: price + 90
    val saving = (old - price).coerceAtLeast(0.0)

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF0F3F2))
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = product.name,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "${product.shop} • ${product.category}",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Current",
                    value = "${ShopViewModel.formatPrice(price)} ₸",
                    note = "today"
                )

                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Old",
                    value = "${ShopViewModel.formatPrice(old)} ₸",
                    note = "before"
                )

                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Save",
                    value = "${ShopViewModel.formatPrice(saving)} ₸",
                    note = "possible"
                )
            }
        }
    }
}

@Composable
private fun PriceChart(
    product: Product
) {
    val price = product.currentPrice ?: product.price

    val points = listOf(
        price + 95,
        price + 74,
        price + 60,
        price + 41,
        price + 38,
        price + 20,
        price
    )

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SectionTitle(
                title = "7-day price trend"
            )

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF8FAF9))
                    .padding(14.dp)
            ) {
                val left = 24.dp.toPx()
                val right = size.width - 24.dp.toPx()
                val top = 26.dp.toPx()
                val bottom = size.height - 28.dp.toPx()

                val chartWidth = right - left
                val chartHeight = bottom - top

                val min = points.minOrNull() ?: 0.0
                val max = points.maxOrNull() ?: 1.0
                val range = (max - min).takeIf { it > 0 } ?: 1.0

                repeat(4) { index ->
                    val y = top + chartHeight * index / 3f

                    drawLine(
                        color = Color(0xFFE3E9E6),
                        start = Offset(left, y),
                        end = Offset(right, y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                val path = Path()

                points.forEachIndexed { index, value ->
                    val x = left + chartWidth * index / (points.size - 1).toFloat()
                    val y = bottom - ((value - min) / range).toFloat() * chartHeight

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }

                    drawCircle(
                        color = BrandGreen,
                        radius = 5.dp.toPx(),
                        center = Offset(x, y)
                    )
                }

                drawPath(
                    path = path,
                    color = BrandGreen,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 3.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }
        }
    }
}

@Composable
private fun StoreComparison(
    product: Product
) {
    val price = product.currentPrice ?: product.price

    val rows = listOf(
        Triple("Magnum", price, "Best price"),
        Triple("Small", price + 45, "+45 ₸"),
        Triple("Dina", price + 72, "+72 ₸"),
        Triple("Galmart", price + 110, "+110 ₸")
    )

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SectionTitle(
                title = "Store comparison"
            )

            rows.forEachIndexed { index, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (index == 0) BrandGreenSoft else Color(0xFFF8FAF9)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = row.first,
                        modifier = Modifier.weight(1f),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${ShopViewModel.formatPrice(row.second)} ₸",
                        modifier = Modifier.weight(1f),
                        color = TextPrimary,
                        fontWeight = FontWeight.Black
                    )

                    Text(
                        text = row.third,
                        color = if (index == 0) BrandGreen else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun BasketScreen(
    viewModel: ShopViewModel,
    currentRoute: String,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
) {
    BackHandler {
        onBack()
    }

    var message by remember {
        mutableStateOf("Basket is ready.")
    }

    AppScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AppHeader(
                    title = "Basket",
                    subtitle = "your selected products",
                    rightText = "Back",
                    onRightClick = onBack
                )
            }

            item {
                InfoCard(
                    title = "Result",
                    message = message
                )
            }

            if (viewModel.basket.isEmpty()) {
                item {
                    InfoCard(
                        title = "Empty basket",
                        message = "Go to catalog and add products.",
                        action = "Open catalog",
                        onAction = {
                            onNavigate("home")
                        }
                    )
                }
            } else {
                items(viewModel.basket) { item ->
                    BasketItemCard(
                        item = item,
                        onPlus = {
                            viewModel.addToBasket(item.product)
                            message = "Quantity increased."
                        },
                        onMinus = {
                            viewModel.decreaseQuantity(item.product)
                            message = "Quantity changed."
                        },
                        onRemove = {
                            viewModel.removeFromBasket(item.product)
                            message = "Product removed."
                        }
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(BrandGreenDark)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Text(
                                text = "Total",
                                color = Color.White.copy(alpha = .8f),
                                fontSize = 15.sp
                            )

                            Text(
                                text = "${ShopViewModel.formatPrice(viewModel.basketTotal())} ₸",
                                color = Color.White,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        viewModel.clearBasket()
                                        message = "Basket cleared."
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = TextPrimary
                                    )
                                ) {
                                    Text("Clear")
                                }

                                Button(
                                    onClick = {
                                        message = "Order simulation: products checked and compared."
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(18.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = BrandGreen
                                    )
                                ) {
                                    Text("Compare")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BasketItemCard(
    item: BasketItem,
    onPlus: () -> Unit,
    onMinus: () -> Unit,
    onRemove: () -> Unit
) {
    val product = item.product

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(82.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFF0F3F2))
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.shop,
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Text(
                    text = "${ShopViewModel.formatPrice(item.subtotal)} ₸",
                    color = BrandGreen,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SmallActionButton(
                        text = "−",
                        onClick = onMinus
                    )

                    Text(
                        text = item.quantity.toString(),
                        fontWeight = FontWeight.Black
                    )

                    SmallActionButton(
                        text = "+",
                        onClick = onPlus
                    )

                    SmallActionButton(
                        text = "Remove",
                        danger = true,
                        onClick = onRemove
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    var name by remember {
        mutableStateOf("Abzal Student")
    }

    var phone by remember {
        mutableStateOf("+7 777 000 00 00")
    }

    var city by remember {
        mutableStateOf("Almaty")
    }

    var saved by remember {
        mutableStateOf("Profile is not changed yet.")
    }

    AppScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppHeader(
                    title = "Profile",
                    subtitle = "personal settings and account"
                )
            }

            item {
                InfoCard(
                    title = "Status",
                    message = saved
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(CardWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {
                                name = it
                            },
                            label = {
                                Text("Name")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = {
                                phone = it
                            },
                            label = {
                                Text("Phone")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        )

                        OutlinedTextField(
                            value = city,
                            onValueChange = {
                                city = it
                            },
                            label = {
                                Text("City")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        )

                        Button(
                            onClick = {
                                saved = "Saved: $name, $phone, $city"
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen
                            )
                        ) {
                            Text(
                                text = "Save profile",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                InfoCard(
                    title = "Diploma functions",
                    message = "Profile page imitates user settings. You can extend it later with login, JWT token and backend endpoints from the website."
                )
            }
        }
    }
}

@Composable
fun AdminScreen(
    viewModel: ShopViewModel,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val logs = remember {
        mutableStateListOf("Admin panel opened")
    }

    var productName by remember {
        mutableStateOf("Demo product")
    }

    var priceText by remember {
        mutableStateOf("499")
    }

    AppScaffold(
        currentRoute = currentRoute,
        onNavigate = onNavigate
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppHeader(
                    title = "Admin",
                    subtitle = "management dashboard"
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Products",
                        value = viewModel.products.value.size.toString(),
                        note = "loaded"
                    )

                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Basket",
                        value = viewModel.basketCount().toString(),
                        note = "items"
                    )

                    MetricCard(
                        modifier = Modifier.weight(1f),
                        title = "Errors",
                        value = if (viewModel.errorMessage.value == null) "0" else "1",
                        note = "backend"
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(26.dp),
                    colors = CardDefaults.cardColors(CardWhite),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Create product simulation",
                            color = TextPrimary,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Black
                        )

                        OutlinedTextField(
                            value = productName,
                            onValueChange = {
                                productName = it
                            },
                            label = {
                                Text("Product name")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        )

                        OutlinedTextField(
                            value = priceText,
                            onValueChange = {
                                priceText = it
                            },
                            label = {
                                Text("Price")
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    logs.add(
                                        0,
                                        "Created: $productName for $priceText ₸"
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandGreen
                                )
                            ) {
                                Text("Create")
                            }

                            Button(
                                onClick = {
                                    viewModel.loadProducts()
                                    logs.add(0, "Catalog reload requested")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandGreenDark
                                )
                            ) {
                                Text("Reload")
                            }
                        }
                    }
                }
            }

            item {
                SectionTitle(
                    title = "Admin logs"
                )
            }

            items(logs) { log ->
                InfoCard(
                    title = "Action",
                    message = log
                )
            }
        }
    }
}