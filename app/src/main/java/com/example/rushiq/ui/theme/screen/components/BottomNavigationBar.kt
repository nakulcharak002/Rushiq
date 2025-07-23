import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.rushiq.R
import androidx.compose.material3.Icon

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    cartViewModel: CartViewModel,
    currentRoute: String,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val cartItemCount by cartViewModel.totalItems.collectAsState()

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
        containerColor =
            Color.White,
        tonalElevation = 8.dp
    ) {
        // Back Button (if enabled) as first item
        if (showBackButton) {
            NavigationBarItem(
                selected = false,
                onClick = onBackClick,
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.back_svgrepo_com),
                        contentDescription = "Back",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = "Back",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF5521AB),
                    selectedTextColor = Color(0xFF5521AB),
                    unselectedIconColor = Color(0xFF5521AB),
                    unselectedTextColor = Color(0xFF5521AB),
                    indicatorColor = Color(0xFF5521AB).copy(alpha = 0.1f)
                )
            )
        }

        // Home Tab
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (selectedTab == 0) R.drawable.home_filled
                        else R.drawable.home_svgrepo_com
                    ),
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5521AB),
                selectedTextColor = Color(0xFF5521AB),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF5521AB).copy(alpha = 0.1f)
            )
        )

        // Cafe Tab
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (selectedTab == 1) R.drawable.cafe_svgrepo_com__1_
                        else R.drawable.cafe_outline_svgrepo_com
                    ),
                    contentDescription = "Cafe",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Cafe",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5521AB),
                selectedTextColor = Color(0xFF5521AB),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF5521AB).copy(alpha = 0.1f)
            )
        )

        // Cart Tab (with badge)
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = Color.Red,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (selectedTab == 2) R.drawable.cart_filled
                            else R.drawable.cart_outline
                        ),
                        contentDescription = "Cart",
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = "Cart",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5521AB),
                selectedTextColor = Color(0xFF5521AB),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF5521AB).copy(alpha = 0.1f)
            )
        )

        // Account Tab
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    painter = painterResource(
                        id = if (selectedTab == 3) R.drawable.account_filled
                        else R.drawable.account_outline
                    ),
                    contentDescription = "Account",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Account",
                    fontSize = 12.sp,
                    fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFF5521AB),
                selectedTextColor = Color(0xFF5521AB),
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray,
                indicatorColor = Color(0xFF5521AB).copy(alpha = 0.1f)
            )
        )
    }
}