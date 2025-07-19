package com.example.rushiq.ui.theme.screen.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.rushiq.data.models.fakeapi.CartItem
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.rushiq.R

@Composable
fun EnhancedCartItemRow(
    cartItem: CartItem,
    cartViewModel: CartViewModel

){
    val product = cartItem.products
    val buttonColor = Color(0xFFFF3F6C)
    val context = LocalContext.current

    Row (
modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
      Box(
          // product image
          modifier = Modifier.size(70.dp)
          .clip(RoundedCornerShape(8.dp))
              .background(Color.White)
              .border(0.5.dp , Color.LightGray, RoundedCornerShape(8.dp))
      ){
      if(product.imageUrl.isNotEmpty()){
          AsyncImage(
              model = ImageRequest.Builder(context)
                  .data(product.imageUrl)
                  .crossfade(true)
                  .build(),
              contentDescription = product.name,
              modifier = Modifier.fillMaxSize()
                  .padding(4.dp),
              contentScale = ContentScale.Fit
          )
      }else{
          Image(
              painter= painterResource(R.drawable.shopping_bag_svgrepo_com),
              contentDescription = product.name,
              modifier = Modifier.fillMaxSize().padding(8.dp)
          )
      }
      }
        Spacer(modifier = Modifier.width(4.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "1 l",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Price with discount
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₹${product.price.toInt()}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                if (product.price < 300) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "₹${(product.price * 1.4).toInt()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textDecoration = TextDecoration.LineThrough
                    )
                }
            }
        }
        Box(
           modifier = Modifier
               .clip(RoundedCornerShape(4.dp))
               .border(1.dp , Color(0xFFE0E0E0), RoundedCornerShape(4.dp))
               .background(Color(0xFFFFF3F7))

        ){
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.height(36.dp)

            ){
                Box(
                    modifier = Modifier.size(36.dp)
                        .clickable {
                            cartViewModel.removeFromCart(product)
                        },
                    contentAlignment = Alignment.Center
                ){
                    Text(
                        text = "-",
                        color = buttonColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )// quantity display
                    Box(
                        modifier = Modifier.width(36.dp),
                        contentAlignment = Alignment.Center

                    ){
                        Text(
                            text="${cartItem.quantity}",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                        )

                    }
                    Box (
                        modifier = Modifier.size(36.dp)
                            .clickable {cartViewModel.addToCart(product)
                            },
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = "+",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,

                        )
                    }

                }

            }

        }

    }

}