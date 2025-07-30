package com.example.rushiq.ui.theme.screen.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.rushiq.R

@Composable
fun DeliveryPartnerTipSection(
    modifier: Modifier = Modifier,
    selectedTip: Int,
    onTipSelected: (Int) -> Unit,
    accentGreen: Color = Color(0xFF0D7148),
    cartViewModel: CartViewModel? = null
) {
    val tipOptions = listOf(20, 30, 50, 0)
    val lightGreen = Color(0xFFECFDF3)

    // Sync the viewModel's tip with the local state if available
    LaunchedEffect(selectedTip) {
        cartViewModel?.setTipAmount(if (selectedTip > 0) selectedTip else 0)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(R.drawable.money_round_1180_svgrepo_com),
                    contentDescription = "Tip",
                    modifier = Modifier.size(24.dp),
                    tint = accentGreen
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Delivery Partner Tip",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "This amount goes to your delivery partner",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }
        // tip options
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tipOptions.forEach { tipAmount ->
                val isSelected = selectedTip == tipAmount

                OutlinedCard(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            onTipSelected(if (isSelected) 0 else tipAmount)
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (isSelected) lightGreen else Color.White
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) accentGreen else Color.LightGray
                    )
                ) {
                    Text(
                        text = if (tipAmount == 0) "No Tip" else "₹$tipAmount",
                        color = if (isSelected) accentGreen else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
        if (selectedTip > 0) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "thank you for tipping ₹$selectedTip to your delivery partner!",
                style = MaterialTheme.typography.bodyMedium,
                color = accentGreen
            )
        }
    }
}