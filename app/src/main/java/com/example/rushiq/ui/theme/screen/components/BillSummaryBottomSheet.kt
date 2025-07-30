package com.example.rushiq.ui.theme.screen.components

import android.widget.Space
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.rushiq.R
import com.google.api.Billing
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.contracts.contract
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillSummaryBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    totalPrice: Double,
    itemCount: Int,
    tipAmount: Double,
    onApplyFreeDelivery: () -> Unit,
    isFreeDeliveryApplied: Boolean,
    isApplyingFreeDelivery: Boolean,
    finalTotal: Double,
    onPayClicked: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    val MIN_CART_VALUE_FOR_FREE_DELIVERY = 200.0
    val handlingCost = 14.99
    val gstOnHandling = 2.40
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val isLateNight = currentHour >= 23 || currentHour < 6
    val lateNightFee = if (isLateNight) 25.0 else 0.0
    val gstOnLateNight = if (isLateNight) 4.13 else 0.0
    var showInfoPopup by remember { mutableStateOf(value = false) }
    val deliveryFee = 30
    val itemTotalWithGST = totalPrice
    val discountedPrice = (itemTotalWithGST * 0.9).roundToInt()

    val isEligibleForFreeDelivery = remember(totalPrice) {
        derivedStateOf { totalPrice >= MIN_CART_VALUE_FOR_FREE_DELIVERY }
    }

    val rawItemTotal = totalPrice
    val itemCost = rawItemTotal
    val exactItemTotal = itemCost + handlingCost + gstOnHandling + (if (isLateNight) gstOnLateNight else 0.0)
    val originalItemPrice = (itemCost + 1.1).roundToInt()

    // Use the pre-calculated finalTotal instead of recalculating here
    val totalWithTip = finalTotal

    val savings = remember(itemCost, isFreeDeliveryApplied) {
        val discountSavings = originalItemPrice - itemCost
        val deliverySavings = if (isFreeDeliveryApplied) deliveryFee.toDouble() else 0.0
        discountSavings + deliverySavings
    }
    if (isVisible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            dragHandle = {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.invoice),
                        contentDescription = "Bill Info",
                        modifier = Modifier.size(28.dp),
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Bill Summary",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Item total section with info button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Items Total & GST ($itemCount items)",
                          style = MaterialTheme.typography.bodyLarge,
                            color = Color.DarkGray
                        )
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Price Info",
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .size(20.dp)
                                .clickable{showInfoPopup = true },
                                tint = Color(0xFF9E9E9E)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "₹${originalItemPrice}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough,
                            modifier = Modifier.alpha(0.7f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "₹${exactItemTotal.roundToInt()}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Delivery Fee",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray
                    )

                    if (isFreeDeliveryApplied) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${deliveryFee}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough,
                                modifier = Modifier.alpha(0.7f)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "FREE",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007148) // Green color
                        )
                    } else {
                        Text(
                            text = "₹${deliveryFee}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                if(isLateNight){
                    Spacer(modifier = Modifier.height(16.dp))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "₹25",
                           style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                if(!isFreeDeliveryApplied){
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = "Free delivery on this order",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isEligibleForFreeDelivery.value) Color(0xFF007148) else Color(0xFFE6E6FA)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                if (isEligibleForFreeDelivery.value) {
                                    onApplyFreeDelivery()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFEEEEEE),
                                disabledContainerColor = Color.LightGray
                            ),
                            enabled = isEligibleForFreeDelivery.value && !isApplyingFreeDelivery,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            if (isApplyingFreeDelivery) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "APPLY",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                if (tipAmount > 0) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Delivery Partner Tip",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.DarkGray
                        )

                        Text(
                            text = "₹${tipAmount}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(color = 0xFF00FF48) // Green color for tip
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color.LightGray)
                Spacer(modifier = Modifier.height(16.dp))

                // Total to pay
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "To Pay",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "incl. all taxes and charges",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        // Display final amount with all charges
                        Text(
                            text = "₹${totalWithTip.roundToInt()}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        // Show calculated savings
                        Text(
                            text = "SAVING ₹${savings.roundToInt()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(color = 0xFF00FF48),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                            onDismiss()
                            onPayClicked()// this will now call our updated launchPayment Activity function
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                        .height(56.dp),
                        colors= ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF3F6C)
                        ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "PROCEED TO PAY ₹${finalTotal.roundToInt()} ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))

            }
        }
        if (showInfoPopup) {
            Dialog(onDismissRequest = { showInfoPopup = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth() // increased width
                        .padding(16.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                    ) {
                        // Header
                        Text(
                            text = "Zepto has no role to play in the taxes and charges being levied by the government",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Dynamic item cost from user's cart
                        PriceRow(
                            label = "Item Cost",
                            amount = itemCost
                        )

                        // Fixed handling fees
                        PriceRow(
                            label = "Item Handling Cost",
                            amount = handlingCost
                        )

                        PriceRow(
                            label = "GST on Item Handling Cost",
                            amount = gstOnHandling
                        )
                        //late night handling charge GST (if applicable)
                        if(isLateNight){
                            PriceRow(
                                label = "GST on late night handling charge",
                                amount = gstOnLateNight
                            )
                        }
                        Divider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = Color.LightGray

                        )
                        PriceRow(
                            label = "Item total & GST",
                            amount = exactItemTotal,
                            isBold = true ,
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun PriceRow(
    label: String,
    amount: Double,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Color.DarkGray
        )

        Text(
            text = "₹${String.format("%.2f", amount)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Color.DarkGray
        )
    }
}
