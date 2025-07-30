package com.example.rushiq.ui.theme.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.rushiq.ui.theme.viewmodels.LocationViewModel
import com.example.rushiq.R

@Composable
fun LocationBar(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    contentColor: Color = Color.Black,
    onLocationClick: () -> Unit = {}
) {
    LocationPermissionHandler {
        LocationWithLiveAddress(
            modifier = modifier,
            backgroundColor = backgroundColor,
            contentColor = contentColor,
            onLocationClick = onLocationClick
        )
    }
}

@Composable
fun LocationWithLiveAddress(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    viewModel: LocationViewModel = hiltViewModel(),
    contentColor: Color = Color.White,
    onLocationClick: () -> Unit = {}
) {
    val address by viewModel.userAddress.collectAsState()
    val deliveryTime by viewModel.deliveryTime.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateUserLocation()
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ),

        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onLocationClick,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.user_03_stroke_rounded),
                contentDescription = "User",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Delivery in ",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White
                )

                Text(
                    text = deliveryTime ?: "6 Mins", // dynamic delivery time
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.headlineLarge.copy(
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 4.dp)

            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = Color.White.copy(alpha = 0.7f),
                        strokeWidth = 2.dp,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Loading address...",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                } else {
                    Text(
                        text = address.ifEmpty { "Home - indirapuram , Uttarpradesh" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 2,
                    )
                    if (isLoading && address.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(8.dp),
                            color = Color.White.copy(alpha = 0.5f),
                            strokeWidth = 1.dp,

                            )
                    }
                }
            }
        }
    }
}
