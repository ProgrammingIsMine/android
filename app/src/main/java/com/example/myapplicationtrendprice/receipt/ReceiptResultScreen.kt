package com.example.myapplicationtrendprice.receipt

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplicationtrendprice.data.Product
import com.example.myapplicationtrendprice.data.ReceiptAnalysis
import com.example.myapplicationtrendprice.ui.AppBackground
import com.example.myapplicationtrendprice.ui.BrandGreen
import com.example.myapplicationtrendprice.ui.BrandGreenDark
import com.example.myapplicationtrendprice.ui.BrandGreenSoft
import com.example.myapplicationtrendprice.ui.CardWhite
import com.example.myapplicationtrendprice.ui.TextPrimary
import com.example.myapplicationtrendprice.ui.TextSecondary
import com.example.myapplicationtrendprice.viewmodel.ShopViewModel

@Composable
fun ReceiptResultScreen(
    analysis: ReceiptAnalysis?,
    onBackClick: () -> Unit,
    onScanAgainClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 26.dp, bottom = 26.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            ReceiptHeader(
                onBackClick = onBackClick,
                onScanAgainClick = onScanAgainClick
            )
        }

        if (analysis == null) {
            item {
                EmptyReceiptCard(onScanAgainClick = onScanAgainClick)
            }
        } else {
            item { ReceiptSummaryCard(analysis = analysis) }

            item {
                Text(
                    text = "Detected products",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge
                )
            }

            items(analysis.items) { product ->
                ReceiptProductCard(product = product)
            }
        }
    }
}

@Composable
private fun ReceiptHeader(
    onBackClick: () -> Unit,
    onScanAgainClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.elevatedCardColors(BrandGreenDark),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 7.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(BrandGreenDark, BrandGreen)))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = Color.White.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "Receipt AI Analysis",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }

                Text(
                    text = "Smart receipt results",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = "See total price, scanned QR data, and money-saving advice from the app.",
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodyMedium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onScanAgainClick,
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = BrandGreenDark
                        )
                    ) { Text("Scan again", fontWeight = FontWeight.Black) }

                    OutlinedButton(
                        onClick = onBackClick,
                        shape = RoundedCornerShape(17.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) { Text("Home", fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}

@Composable
private fun EmptyReceiptCard(onScanAgainClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(CardWhite),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("No receipt scanned yet", color = TextPrimary, style = MaterialTheme.typography.titleLarge)
            Text("Scan a QR code from a receipt to generate comparison and saving recommendations.", color = TextSecondary)
            Button(
                onClick = onScanAgainClick,
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) { Text("Open scanner", fontWeight = FontWeight.Black) }
        }
    }
}

@Composable
private fun ReceiptSummaryCard(analysis: ReceiptAnalysis) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(CardWhite),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = analysis.shopName, color = TextPrimary, style = MaterialTheme.typography.titleLarge)
                    Text(text = "Detected shop", color = TextSecondary, fontSize = 13.sp)
                }

                Surface(color = BrandGreenSoft, shape = RoundedCornerShape(18.dp)) {
                    Text(
                        text = "${ShopViewModel.formatPrice(analysis.totalPrice)} ₸",
                        color = BrandGreenDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFF7FAF8))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("AI recommendation", color = BrandGreen, fontWeight = FontWeight.Black)
                Text(analysis.advice, color = TextPrimary, style = MaterialTheme.typography.bodyMedium)
            }

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("QR text", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(
                    text = analysis.qrText,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ReceiptProductCard(product: Product) {
    val price = product.currentPrice ?: product.price

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(CardWhite),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(BrandGreenSoft)
                    .padding(horizontal = 13.dp, vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("TP", color = BrandGreenDark, fontWeight = FontWeight.Black)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("${product.shop} • ${product.category}", color = TextSecondary, fontSize = 13.sp)
            }

            Text(
                text = "${ShopViewModel.formatPrice(price)} ₸",
                color = BrandGreenDark,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }
    }
}
