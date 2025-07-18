package com.example.rushiq.ui.theme.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.rushiq.ui.theme.navigation.ZeptoDestinations
import com.example.rushiq.ui.theme.viewmodels.AuthViewModel
import java.nio.file.WatchEvent



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  AccountScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel(),
    onLogout : ()-> Unit = {authViewModel.signOut()},


) {
    val currentUser = authViewModel.getCurrentUser()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val rushiqPurple = Color( 0xFF5D0079)
    val rushiqPink = Color( 0xFFFF4D80)
    val rushiqLightPurple = Color(0xFFF2E7F7)
    val dividerColor = Color(0xFFEEEEEE)

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Account" , fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = {navController.navigateUp()}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        },
        containerColor = rushiqLightPurple
    ){innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // profile section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                //profile picture
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(rushiqPurple),
                    contentAlignment = Alignment.Center
                )
                {
                    Text(
                        text = (currentUser?.displayName?.firstOrNull()?:"U").toString(),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                // user info
                Text(
                    text = currentUser?.displayName?:"User",
                  style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                // user phone Number
                Text(
                    text = currentUser?.phoneNumber?:"7051671602",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                )

            }
            Spacer(modifier = Modifier.height(8.dp))
            //Quick action row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionItem(
                    icon = Icons.Default.ShoppingCart,
                    title = "Your\nOrders",
                    onClick = {navController.navigate(ZeptoDestinations.PaymentHistory.route)}
                )
                QuickActionItem(
                    icon = Icons.Default.Phone,
                    title = "Help &\n Support",
                    onClick = { navController.navigate("support") }
                )
                QuickActionItem(
                    icon = Icons.Default.AccountCircle,
                    title = "Zepto\nCash",
                    onClick = { navController.navigate("wallet") }
                )


            }
            Spacer(modifier = Modifier.height(8.dp))
            // Rushiq Pass Card
            RushiqPassCard(
                onClick = {navController.navigate("subscription")}
            )
            Spacer(modifier = Modifier.height(8.dp))

            // RushiqCashCard
            RushiqCashCard(
                balance = "₹0",
                onClick = {navController.navigate("wallet")}
            )










        }



    }

}
@Composable
fun QuickActionItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
){
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp)
    ){
        Icon(
            painter = painterResource(id = icon),
            contentDescription = title ,
            modifier = Modifier.size(28.dp),
            tint = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )

    }

}
 @Composable
 fun RushiqPassCard(onClick: () -> Unit){
     Card(
         modifier = Modifier
             .fillMaxWidth()
             .padding(horizontal = 16.dp)

             .clickable(onClick = onClick),
         shape = RoundedCornerShape(12.dp),
         colors = CardDefaults.cardColors(containerColor = Color(0xFF5D0079))
     ) {
         Box(
             modifier = Modifier
                 .fillMaxWidth()
                 .padding(16.dp)


         ){
             Column {
                 Row(
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     Text(
                         text = "Rushiq",
                         color = Color.White,
                         fontSize = 18.sp,
                         fontWeight = FontWeight.Bold,

                     )
                     Spacer(modifier = Modifier.width(4.dp))
                     Text(
                         text = "Pass",
                         color = Color.White,
                         fontSize = 18.sp,
                         fontWeight = FontWeight.ExtraBold,
                         modifier = Modifier
                             .background(
                                 color = Color(0xFFFFD700),
                                 shape = RoundedCornerShape(4.dp)

                             )
                             .padding (horizontal = 4.dp , vertical = 2.dp)
                     )
                 }
                 Spacer(modifier = Modifier.height(8.dp))
                 Text(
                     text = "You would potentially save ₹450 per month with Rushiq Pass",
                     color = Color.White,
                     fontSize = 16.sp
                 )
                 Spacer(modifier = Modifier.height(16.dp))
                 Button(
                     onClick = onClick,
                     colors = ButtonDefaults.buttonColors(
                         containerColor = Color(0xFFFFD700)
                     ),
                     shape = RoundedCornerShape(8.dp),
                     modifier = Modifier.align (Alignment.CenterHorizontally)
                 ){
                     Text(
                         text = "Renew Rushiq Pass ",
                         color = Color.Black,
                         fontWeight = FontWeight.Bold
                     )
                     Icon(
                         imageVector = Icons.Default.KeyboardArrowRight,
                         contentDescription = null,
                         tint = Color.Black
                     )

                 }

             }

         }

     }
 }
@Composable
fun RushiqCashCard(balance : String, onClick: () -> Unit){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF2E7F7))
    ){
        Row (
            modifier =  Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically

        ){
            Text(
                text = "💰",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Zepto Cash & Gift Card",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)

            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )

        }
        Divider(color = Color.LightGray.copy(0.5f))
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Column {
                Text(text = "Available Balance",
                    fontSize = 18.sp,
                    color = Color.Gray
                    )
                Text(text = " balance",
                    fontSize = 18.sp,
                   fontWeight = FontWeight.Bold
                )

            }

        }

    }
}
