package com.example.rushiq.ui.theme.screen.components

import android.widget.Space
import androidx.cardview.widget.CardView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.zepto.R
import kotlin.coroutines.jvm.internal.CompletedContinuation.context

@Composable
fun ProductCard(
    products: Products,
    cardViewModel: CartViewModel,
    onProductClick : (Int) -> Unit = {}
){
    val content = LocalContext.current
    val discountPercentage = remember { (Math.random()*60+20).toInt() }

     val cardItems by cardViewModel.cartItems.collectAsState()
    val quantity = cardItems.find { it.products.id == products.id  }?.quantity?:0

    val discountBadgeShape = RoundedCornerShape(
        topStart = 12.dp,
        bottomEnd = 12.dp,
        topEnd = 0.dp,
        bottomStart = 0.dp,
    )
    val addButtonShape = RoundedCornerShape(50)
    val addButtonColor = Color(0xffe20F48)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable {
                val productId = products.id
                onProductClick(productId)
            },
        colors =  CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
 Column {
     Box (
         modifier = Modifier
             .fillMaxWidth()
             .height(130.dp)
             .border(1.dp, Color.White, RoundedCornerShape(12.dp))
             .clip(RoundedCornerShape(12.dp))
             .background(Color.White)
     ){
         if(products.imageUrl.isEmpty()){
             AsyncImage(
                 model = ImageRequest.Builder(content)
                     .data(products.imageUrl)
                     .crossfade(true)
                     .build(),
                 contentDescription = products.name,
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(8.dp),
                 contentScale = ContentScale.Fit,
                 placeholder = painterResource(R.drawable.shopping_bag_svgrepo_com)
             )
         }else if (products.imageRes != 0){
             Image(
                 painter = painterResource(products.imageRes),
                 contentDescription = products.name,
                 modifier = Modifier.fillMaxSize(),
                 contentScale = ContentScale.Fit,

             )
         }else{
             Box(
                 modifier = Modifier
                     .fillMaxSize()
                     .background(Color.White),
                     contentAlignment = Alignment.Center
             ){
                 Text("No Image ")
             }
         }
         if(products.price<300){
             Box(
                 modifier = Modifier
                     .padding(start = 0.dp, top = 0.dp)
                     .align(Alignment.TopStart)
                     .clip(discountBadgeShape)
                     .background(Color(0xFF8E22AA))
                     .padding(horizontal = 8.dp, vertical = 4.dp)
             )
             {
                Text(
                    text = "${discountPercentage}%Off",
                    color = Color.White,
                    style =MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
             }
         }
             // Add/Minus button (toggles between plus and a quantity selector with +/- buttons)
                 Box(
                 modifier = Modifier
                     .align(Alignment.BottomEnd)
                     .padding(8.dp)
             ) {
                 if (quantity == 0) {
                     // Add button
                     Box(
                         modifier = Modifier
                             .size(30.dp)
                             .clip(RoundedCornerShape(6.dp))
                             .background(Color.White)
                             .border(
                                 width = 1.dp,
                                 color = addButtonColor,
                                 shape = RoundedCornerShape(6.dp)
                             )
                             .clickable {
                                 // Add product to cart
                                 cardViewModel.addToCart(products)
                             },
                         contentAlignment = Alignment.Center
                     ) {
                         Icon(
                             imageVector = Icons.Default.Add,
                             contentDescription = "Add to cart",
                             tint = addButtonColor,
                             modifier = Modifier.size(18.dp)
                         )
                     }
                 } else {
                     Row(
                         modifier = Modifier
                             .height(32.dp)
                             .clip(addButtonShape)
                             .background(addButtonColor),
                         verticalAlignment = Alignment.CenterVertically

                     ) {
                          // minus button
                         Box(
                             modifier = Modifier
                                 .size(32.dp)
                                 .clickable {
                                     cardViewModel.removeFromCard(products)

                                 },
                             contentAlignment = Alignment.Center

                         ){
                             Text(
                                 text = "-",
                                 color = Color.White,
                                 fontSize = 20.sp,
                                 fontWeight = FontWeight.Bold
                             )

                         }
                         // quantity  display
                         Box(
                              modifier = Modifier.
                             width(32.dp),
                             contentAlignment = Alignment.Center

                         ) {
 Text(
     text = "$quantity",
     color = Color.White,
     fontWeight = FontWeight.Bold
 )
                         }
                         //plus button
                         Box(
                             modifier = Modifier
                                 .size(32.dp)
                                 .clickable {
                                     cardViewModel.addToCart(products)

                                 },
                             contentAlignment = Alignment.Center

                         ){
                             Text(
                                 text = "+",
                                 color = Color.White,
                                 fontSize = 20.sp,
                                 fontWeight = FontWeight.Bold
                             )

                         }
                     }


                 }
             }


                 }
     Column(
         modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
     ) {
         // Product name
         Text(
             text = products.name,
             style = MaterialTheme.typography.bodyMedium,
             maxLines = 2,
             overflow = TextOverflow.Ellipsis,
             fontWeight = FontWeight.Normal,
             color = Color.Black
         )

         Spacer(modifier = Modifier.height(2.dp))

         // Product piece info
         Text(
             text = "1 lt", // Matching the screenshot format
             style = MaterialTheme.typography.bodySmall,
             color = Color.Gray
         )

         Spacer(modifier = Modifier.height(2.dp))

         // Star rating - Using API data
         Row(
             verticalAlignment = Alignment.CenterVertically
         ) {
             // Star rating background in green with rating number
             Box(
                 modifier = Modifier
                     .clip(RoundedCornerShape(4.dp))
                     .background(Color(0xFF007148)) // Dark green color from screenshot
                     .padding(horizontal = 4.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
             ){
                 Row(
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     Icon(
                         imageVector = Icons.Default.Star,
                         contentDescription = "Rating",
                         tint = Color.White,
                         modifier = Modifier.size(14.dp)
                     )

                     Spacer(modifier = Modifier.width(2.dp))

                     Text(
                         text = String.format("%.1f", products.rating?.rate ?: 0.0),
                         color = Color.White,
                         style = MaterialTheme.typography.labelLarge,
                         fontWeight = FontWeight.Bold
                     )
                 }
             }
             Spacer(modifier = Modifier.height(4.dp))
             Row(
                 verticalAlignment = Alignment.CenterVertically
             ){
                 Text(
                     text = ("₹"),
                     style = MaterialTheme.typography.bodyLarge,
                     fontWeight = FontWeight.Bold,
                     color = Color.Black
                 )
                 Text(
                     text = "${products.price.toInt()}",
                     style = MaterialTheme.typography.bodyLarge,
                     fontWeight = FontWeight.Bold,
                     color = Color.Black
                 )
                 Spacer(modifier = Modifier.width(4.dp))
                 if(products.price < 300){
                     Text(
                         text = "${(products.price *1.4).toInt()}",
                         style = MaterialTheme.typography.bodySmall,
                         color = Color.Gray,
                         textDecoration = TextDecoration.LineThrough
                     )
                 }

             }

         }
     }
 }

    }

}