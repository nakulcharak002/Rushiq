package com.example.rushiq.ui.theme.screen

import android.app.Activity
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rushiq.data.models.fakeapi.CartItem
import com.example.rushiq.ui.theme.screen.components.DeliveryPartnerTipSection
import com.example.rushiq.ui.theme.screen.components.EnhancedCartItemRow
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.rushiq.ui.theme.viewmodels.LocationViewModel
import kotlin.math.roundToInt

@Composable
fun CartScreen(
    paddingValues: PaddingValues,
    onNavigateBack: () -> Unit,
    cartViewModel: CartViewModel = hiltViewModel(),
    viewModel: LocationViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val selectedTip by remember { mutableStateOf(-1) }
    val address by viewModel.userAddress.collectAsState()
    val deliveryTime by viewModel.deliveryTime.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val cartItems by cartViewModel.cartItems.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val buttonColor = Color(0xFFFF5F6D)  // Pink color for buttons
    val savedColor = Color(0xFF8BC34A)   // Light green for saved amount
    val accentGreen = Color(0xFF4CAF50)  // Green for success icons
    val scrollState = rememberScrollState()
    val context = LocalContext.current
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
            // Sticky header - cart summary with saving
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
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
                        // Saved badge
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
            if(cartItems.isEmpty()){
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .height(300.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center

                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ){
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
                            colors = ButtonDefaults.buttonColors(
                                containerColor = buttonColor
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text =  "Continue shopping ",
                                style = MaterialTheme.typography.bodyMedium,
                               fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal =  16.dp, vertical = 8.dp )


                            )
                        }
                    }

                }
            }else{
                Card(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp , vertical = 8.dp),
                    colors =CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation =1.dp)


                ) {
                    Column(
modifier = Modifier.fillMaxWidth()
    .padding(16.dp)
                    ) {
                        cartItems.forEach {cartItem ->
                            EnhancedCartItemRow(
                                cartItem = cartItem,
                                cartViewModel = cartViewModel

                            )
                            if ((cartItem != cartItems.last())){
                                Divider(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    color = Color(0xFFEEEEEE)
                                )
                            }

                        }
                        Divider(
                            modifier =  Modifier.padding(vertical = 16.dp),
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
                                color = Color( 0xFFD4AF37) // Gold color
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
                                color = Color( 0xFFD4AF37), // gold color
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFF8F0D8),
                                        shape = RoundedCornerShape(4.dp)

                                    )
                                    .padding(horizontal = 8.dp , vertical = 2.dp)
                            )
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color(0xFFEEEEEE)
                        )
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Free delivery",
                                tint = Color(0xFFD4AF37)

                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Free delivery",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                            Text(
                                text = "Free delivery",
                                style = MaterialTheme.typography.bodyLarge,
                            )
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
                                .background(Color(color = 0xFFECFDF3), CircleShape),
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
                // oder for someone else
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


            }
        }
    }
}
