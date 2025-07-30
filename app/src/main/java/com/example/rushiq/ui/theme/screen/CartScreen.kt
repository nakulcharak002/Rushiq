package com.example.rushiq.ui.theme.screen


import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.example.rushiq.ui.theme.payment.PaymentActivity
import com.example.rushiq.ui.theme.screen.components.BillSummaryBottomSheet
import com.example.rushiq.ui.theme.screen.components.DeliveryPartnerTipSection
import com.example.rushiq.ui.theme.screen.components.EnhancedCartItemRow
import com.example.rushiq.ui.theme.viewmodels.AuthViewModel
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.rushiq.ui.theme.viewmodels.LocationViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun CartScreen(
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    viewModel: LocationViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    var selectedTip by remember { mutableStateOf(-1) }
    val address by viewModel.userAddress.collectAsState()
    val deliveryTime by viewModel.deliveryTime.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val buttonColor = Color(0xFFFF5F6D)
    val savedColor = Color(0xFF8BC34A)
    val accentGreen = Color(0xFF4CAF50)
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val tipAmount by cartViewModel.tipAmount.collectAsState()
    val totalWithTip by cartViewModel.totalWithTip.collectAsState()
    val totalItems by cartViewModel.totalItems.collectAsState()
    val isBottomSheetVisible by cartViewModel.isBottomSheetVisible.collectAsState()
    val isFreeDeliveryApplied by cartViewModel.isFreeDeliveryApplied.collectAsState()
    val isApplyingFreeDelivery by cartViewModel.isApplyingFreeDelivery.collectAsState()
    val finalTotal by cartViewModel.finalTotal.collectAsState()
    val bottomSheetHeight = 50.dp + 40.dp + 72.dp
    val currentUser = authViewModel.getCurrentUser()
    val phone = authViewModel.phoneNumber.collectAsState().value ?: ""

    val paymentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val isPaymentSuccessful =
                result.data?.getBooleanExtra("PAYMENT_SUCCESSFUL", false) ?: false

            if (isPaymentSuccessful) {
                cartViewModel.clearCart()
                Toast.makeText(
                    context,
                    "Payment successful. Your order has been placed.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    context,
                    "Payment was not completed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun launchPaymentActivity() {
        val orderId = cartViewModel.generateOrderId()
        val email = currentUser?.email ?: ""

        (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch {
            try {
                val (itemDetails, imageUrls) = cartViewModel.getCartItemsWithImageUrls()
                Log.d("CartScreen", "Launching payment with ${itemDetails.size} items and ${imageUrls.size} image URLs")
                imageUrls.forEach { (id, url) ->
                    Log.d("CartScreen", "Item $id has image URL: $url")
                }
                val intent = Intent(context, PaymentActivity::class.java).apply {
                    putExtra("TOTAL_AMOUNT", finalTotal)
                    putExtra("ORDER_ID", orderId)
                    putExtra("USER_EMAIL", email)
                    putExtra("USER_PHONE", phone)
                    putExtra("TIP_AMOUNT", tipAmount)
                    putExtra("DELIVERY_ADDRESS", address)
                    putStringArrayListExtra("CART_ITEMS_DATA", ArrayList(itemDetails))
                    putExtra("CART_ITEMS_IMAGES", HashMap<String, String>(imageUrls))
                }
                paymentLauncher.launch(intent)

            } catch (e: Exception) {
                Log.d("CartScreen", "Error preparing payment intent", e)
                Toast.makeText(context, "Error preparing payment", Toast.LENGTH_SHORT).show()
            }
        }?:run {
            Log.w("CartScreen" ,"LifecycleOwner not available . using non-Coroutine approach")
            val intent = Intent(context, PaymentActivity::class.java).apply {
                putExtra("TOTAL_AMOUNT", finalTotal)
                putExtra("ORDER_ID", orderId)
                putExtra("USER_EMAIL", email)
                putExtra("USER_PHONE", phone)
                putExtra("TIP_AMOUNT", tipAmount)
                putExtra("DELIVERY_ADDRESS", address)

                val simpleItemDetails = cartItems.map{
                    cartItem ->
                    "${cartItem.products.name} (${cartItem.products.price} x ${cartItem.quantity}= ${cartItem.products.price * cartItem.quantity})"
                }
                putStringArrayListExtra("CART_ITEMS_DATA", ArrayList(simpleItemDetails))
            }
            paymentLauncher.launch(intent)
        }
    }

    BillSummaryBottomSheet(
        isVisible = isBottomSheetVisible,
        onDismiss= {cartViewModel.hideBottomSheet()},
    totalPrice = totalPrice,
    itemCount = totalItems,
    tipAmount = tipAmount,
    onApplyFreeDelivery = {cartViewModel.applyFreeDelivery()},
    isFreeDeliveryApplied =  isFreeDeliveryApplied,
    isApplyingFreeDelivery = isApplyingFreeDelivery,
    finalTotal = finalTotal,
    onPayClicked = {launchPaymentActivity()}
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 32.dp)
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = bottomSheetHeight)
                .verticalScroll(scrollState)
        ) {
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(onClick = onNavigateBack)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                        Text(
                            text = "Your Cart",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(savedColor)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "SAVED ₹${(totalPrice * 0.25).roundToInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.DarkGray
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(accentGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Saved",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Saved",
                            style = MaterialTheme.typography.bodyLarge,
                            color = accentGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " ₹${(totalPrice * 0.25).roundToInt()}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = accentGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = " including ₹30 through free delivery ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.DarkGray,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFB8EBCD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Delivery Time",
                            tint = accentGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Delivery in 7 mins",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Empty Cart",
                            modifier = Modifier.size(100.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your Cart is empty",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Add items to your Cart to continue shopping ",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Continue shopping ",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        cartItems.forEach { cartItem ->
                            EnhancedCartItemRow(
                                cartItem = cartItem,
                                cartViewModel = cartViewModel
                            )
                            if ((cartItem != cartItems.last())) {
                                Divider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = Color(0xFFEEEEEE)
                                )
                            }
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color(0xFFEEEEEE)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Missed something?",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            Button(
                                onClick = onNavigateBack,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add more items",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Add more Items",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }
                }
            }
            // Additional options that appear when scrolling
            if (cartItems.isNotEmpty()) {
                // Savings info card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "₹30",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD4AF37) // Gold color
                            )
                            Text(
                                text = " saved with ",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Black
                            )
                            Text(
                                text = "pass",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                ),
                                color = Color(0xFFD4AF37), // gold color
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFF8F0D8),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color(0xFFEEEEEE)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Free delivery",
                                tint = Color(0xFFD4AF37)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Free delivery",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD4AF37)
                            )
                        }
                    }
                }
                // Coupons card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color(0xFFECFDF3), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Coupons",
                                tint = accentGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row {
                                Text(
                                    text = "You have unlocked ",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                                Text(
                                    text = "15 new coupons",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF9C27B0)
                                )
                            }
                            Text(
                                text = "Explore Now",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Go to coupon",
                            tint = Color.Gray,
                        )
                    }
                }
                // order for someone else
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ordering for someone else?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        OutlinedButton(
                            onClick = { /* Add Details */ },
                            border = BorderStroke(1.dp, buttonColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Add Details",
                                color = buttonColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                // delivery partner tip
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        DeliveryPartnerTipSection(
                            selectedTip = selectedTip,
                            onTipSelected = { newTip ->
                                selectedTip = newTip
                            },
                            accentGreen = accentGreen
                        )
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Instructions",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Delivery Instructions",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Delivery partner will be notified",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Add instructions",
                            tint = Color.Gray
                        )
                    }
                }
            }
            // to pay section
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { cartViewModel.showBottomSheet() },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "To Pay",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "To Pay",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Inc. all taxes and charges",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                        // right side - price display
                        Column(horizontalAlignment = Alignment.End) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val originalPrice = (finalTotal * 1.1).roundToInt()
                                Text(
                                    text = "₹$originalPrice",
                                    style = MaterialTheme.typography.bodyMedium,
                                    textDecoration = TextDecoration.LineThrough,
                                    color = Color.Gray,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "₹${finalTotal.roundToInt()}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // additional info remain the same
                            if (tipAmount > 0) {
                                Text(
                                    text = "INCLUDING ₹$tipAmount TIP",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = accentGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (isFreeDeliveryApplied) {
                                Text(
                                    text = "FREE DELIVERY APPLIED",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = accentGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            val deliverySavings = if (isFreeDeliveryApplied) 30 else 0
                            val totalSavings = (totalPrice * 0.25 + deliverySavings).roundToInt()
                            Text(
                                text = "SAVING ₹$totalSavings ",
                                style = MaterialTheme.typography.bodySmall,
                                color = accentGreen,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "View bills details",
                            tint = Color.Gray,
                        )
                    }
                }
            }
            //delivery partner safety
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Safety",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Delivery Partner's Safety",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Learn more about how we ensure their safety",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Learn more",
                            tint = Color.Gray,
                        )
                    }
                }
            }

            // Delivery Location Section
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Delivery Location",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Delivery Location",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = address ?: "Add delivery address",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = "Change location",
                            tint = Color.Gray,
                        )
                    }
                }
            }

            // Pay Button Section
            if (cartItems.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Button(
                            onClick = {
                                cartViewModel.showBottomSheet()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Pay ₹${finalTotal.roundToInt()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        // Bottom fixed section
        if (cartItems.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                ) {//delivery location
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0E0E0)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home",
                                    tint = Color.Gray
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Delivery to Home",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Change Location",
                                        tint = Color.Black
                                    )

                                }
                                Text(
                                    text = "address",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.Gray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFFADEFE))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Distance",
                                        tint = buttonColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "3.3 kms Away ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = buttonColor,
                                        fontWeight = FontWeight.Medium
                                    )

                                }
                            }
                        }

                    }
                    // correct eco box with proper structure padding
                    Box(
                        modifier = Modifier.fillMaxWidth()
                            .background(Color.White)
                            .padding(horizontal = 16.dp)

                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White)
                                .clickable {
                                    /*toggle eco option */
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(24.dp)
                                    .background(accentGreen, CircleShape),
                                contentAlignment = Alignment.Center

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)

                                )

                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "We have opted you in for no bag delivery",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.ThumbUp,
                                contentDescription = "Eco friendly",
                                tint = accentGreen
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowRight,
                                contentDescription = "Change option ",
                                tint = Color.Gray
                            )
                        }

                    }
                    Box(
                        modifier = Modifier.background(Color.White)
                    )
                    {
                        //completing the pay button code
                        Button(
                            onClick = {
                                if (totalPrice < 200.0) {
                                    Toast.makeText(
                                        context,
                                        "Add items worth ₹${(200 - totalPrice).roundToInt()} more for free delivery",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    cartViewModel.showBottomSheet()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                                .padding(16.dp)
                                .height(56.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor
                            ),
                            shape = RoundedCornerShape(0.dp),
                            contentPadding =   PaddingValues(0.dp)
                        ) {
                            Text(
                                text = if (tipAmount > 0) {
                                    "Click to pay ₹${finalTotal} (incl ₹$tipAmount tip)"
                                } else {
                                    "Click to pay ₹${finalTotal.roundToInt()}"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White

                            )
                        }
                    }
                }
            }
        }
    }
}