package com.example.rushiq.ui.theme.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rushiq.ui.theme.screen.HomeScreen
import com.example.rushiq.ui.theme.viewmodels.ProductViewModel
import com.example.rushiq.ui.theme.viewmodels.CartViewModel // ✅ added import
import com.example.rushiq.ui.theme.navigation.ZeptoDestinations
import com.example.rushiq.ui.theme.screen.AuthScreen
import com.example.rushiq.ui.theme.viewmodels.AuthViewModel
import com.google.firebase.auth.oAuthProvider

@Composable
fun ZeptoNavGraph(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onNavGraphInitialized: () -> Unit = {},

    // ViewModels (uncomment when implementing these)
    // val authViewModel: AuthViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(), //
) {
    // Collect the product list from the ViewModel
    val products by productViewModel.products.collectAsState()

    // Manage selected product ID using state
    val (selectedProductId, setSelectedProductId) = remember { mutableStateOf<Int?>(null) }

    // Get the full product object from the list based on selected ID
    val selectedProduct = selectedProductId?.let { id -> products.find { it.id == id } }

    // Call this when the NavGraph is initialized
    LaunchedEffect(Unit) {
        onNavGraphInitialized()
    }

    // Define the navigation graph
    NavHost(
        navController = navController,
        startDestination = ZeptoDestinations.Home.route
    ) {
        // Home Screen
        composable(ZeptoDestinations.Home.route) {
            HomeScreen(
                paddingValues = paddingValues,
                navHostController = navController,
                onNavigationToCategory = { categoryId ->
                    navController.navigate(ZeptoDestinations.CategoryDetail.createRoute(categoryId))
                },
                onProductClick = { productId ->
                    setSelectedProductId(productId)
                },
                cartViewModel = cartViewModel //
            )
        }

        composable(ZeptoDestinations.Auth.route){
            LaunchedEffect(Unit) {
                onBottomBarVisibilityChange(false)
            }
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(ZeptoDestinations.Welcome.route){
                        popUpTo(ZeptoDestinations.Auth.route){inclusive = true}

                    }
                    onBottomBarVisibilityChange(true)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                viewModel = authViewModel
            )
        }
    }
}
