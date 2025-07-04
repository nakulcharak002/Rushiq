package com.example.rushiq.ui.theme.screen.components

import androidx.cardview.widget.CardView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.zepto.R

@Composable
fun ProductCard(
    products: Products,
    cardViewModel: CartViewModel,
    onProductClick : (Int) -> Unit = {}
){
    val content = LocalContext.current
    val discountPercentage = remember { (Math.random()*60+20).toInt() }

     val cardItems by cardViewModel.cartItems.collectAsState()
    val quantity = cardItems.find { it.product.id == products.id  }?.quantity?:0

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
             .border(1.dp ,Color.White, RoundedCornerShape(12.dp))
             .clip(RoundedCornerShape(12.dp))
             .background(Color.White)
     ){
         if(products.imageUrl.isEmpty()){
             AsyncImage(
                 model = ImageRequest.Builder(context)
                     .data(products.imageUrl)
                     .crossFade(true)
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
                 modifier = Modifier.fillMaxSize()
                     .background(Color.White),
                     contentAlignment = Alignment.Center
             ){
                 Text("No Image ")
             }
         }
         if(products.price<300){
             Box(
                 modifier = Modifier
                     .padding(start = 0.dp , top = 0.dp)
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
         Box(

         ){

         }

     }
 }
    }

}