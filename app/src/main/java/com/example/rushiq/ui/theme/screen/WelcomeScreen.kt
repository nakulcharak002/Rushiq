package com.example.rushiq.ui.theme.screen

import android.R.attr.onClick

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rushiq.data.models.fakeapi.Category
import com.example.rushiq.ui.theme.screen.components.LocationBar
import com.example.rushiq.ui.theme.screen.components.PromoCardCarousel


@Composable
fun WelcomeScreen(
    onCategorySelected: (CategoryType) -> Unit,
    navController: NavController,
) {
    val scrollState = rememberScrollState()
    val deliveryTime = "6 Mins"

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(color = 0xFFF5F5F5)),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
        ) {

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(color = 0xFF333535),
                            shape = RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp),
                        ).padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // Your LocationBar component
                    LocationBar(
                        modifier = Modifier.wrapContentWidth(),
                        contentColor = Color.Black,
                    )
                    PromoCardCarousel()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)

            ) {
                EveryDayEssentialCard(
                    onClick = { onCategorySelected(CategoryType.EVERYDAY) },
                )
            }
            // cafe card
            CafeCard(
                onClick = { onCategorySelected(CategoryType.CAFE) },
            ) {


            }

        }
    }
}
