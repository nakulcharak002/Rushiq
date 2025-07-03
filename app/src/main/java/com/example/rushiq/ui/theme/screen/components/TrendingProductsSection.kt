package com.example.rushiq.ui.theme.screen.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.ui.theme.viewmodels.HomeViewModel
import com.example.zepto.R
import getCategoryGradient

private const val TAG = "TrendingProductSection"

@Composable
fun TrendingProductsSection(
    products: List<Products>,
    viewModel: HomeViewModel = viewModel(),
    onNavigateToCategory: (String) -> Unit
) {
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    LaunchedEffect(products) {
        Log.d(TAG, "TrendingProductsSection received ${products.size} products")
        products.forEachIndexed { index, product ->
            Log.d(
                TAG,
                "Product $index: id = ${product.id}, name = ${product.name}, " +
                        "category = ${product.category}, imageUrl = ${product.imageUrl}, imageRes = ${product.imageRes}"
            )
        }
    }

    LaunchedEffect(Unit) {
        Log.d(TAG, "Calling fixProductsImageUrl from TrendingProductsSection")
        viewModel.fixProductsImageUrl()
    }

    val categoryBackground = selectedCategory?.let {
        getCategoryGradient(it)
    } ?: Brush.horizontalGradient(listOf(Color(0xFF6200EE), Color(0xff3700B3)))

    val displayProducts = remember(products) {
        val categories = listOf("electronics", "jewelery", "men's clothing", "women's clothing")
        val result = mutableListOf<Products>()

        if (products.isEmpty()) {
            Log.d(TAG, "Creating dummy products because product list is empty")
            repeat(6) { index ->
                val categoryIndex = index % categories.size
                val imageRes = when (categoryIndex) {
                    0 -> R.drawable.headphones_round_svgrepo_com
                    1 -> R.drawable.wedding_dress_svgrepo_com
                    2 -> R.drawable.fashion
                    3 -> R.drawable.beauty
                    else -> R.drawable.shopping_bag_svgrepo_com
                }

                Log.d(TAG, "Created dummy product $index with imageRes=$imageRes")

                result.add(
                    Products(
                        id = index,
                        name = "Product $index",
                        price = 299.0,
                        category = categories[categoryIndex],
                        imageUrl = "",
                        imageRes = imageRes
                    )
                )
            }
        } else if (products.size < 6) {
            Log.d(TAG, "Not enough products (${products.size}), filling to 6")
            repeat(6) { index ->
                val product = products[index % products.size]
                val category = categories[index % categories.size]
                val modifiedProduct = product.copy(category = category)
                result.add(modifiedProduct)
                Log.d(TAG, "Added product ${modifiedProduct.id} with category $category")
            }
        } else {
            val groupedByCategory = products.groupBy { it.category?.lowercase() ?: "other" }

            categories.forEach { category ->
                groupedByCategory[category]?.firstOrNull()?.let {
                    result.add(it)
                    Log.d(TAG, "Added product ${it.id} from category $category")
                }
            }

            if (result.size < 6) {
                val remaining = products.filter { it !in result }
                val additionalProducts = remaining.take(6 - result.size)
                result.addAll(additionalProducts)
                Log.d(TAG, "Added ${additionalProducts.size} additional products to reach desired count")
            }

            if (result.size < 6) {
                repeat(6 - result.size) { index ->
                    val product = result[index % result.size]
                    result.add(product)
                    Log.d(TAG, "Repeated product ${product.id} to reach desired count")
                }
            }
        }

        val finalProducts = result.take(6)
        Log.d(TAG, "Final products for display: ${finalProducts.map { it.id }}")
        finalProducts
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = categoryBackground,
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
    ) {
        // Lottie Big Sale Animation Banner
        LottieSaleAnimation()

        Spacer(modifier = Modifier.height(8.dp))

        //  Trending Products Row (first 3)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            displayProducts.take(3).forEach { product ->
                Box(modifier = Modifier.weight(1f)) {
                    TrendingProductCard(product = product, onCategoryClick = onNavigateToCategory)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        //  Next Row of Products (last 3)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp , start = 8.dp , end = 8.dp) ,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            displayProducts.drop(3).take(3).forEach { product ->
                Box(modifier = Modifier.weight(1f)) {
                    TrendingProductCard(product = product, onCategoryClick = onNavigateToCategory)
                }
            }
        }
    }
}
