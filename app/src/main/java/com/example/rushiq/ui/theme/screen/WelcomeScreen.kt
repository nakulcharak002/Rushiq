package com.example.rushiq.ui.theme.screen

import com.example.rushiq.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rushiq.ui.theme.screen.components.LocationBar
import com.example.rushiq.ui.theme.screen.components.PromoCardCarousel

// Add this enum if it doesn't exist
enum class CategoryType {
    EVERYDAY,
    CAFE
}

@Composable
fun WelcomeScreen(
    onCategorySelected: (CategoryType) -> Unit,
    navController: NavController,
) {
    val scrollState = rememberScrollState()
    val deliveryTime = "6 Mins"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(color = 0xFFF5F5F5)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState),
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(color = 0xFF333535),
                        shape = RoundedCornerShape(topStart = 20.dp, bottomEnd = 20.dp),
                    )
                    .padding(16.dp),
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

                // cafe card
                CafeCard(
                    onClick = { onCategorySelected(CategoryType.CAFE) },
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun EveryDayEssentialCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF9C2780),
                            Color(0xFF6A0080),
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFF4A0072).copy(alpha = 0.8f)),
            ) {
                Text(
                    text = "No Minimum Order value",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 2.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .padding(bottom = 32.dp),
                ) {
                    Text(
                        text = "Everyday",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Essential Store",
                        fontSize = 30.sp, // Fixed: was 30.dp, should be 30.sp
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 16.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Go to Everyday Essentials",
                            tint = Color.White
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Text(
                        text = "Rushiq",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Box(
                        modifier = Modifier
                            .size(144.dp)
                            .padding(top = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.everyday),
                                contentDescription = "Everyday Essential Products",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Inside
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CafeCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF8B4513),
                            Color(0xFF5D2906),
                        ),
                    ),
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .align(Alignment.BottomCenter)
                    .background(Color(0xFF4A0072).copy(alpha = 0.8f)),
            ) {
                Text(
                    text = "Coffee & Snacks in a Minutes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 2.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                        .padding(bottom = 32.dp),
                ) {
                    Text(
                        text = "Rushiq",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = "Cafe",
                        fontSize = 30.sp, // Fixed: was 30.dp, should be 30.sp
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 16.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Go to Cafe",
                            tint = Color.White
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Text(
                        text = "Rushiq",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    Box(
                        modifier = Modifier
                            .size(144.dp)
                            .padding(top = 20.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.coffee),
                                contentDescription = "Coffee Products",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Inside
                            )
                        }
                    }
                }
            }
        }
    }
}