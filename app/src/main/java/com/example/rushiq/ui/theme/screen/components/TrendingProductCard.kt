package com.example.rushiq.ui.theme.screen.components
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.example.rushiq.data.models.fakeapi.Products
import com.example.zepto.R
import okhttp3.internal.format

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

    // Wrap card content vertically
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Card representing the product
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    product.category?.let { category ->
                        onCategoryClick(category)
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            // This is where you'd start the discount banner with glass effect
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
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }

            //  Product Image with placeholder + crossfade
            Box(
                modifier = Modifier
                    .background(Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.White)

                ) {
                 Image(
                     painter = rememberImagePainter(
                         data= product.imageUrl?:"",
                         builder = {
                             placeholder(R.drawable.shopping_bag_svgrepo_com)
                             error(R.drawable.shopping_bag_svgrepo_com)
                             crossfade(true)

                         }
                     ),
                     contentDescription = product.name?:"Product Image",
                     contentScale = ContentScale.Inside,
                     modifier = Modifier
                         .fillMaxSize()
                         .padding(4.dp),
                     alignment = Alignment.BottomCenter
                  )
                }

            }
            Text(
                text = formatCategoryName(product.category?:"General"),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
               maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 4.dp)


            )
        }
    }


}
private fun formatCategoryName(category : String):String{
    return when(category.lowercase()){
        "electronics" -> "Electronics"
        "jewelery"        -> "Jewelery"
        "men's clothing"  -> "Men's clothing"
        "women's clothing"-> "Women's clothing"
        else              -> category.split("").joinToString (" "){it.capitalize()}
    }
}

