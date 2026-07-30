package com.example.myapplicationtrendprice.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplicationtrendprice.data.Product
import com.example.myapplicationtrendprice.viewmodel.ShopViewModel

@Composable
fun AppScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    androidx.compose.material3.Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onNavigate = onNavigate
            )
        },
        content = content
    )
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        NavItem("home", "Home", "⌂"),
        NavItem("analytics", "Analytics", "↗"),
        NavItem("basket", "Basket", "▣"),
        NavItem("profile", "Profile", "◉"),
        NavItem("admin", "Admin", "⚙")
    )

    Surface(
        color = CardWhite,
        tonalElevation = 8.dp,
        shadowElevation = 18.dp
    ) {
        NavigationBar(
            containerColor = CardWhite,
            tonalElevation = 0.dp,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            items.forEach { item ->
                val selected = currentRoute == item.route
                NavigationBarItem(
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    icon = {
                        Text(
                            text = item.icon,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    },
                    label = {
                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandGreen,
                        selectedTextColor = BrandGreenDark,
                        indicatorColor = BrandGreenSoft,
                        unselectedIconColor = TextSecondary,
                        unselectedTextColor = TextSecondary
                    )
                )
            }
        }
    }
}

data class NavItem(
    val route: String,
    val title: String,
    val icon: String
)

@Composable
fun AppHeader(
    title: String,
    subtitle: String,
    rightText: String? = null,
    onRightClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(10.dp, RoundedCornerShape(18.dp), clip = false)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BrandGreen, BrandGreenDark)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "TP",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 0.6.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.headlineMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = subtitle,
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (rightText != null && onRightClick != null) {
            FilledTonalButton(
                onClick = onRightClick,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = BrandGreenSoft,
                    contentColor = BrandGreenDark
                )
            ) {
                Text(
                    text = rightText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge
        )

        if (action != null) {
            TextButton(onClick = { onAction?.invoke() }) {
                Text(
                    text = action,
                    color = BrandGreen,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
fun SearchCard(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onScannerClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.elevatedCardColors(CardWhite),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                placeholder = { Text("Search milk, bread, apple...") },
                leadingIcon = {
                    Text("⌕", color = TextSecondary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandGreen,
                    unfocusedBorderColor = BorderColor,
                    focusedContainerColor = Color(0xFFFBFDFC),
                    unfocusedContainerColor = Color(0xFFFBFDFC)
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onSearchClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    contentPadding = PaddingValues(vertical = 13.dp)
                ) {
                    Text("Search", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onScannerClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreenDark),
                    contentPadding = PaddingValues(vertical = 13.dp)
                ) {
                    Text("Scan QR", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HeroCard(
    onScannerClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp),
        shape = RoundedCornerShape(34.dp),
        colors = CardDefaults.cardColors(BrandGreenDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(PremiumDark, BrandGreenDark, BrandGreen)
                    )
                )
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(118.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.10f))
            )
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
            )

            Column(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalArrangement = Arrangement.spacedBy(13.dp)
            ) {
                Surface(
                    color = Color.White.copy(alpha = .15f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "Live prices • smarter shopping",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 7.dp)
                    )
                }

                Text(
                    text = "Compare prices\nand save money",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall
                )

                Button(
                    onClick = onScannerClick,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = BrandGreenDark
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 11.dp)
                ) {
                    Text("Scan receipt", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    note: String
) {
    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(CardWhite),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(text = title, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text(text = value, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Black, maxLines = 1)
            Text(text = note, color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    modifier: Modifier = Modifier,
    onAdd: () -> Unit,
    onOpenAnalytics: () -> Unit
) {
    val price = product.currentPrice ?: product.price
    val discount = product.discount ?: 0.0

    ElevatedCard(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(CardWhite),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFFF7FAF8), Color(0xFFEFF5F2))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(102.dp)
                        .padding(10.dp)
                )

                if (discount > 0.0) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        color = Color(0xFFFFF4E2),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = ShopViewModel.formatDiscount(discount),
                            color = Color(0xFFB55B00),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = product.name,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                lineHeight = 18.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${product.shop} • ${product.category}",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${ShopViewModel.formatPrice(price)} ₸",
                color = BrandGreenDark,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(11.dp))

            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                contentPadding = PaddingValues(vertical = 11.dp)
            ) {
                Text("+ Add", fontWeight = FontWeight.Black)
            }

            TextButton(
                onClick = onOpenAnalytics,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "View analytics",
                    color = BrandGreen,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    message: String,
    action: String? = null,
    onAction: (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.elevatedCardColors(CardWhite),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(BrandGreenSoft),
                contentAlignment = Alignment.Center
            ) {
                Text("→", color = BrandGreen, fontSize = 22.sp, fontWeight = FontWeight.Black)
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(text = title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(text = message, color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp)

                if (action != null && onAction != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Button(
                        onClick = onAction,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                    ) {
                        Text(action, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun Chip(
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (selected) BrandGreen else CardWhite,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) BrandGreen else BorderColor
        ),
        tonalElevation = if (selected) 4.dp else 0.dp,
        shadowElevation = if (selected) 3.dp else 0.dp
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)
        )
    }
}

@Composable
fun SmallActionButton(
    text: String,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = if (danger) Color(0xFFFFEEEE) else BrandGreenSoft,
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(1.dp, if (danger) Color(0xFFFFD6D6) else Color(0xFFD4EFE2))
    ) {
        Text(
            text = text,
            color = if (danger) DangerRed else BrandGreenDark,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp)
        )
    }
}
