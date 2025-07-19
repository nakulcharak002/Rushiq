package com.example.rushiq.ui.theme.payment

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rushiq.data.models.mealDB.PaymentRecord
import com.example.rushiq.ui.theme.viewmodels.PaymentDetailState
import com.example.rushiq.ui.theme.viewmodels.PaymentDetailViewModel
import com.example.rushiq.R
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Locale

// Data classes for parsing item details
data class ItemDetails(val name: String, val priceQuantity: String) {
    data class PriceQuantityInfo(
        val price: String,
        val quantity: String,
        val subtotal: String
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDetailsScreen(
    paymentId: String,
    navController: NavController,
    viewModel: PaymentDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val zeptoPurple = Color(color = 0xFF8B5CF6)

    LaunchedEffect(paymentId) {
        viewModel.loadPaymentDetail(paymentId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Payment Details", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = zeptoPurple,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(color = 0xFFF8F8F8)) // Light gray background
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is PaymentDetailState.Loading -> {
                    LoadingState()
                }
                is PaymentDetailState.Success -> {
                    ProfessionalPaymentDetailContent(payment = state.payment)
                }
                is PaymentDetailState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { viewModel.loadPaymentDetail(paymentId) }
                    )
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = Color(color = 0xFF8B5CF6),
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Loading payment details...",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Clear,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = Color.Red.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Couldn't load payment details",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF5D0079)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Try Again",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun ProfessionalPaymentDetailContent(payment: PaymentRecord) {
    // Debug logging for image URLs
    LaunchedEffect(payment) {
        Log.d("PaymentDetailsScreen", "Payment has ${payment.items.size} items")
        Log.d(
            "PaymentDetailsScreen",
            "Payment itemTagsUrls is ${if (payment.itemImageUrls?.isNotEmpty() == true) payment.itemImageUrls else "null"} else ${payment.itemImageUrls?.size ?: "entries"}"
        )

        // Log all image URLs
        payment.itemImageUrls?.forEach { (key, url) ->
            Log.d("PaymentDetailsScreen", "Image URL for '$key': $url")
        }

        // Check if the URLs are actually valid URLs
        payment.itemImageUrls?.values?.forEach { url ->
            try {
                val uri = URI(url)
                val isValidUrl = uri.scheme?.startsWith("http") == true
                Log.d(
                    "PaymentDetailsScreen",
                    "URL validation for $url: ${if (isValidUrl) "VALID" else "INVALID"}"
                )
            } catch (e: Exception) {
                Log.e("PaymentDetailsScreen", "Invalid URL: $url", e)
            }
        }

        // Log the items to check matching
        payment.items.forEach { item ->
            val itemName = parseItemDetails(item).name
            Log.d(
                "PaymentDetailsScreen",
                "Item: '$itemName', trying to find a matching image URL key"
            )
            val matchingKey = payment.itemImageUrls?.keys?.firstOrNull { key ->
                key.contains(itemName, ignoreCase = true) || itemName.contains(
                    key,
                    ignoreCase = true
                )
            }
            Log.d("PaymentDetailsScreen", " - Matching key found: ${matchingKey ?: "NONE"}")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        PaymentStatusCard(payment)
        OrderDetailCard(payment)
        ItemsListCard(payment)
    }
}

@Composable
fun PaymentStatusCard(payment: PaymentRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(color = 0xFFE8F5E9))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = Color(color = 0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = payment.status,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = Color(color = 0xFF4CAF50)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            // The color below `Color(0xFFE5E7EB)` seems like a very light grey, almost white.
            // If you intend for the amount to be prominently visible, consider a darker color.
            // Keeping it as is based on the original code.
            Text(
                text = "₹${payment.amount.toInt()}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFE5E7EB)
            )

            Text(
                text = "Total Amount Paid",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Time
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.time_svgrepo_com),
                    contentDescription = null,
                    // The color below `Color(0xFFE5E7EB)` seems like a very light grey, almost white.
                    // If you intend for the icon to be prominently visible, consider a darker color.
                    // Keeping it as is based on the original code.
                    tint = Color(0xFFE5E7EB),
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                Text(
                    text = dateFormat.format(payment.timestamp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun OrderDetailCard(payment: PaymentRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Order Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2F2F2F)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Order ID
            DetailRow(
                icon = Icons.Default.Done,
                label = "Order ID",
                value = payment.orderId
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Payment ID
            DetailRow(
                icon = Icons.Default.Menu,
                label = "Payment ID",
                value = payment.id
            )

            // Corrected conditional checks for email and phone
            if (payment.userEmail.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                DetailRow(
                    icon = Icons.Default.Email,
                    label = "Email",
                    value = payment.userEmail
                )
            }
            if (payment.userPhone.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                DetailRow(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    // This was `payment.userEmail` in the original code, changed to `payment.userPhone`
                    value = payment.userPhone
                )
            }
        }
    }
}

@Composable
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFE3F2FD)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,

                tint = Color(0xFF5D0079),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Label and value
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF212121)
            )
        }
    }
}

