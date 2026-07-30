package com.example.myapplicationtrendprice.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapplicationtrendprice.data.Product
import com.example.myapplicationtrendprice.viewmodel.ShopViewModel

@Composable
fun ProductCard(
    product: Product,
    onAddToCart: () -> Unit,
    onAnalyticsClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, androidx.compose.ui.graphics.Color(0xFFD7D7D7)),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier
                    .size(170.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = product.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${product.category} | ${product.shop}",
                fontSize = 14.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${ShopViewModel.formatPrice(product.currentPrice ?: product.price)}₸",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color(0xFF222222)
                    )

                    val shouldShowOldPrice =
                        product.oldPrice != null &&
                                product.currentPrice != null &&
                                product.oldPrice > product.currentPrice

                    if (shouldShowOldPrice) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${ShopViewModel.formatPrice(product.oldPrice)}₸",
                            fontSize = 14.sp,
                            color = androidx.compose.ui.graphics.Color.Gray,
                            style = TextStyle(
                                textDecoration = TextDecoration.LineThrough
                            )
                        )
                    }
                }

                val shouldShowDiscount =
                    product.discount != null && product.discount > 0

                if (shouldShowDiscount) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = androidx.compose.ui.graphics.Color(0xFFE7F8EF)
                    ) {
                        Text(
                            text = ShopViewModel.formatDiscount(product.discount),
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = androidx.compose.ui.graphics.Color(0xFF0E9F6E)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onAddToCart,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Add to cart")
                }

                OutlinedButton(
                    onClick = onAnalyticsClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Price analytics")
                }
            }
        }
    }
}