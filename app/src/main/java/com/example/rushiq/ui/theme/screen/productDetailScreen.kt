package com.example.rushiq.ui.theme.screen

import android.telephony.PhoneNumberUtils.formatNumber
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import com.example.rushiq.R
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.ui.theme.viewmodels.CartViewModel

@Composable
fun ProductDetailScreen(
    product: Products,
    isVisible : Boolean,
    cartViewModel: CartViewModel,
    onDismiss : () -> Unit,
) {
    var isInCart by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(0) }
    //Update the state when the dialog appear or current changes
    LaunchedEffect(isVisible , product.id) {
        if(isVisible){
            isInCart = cartViewModel.isInCart(product)
            quantity = cartViewModel.getQuantity(product)
        }
    }
    if(isVisible){
        val context = LocalContext.current

        // hard coded value
        val discountPercentage = 54
        val totalUsers = 2854
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x99000000))
                .clickable(onClick = onDismiss)

        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.TopCenter)
                    .offset(y = (180).dp)
                    .zIndex(1f)

            ){
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xCD000000), RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )

                }

            }
            // bottom sheet content
            Box(
                modifier = Modifier
                    .align (Alignment.BottomCenter)
                    .fillMaxHeight(0.75f)
                    .clip(RoundedCornerShape(topStart = 16.dp , topEnd = 16.dp))
                    .background(Color.White)
                    .clickable(onClick = {})

            ){
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 80.dp)

                ){
                    if(product.imageUrl.isNotEmpty() || product.imageRes != 0){
                        Box (
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(Color.White)

                        ){
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(product.imageUrl.ifEmpty { product.imageRes })
                                    .crossfade(true)
                                    .build(),
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxWidth()
                                    .align (Alignment.Center),
                                contentScale = ContentScale.Crop,
                                placeholder = painterResource(R.drawable.shopping_bag_svgrepo_com)
                            )
                            //share button at topleft corner
                            IconButton(
                                onClick = {/* share functionality*/},
                                modifier = Modifier
                                    .align (Alignment.TopStart)
                                    .padding(start = 8.dp)
                                    .size(60.dp)
                                    .offset(y = (235).dp)
                                    .background(Color.White, RoundedCornerShape(20.dp))
                                    .zIndex(1f)

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    tint = Color.Black
                                )
                            }

                        }
                    }
                      // Product details
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {

                            // Trusted by text
                            Text(
                                    text = "Trusted By ${totalUsers/100000.0} Lakh + Customers",
                                fontSize = 14.sp,
                                color = Color( 0xFF666666)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Product name
                            Text(
                                text = product.name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color( 0xFF333333)
                            )

                            // Quantity info
                            Text(
                                text = "30 Pieces",
                                fontSize = 14.sp,
                                color = Color( 0xFF666666)
                            )

                            // Rating row - Using API data
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {

                                // Rating pill
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF0D7148)


                                    ){
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp , vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text = String.format("%.1f ", product.rating?.rate?:0.0),
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                         imageVector = Icons.Default.Star,
                                            contentDescription = "Rating",
                                            modifier = Modifier.size(12.dp),
                                            tint = Color.White

                                        )

                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                // rating count
                                Text(
                                    text = "(${product.rating?.count?:0})",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666)
                                )

                            }
                            // Price section
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 8.dp)
                            ) {
                                // Current price
                                Text(
                                    text = "₹${product.price.toInt()}",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Original price (strikethrough)
                                Text(
                                    text = "₹${(product.price * (1 + discountPercentage/100.0)).toInt()}",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    textDecoration = TextDecoration.LineThrough
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Discount
                                Text(
                                    text = "${discountPercentage}% Off",
                                    fontSize = 14.sp,
                                    color = Color( 0xFF007148),
                                fontWeight = FontWeight.Bold
                                )
                            }
                        }

                }

            }


        }

    }

}