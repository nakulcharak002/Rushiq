package com.example.rushiq.ui.theme.screen


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.BiasAbsoluteAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
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

                ) {
                    if (product.imageUrl.isNotEmpty() || product.imageRes != 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(Color.White)

                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(product.imageUrl.ifEmpty { product.imageRes })
                                    .crossfade(true)
                                    .build(),
                                contentDescription = product.name,
                                modifier = Modifier.fillMaxWidth()
                                    .align(Alignment.Center),
                                contentScale = ContentScale.Inside,
                                placeholder = painterResource(R.drawable.shopping_bag_svgrepo_com)
                            )
                            //share button at topleft corner
                            IconButton(
                                onClick = {/* share functionality*/ },
                                modifier = Modifier
                                    .align(Alignment.TopStart)
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
                            text = "Trusted By ${totalUsers / 100000.0} Lakh + Customers",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Product name
                        Text(
                            text = product.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )

                        // Quantity info
                        Text(
                            text = "30 Pieces",
                            fontSize = 14.sp,
                            color = Color(0xFF666666)
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


                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = String.format("%.1f ", product.rating?.rate ?: 0.0),
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
                                text = "(${product.rating?.count ?: 0})",
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
                                text = "₹${(product.price * (1 + discountPercentage / 100.0)).toInt()}",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textDecoration = TextDecoration.LineThrough
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // Discount
                            Text(
                                text = "${discountPercentage}% Off",
                                fontSize = 14.sp,
                                color = Color(0xFF007148),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.lightining_svgrepo_com__1_),
                                contentDescription = "Fast Delivery",
                                modifier = Modifier.size(30.dp),
                                tint = Color(0xFFFFC700)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "Estimated Delivery Time : 6 mins",
                                fontSize = 14.sp,
                                color = Color(0xFF0D7148),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    HorizontalDivider(thickness = 8.dp, color = Color(0xFFEEEEEE))
                    //Service Features section
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Icon(
                                painter = painterResource(R.drawable.exchange_diagonal_svgrepo_com),
                                contentDescription = "Exchange",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF666666)

                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "3 Days Exchange",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF333333),
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // FAST DELIVERY
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Icon(
                                painter = painterResource(R.drawable.scooter_svgrepo_com),
                                contentDescription = "Fast Delivery",
                                modifier = Modifier.size(32.dp),
                                tint = Color(0xFF666666)

                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Fast Delivery",
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = Color(0xFF333333),
                            )
                        }

                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))

// Highlights section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Highlights",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Spacer(modifier = Modifier.height(8.dp))

                        // Key Features
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Key Features",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.width(100.dp)
                            )

                            Text(
                                text = product.description ?: "",
                                fontSize = 14.sp,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                    HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))
                    //Information section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF333333)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // disclaimer


                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Disclaimer",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                modifier = Modifier.width(100.dp)
                            )

                            Text(
                                text = "All images are for representation purposes only, it is advised that you read the batch and manufacturing details, directions for use, allergen information, health and nutritional ",
                                fontSize = 14.sp,
                                color = Color(0xFF333333),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 4
                            )
                        }
                    }
                    // extra padding at the bottom to ensure all the content is scrollable
                    Spacer(modifier = Modifier.height(50.dp))
                }
                // bottom add to cart section
                Surface(
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Button with counter
                        Box(
                            contentAlignment = Alignment.TopEnd,
                            modifier = Modifier
                                .padding(end = 16.dp)
                        ) {
                            IconButton(
                                onClick = { /* Open cart */ },
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White, RoundedCornerShape(8.dp))
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(
                                        1.dp,
                                        Color(0xFFEEEEEE),
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.cart_shopping_fast_svgrepo_com),
                                    contentDescription = "Cart",
                                    tint = Color(0xFF666666)
                                )
                            }

                            // Item counter badge
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFF3E5E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cartViewModel.getTotalItems().toString(),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Add to cart button or quantity control
                        if(!isInCart||quantity == 0){
                            Button(
                                onClick = {
                                    cartViewModel.addToCart(product)
                                    isInCart = true
                                    quantity = 1

                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFE91E63) // PINK COLOR
                                ),
                                shape = RoundedCornerShape(8.dp)

                            ){
                                Text(
                                    text = "Add to cart",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }

                        } else {
                            // quantity control (when already in cart )
                            Row(
                                modifier = Modifier.weight(1f)
                                    .height(48.dp)
                                    .border(1.dp , Color(0xFFE91E63), RoundedCornerShape(8.dp)),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                // minus button
                                IconButton(
                                    onClick = {
                                        if(quantity > 0){
                                            quantity--
                                            if(quantity == 0){
                                                cartViewModel.removeFromCart(product)
                                                isInCart = false
                                            }else{
                                                cartViewModel.updateQuantity(product , quantity)

                                            }

                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    Text(
                                        text = "-",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE91E63)
                                    )

                                }
                                // quantity display
                                Text(
                                    text = quantity.toString(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE91E63),
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center

                                )
                                // plus button
                                IconButton(
                                    onClick = {
                                        quantity++
                                        cartViewModel.updateQuantity(product , quantity)

                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()

                                ) {
                                    Text(
                                        text = "+",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color  = Color(0xFFE91E63)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}