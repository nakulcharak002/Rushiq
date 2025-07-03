package com.example.rushiq.ui.theme.screen.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.zepto.R

@Composable
fun LottieSaleAnimation(
    modifier: Modifier = Modifier,
    animationResId: Int = R.raw.big_sale_anim // replace with your actual Lottie JSON file
) {
    // Load the Lottie animation composition
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animationResId)
    )

    // Control the animation playback
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    // Pulse animation
    val pulseAnim = rememberInfiniteTransition()
    val scale by pulseAnim.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Container Box
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp),
        contentAlignment = Alignment.Center
    ) {
        // Card for styling
        Card(
            modifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    clip = true
                },
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            // The Lottie animation
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}
