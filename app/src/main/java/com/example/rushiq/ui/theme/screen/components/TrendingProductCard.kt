package com.example.rushiq.ui.theme.screen.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.R

private const val TAG = "TrendingProductCard"

@Composable
fun TrendingProductCard(
    product: Products,
    onCategoryClick: (String) -> Unit
) {
    LaunchedEffect(product.id) {
        Log.d(TAG, "Rendering TrendingProductCard for product id=${product.id}, name=${product.name}")
    }

    var isFavorite by remember { mutableStateOf(false) }

    val discountPercentage = when (product.category?.lowercase()) {
        "electronics"     -> "UP TO 90% OFF"
        "jewelery"        -> "UP TO 85% OFF"
        "men's clothing"  -> "UP TO 80% OFF"
        "women's clothing"-> "UP TO 80% OFF"
        else              -> "UP TO 80% OFF"
    }

    // Main card container
    Card(
        modifier = Modifier
            .fillMaxSize() // This ensures the card fills the available space
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                product.category?.let { category ->
                    onCategoryClick(category)
                }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        // Column to arrange discount banner, image, and category text vertically
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Discount banner at the top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.15f))
                    .blur(radius = 0.5.dp)
            ) {
                Text(
                    text = discountPercentage,
                    color = Color(0xFF2864D4),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            // Product Image - This is the main fix
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // This makes the image take up remaining space
                    .background(Color.White)
                    .padding(8.dp), // Add some padding around the image
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberImagePainter(
                        data = product.imageUrl ?: "",
                        builder = {
                            placeholder(R.drawable.shopping_bag_svgrepo_com)
                            error(R.drawable.shopping_bag_svgrepo_com)
                            crossfade(true)
                        }
                    ),
                    contentDescription = product.name ?: "Product Image",
                    contentScale = ContentScale.Fit, // Changed from Inside to Fit for better scaling
                    modifier = Modifier.fillMaxSize() // Fill the available space in the Box
                )
            }

            // Category text at the bottom
            Text(
                text = formatCategoryName(product.category ?: "General"),
                color = Color.Black, // Changed to black for better visibility on white background
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodySmall, // Made smaller to fit better
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp) // Add padding for better spacing
            )
        }
    }
}

private fun formatCategoryName(category: String): String {
    return when (category.lowercase()) {
        "electronics" -> "Electronics"
        "jewelery" -> "Jewelery"
        "men's clothing" -> "Men's Clothing"
        "women's clothing" -> "Women's Clothing"
        else -> category.split(" ").joinToString(" ") {
            it.replaceFirstChar { char -> char.uppercaseChar() }
        }
    }
}