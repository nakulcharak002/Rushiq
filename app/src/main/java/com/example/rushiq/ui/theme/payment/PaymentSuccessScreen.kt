//package com.example.rushiq.ui.theme.payment
//
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.example.rushiq.data.models.mealDB.PaymentRecord
//import com.example.rushiq.ui.theme.viewmodels.PaymentViewModel
//import kotlinx.coroutines.delay
//
//@Composable
//fun PaymentSuccessScreen(
//    paymentId: String,
//    paymentViewModel: PaymentViewModel = hiltViewModel(),
//    onHome: () -> Unit
//) {
//    val accentGreen = Color(color = 0xFF007148)  // Green for success icons
//    val buttonColor = Color(color = 0xFFFF3F63)  // Pink color for buttons
//
//    var paymentBy by remember { mutableStateOf<PaymentRecord?>(value = null) }
//    var isLoading by remember { mutableStateOf(value = true) }
//    var errorMessage by remember { mutableStateOf(value = "") }
//
//    // Automatically navigate back after 5 seconds
//    LaunchedEffect(Unit) {
//        delay(timeMillis = 5000)
//        onHome()
//    }
//
//    // Load payment details from Firestore
//    LaunchedEffect(paymentId) {
//        paymentViewModel.getPaymentDetails(
//            paymentId = paymentId,
//            onSuccess = { paymentRecord ->
//                payment = paymentRecord
//                isLoading = false
//            },
//            onError = { error ->
//                errorMessage = error
//                isLoading = false
//            }
//        )
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//            .padding(24.dp)
//    )
//}