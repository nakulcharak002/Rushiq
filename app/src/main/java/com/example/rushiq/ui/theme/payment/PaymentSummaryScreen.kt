package com.example.rushiq.ui.theme.payment

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.rushiq.ui.theme.viewmodels.PaymentViewModel

@Composable
fun PaymentSummaryScreen (
    paymentViewModel: PaymentViewModel,
    cartViewModel: CartViewModel = hiltViewModel(),
    onPaymentInit : (Double)-> Unit,
    onNavigateBack : ()->Unit
){

}