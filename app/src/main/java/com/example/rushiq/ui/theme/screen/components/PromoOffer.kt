package com.example.rushiq.ui.theme.screen.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rushiq.R
import com.example.rushiq.data.models.PromoOffer
import kotlinx.coroutines.delay
import java.nio.file.WatchEvent

@Composable
fun PromoCardCarousel() {
    // List of promotional offers
    val promoOffers = listOf(
        PromoOffer("Health & Wellness", "UP TO 65% Off", "Ends Soon", R.drawable.electronic),
        PromoOffer("Health & Wellness", "UP TO 65% Off", "Ends Soon", R.drawable.veggie),
        PromoOffer("Health & Wellness", "UP TO 65% Off", "Ends Soon", R.drawable.condor)
    )

    // State to track current visible card
    var currentIndex by remember { mutableIntStateOf(value = 0) }

    // Heartbeat animation scale for "Ends Soon" text
    val heartbeatScale = remember { Animatable(initialValue = 1f) }

    // Heartbeat animation effect
    LaunchedEffect(Unit) {
        while (true) {
            // Start animation after a delay
            delay(timeMillis = 500)
            // Quick scale up
            heartbeatScale.animateTo(
                targetValue = 1.15f,
                animationSpec = tween(durationMillis = 150)
            )
            // Quick scale down
            heartbeatScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 150)
            ) // smaller second beat
            delay(timeMillis = 300)
            heartbeatScale.animateTo(
                targetValue = 1.07f,
                animationSpec = tween(durationMillis = 120)
            )
            heartbeatScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 120)
            )
        }
    }
    // auto- scroll effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentIndex = (currentIndex + 1) % promoOffers.size
        }
    }
    Box (
        modifier = Modifier.fillMaxWidth()
            .height(200.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center

    ){
        // fixed promotional text on the left
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart )
                .padding(start = 24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // main offer text
            Text(
                text = "UP TO\n65% Off",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif, // Clean, modern look for promotional text
                    fontSize  = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001C4E),
                    lineHeight = 40.sp

                )
            )
            Divider(
                modifier = Modifier
                    .padding(top = 8.dp , bottom = 8.dp)
                    .width(100.dp)
                    .height(2.dp),
                color = Color(0xFF001C4E).copy(alpha = 0.3f)
            )
            Text(
                text = "Ends Soon",
                style = TextStyle(
                    fontFamily = FontFamily.SansSerif, // Consistent with main offer text
                    fontSize  = 16.sp, // Fixed: was 24.dp, should be sp for font size
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF001C4E),
                    lineHeight = 20.sp // Adjusted to match font size

                ),
                modifier = Modifier.scale(heartbeatScale.value)
            )

        }
        Box(
            modifier = Modifier.align(Alignment.CenterEnd)
                .padding(end = 24.dp),
            contentAlignment = Alignment.Center
        ){
            promoOffers.forEachIndexed {index , offer ->
                val visibleState = remember {
                    MutableTransitionState(index == currentIndex)
                }
                LaunchedEffect(currentIndex) {
                    visibleState.targetState = (index == currentIndex)
                }
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = fadeIn(animationSpec = tween(durationMillis = 300)) +
                            scaleIn(initialScale = 0.8f, animationSpec = tween(durationMillis = 300)),
                    exit = fadeOut(animationSpec = tween(durationMillis = 300)) +
                            scaleOut(targetScale = 0.8f, animationSpec = tween(durationMillis = 300)),
                ) {
                    ProductCards(offer.imageResId)

                }
            }
        }

    }
}