@Composable
fun ItemsListCard(payment: PaymentRecord) {
    var expanded by remember { mutableStateOf(true) }
    LaunchedEffect(payment) {
        Log.d("itemsListCard", "Item Image Urls : ${payment.itemImageUrls?.keys?.joinToString()}")
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(spring()),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Items (${payment.items.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.Gray
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(24.dp))
                if (payment.items.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No items in the order",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        payment.items.forEachIndexed { index, item ->
                            if (index > 0) {
                                Divider(color = Color(0xFFEEEEEE))
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            val imageUrl = getImageUrlForItem(payment, item)
                            Log.d("itemsListCard ", "Item :$item , Found Url :$imageUrl")
                            EnhancedItemRow(
                                item = item,
                                imageUrl = imageUrl
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getImageUrlForItem(payment: PaymentRecord, item: String): String? {
    val itemName = parseItemDetails(item).name
    // Try to find a direct match or a key that contains the item name
    return payment.itemImageUrls?.entries?.firstOrNull { (key, _) ->
        key.contains(itemName, ignoreCase = true) || itemName.contains(key, ignoreCase = true)
    }?.value
}


@Composable
fun EnhancedItemRow(
    item: String,
    imageUrl: String?
) {
    val itemsDetails = parseItemDetails(item)
    val priceInfo = parsePriceAndQuantity(itemsDetails.priceQuantity)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(4.dp)
        ) {
            ItemImage(
                imageUrl = imageUrl,
                item = itemsDetails.name, // Pass the parsed name for category color/icon
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        //item details
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = itemsDetails.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF212121),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = priceInfo.price,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF212121),
                )
                Text(
                    text = "*",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFEFEBF3))
                        .padding(horizontal = 8.dp, vertical = 2.dp)

                ) {
                    Text(
                        text = priceInfo.quantity,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF5D0079)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = priceInfo.subtotal,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D0079)
                )
            }
        }
    }
}

@Composable
fun ItemImage(
    imageUrl: String?,
    item: String,
    modifier: Modifier = Modifier
) {
    Log.d("ItemImage", "Rendering for item: $item, URL: $imageUrl")

    if (imageUrl.isNullOrEmpty()) {
        // Fallback for missing image URL
        Box(
            modifier = modifier.background(getCategoryColor(item)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = getCategoryIcon(item),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    } else {
        // Load image with proper error handling
        val context = LocalContext.current

        Box(modifier = modifier) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Item image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                onError = {
                    Log.e("ItemImage", "Error loading image: $imageUrl", it.result.throwable)
                },

            )
        }
    }
}


private fun parseItemDetails(itemString: String): ItemDetails {
    val name = itemString.substringBefore("(").trim()
    val priceQuantityPart = if (itemString.contains("(")) {
        itemString.substringAfter("(").substringBefore(")")
    } else {
        // Handle cases where there are no parentheses, e.g., just the item name
        ""
    }
    return ItemDetails(name, priceQuantityPart)
}

private fun parsePriceAndQuantity(priceQuantityText: String): ItemDetails.PriceQuantityInfo {
    var price = "₹0"
    var quantity = "0"
    var subtotal = "₹0" // Initialize with currency symbol for consistency
    try {
        val parts = priceQuantityText.split("*", "=")
        if (parts.size >= 3) {
            price = parts[0].trim()
            quantity = parts[1].trim()
            subtotal = parts[2].trim()
        }
    } catch (e: Exception) {
        Log.e("PaymentDetailScreen", "Error parsing price quantity: $priceQuantityText", e)
    }
    return ItemDetails.PriceQuantityInfo(price, quantity, subtotal)
}

private fun getCategoryColor(item: String): Color {
    return when {
        item.contains("rice", ignoreCase = true) ||
                item.contains("dal", ignoreCase = true) ||
                item.contains("flour", ignoreCase = true) -> Color(0xFFFFCDD2) // Light Red (was 0xFFFF7373, too strong)

        item.contains("fruit", ignoreCase = true) ||
                item.contains("apple", ignoreCase = true) ||
                item.contains("banana", ignoreCase = true) -> Color(0xFFFFECB3) // Light Orange (was 0xFFFFB74D, too strong)

        item.contains("milk", ignoreCase = true) ||
                item.contains("cheese", ignoreCase = true) -> Color(0xFFBBDEFB) // Light Blue (was 0xFF4FC3F7, too strong)

        item.contains("bread", ignoreCase = true) ||
                item.contains("biscuit", ignoreCase = true) -> Color(0xFFE1BEE7) // Light Purple (was 0xFFBA68CB, too strong)

        item.contains("vegetable", ignoreCase = true) ||
                item.contains("tomato", ignoreCase = true) ||
                item.contains("potato", ignoreCase = true) -> Color(0xFFC8E6C9) // Light Green (was 0xFF81C784, too strong)

        else -> Color(0xFFE0E0E0) // Light Gray default (was 0xFF9E9E9E, a bit dark)
    }
}

private fun getCategoryIcon(item: String): ImageVector {
    // You can expand this to have more specific icons for different categories.
    // For now, using ShoppingCart as a general fallback or for items not specifically matched.
    return when {
        item.contains("fruit", ignoreCase = true) || item.contains("vegetable", ignoreCase = true) -> Icons.Filled.ShoppingCart // Example, could be a fruit/veg icon
        item.contains("milk", ignoreCase = true) || item.contains("cheese", ignoreCase = true) -> Icons.Filled.ShoppingCart // Example, could be a dairy icon
        else -> Icons.Filled.ShoppingCart
    }
}