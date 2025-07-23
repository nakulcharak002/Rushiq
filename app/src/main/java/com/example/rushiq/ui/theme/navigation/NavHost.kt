package com.example.rushiq.ui.theme.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rushiq.ui.theme.screen.AccountScreen
import com.example.rushiq.ui.theme.screen.HomeScreen
import com.example.rushiq.ui.theme.viewmodels.ProductViewModel
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.rushiq.ui.theme.screen.AuthScreen
import com.example.rushiq.ui.theme.screen.CafeScreen
import com.example.rushiq.ui.theme.screen.CartScreen
import com.example.rushiq.ui.theme.screen.CategoryType
import com.example.rushiq.ui.theme.screen.ProductDetailScreen
import com.example.rushiq.ui.theme.screen.WelcomeScreen
import com.example.rushiq.ui.theme.viewmodels.AuthViewModel
// Add these missing imports:
// import com.example.rushiq.ui.theme.screen.SearchScreen
// import com.example.rushiq.ui.theme.screen.CategoriesScreen
import kotlinx.coroutines.delay

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RushiqNavHost(
    navController: NavHostController,
    paddingValues: PaddingValues,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onNavGraphInitialized: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel(),
    productViewModel: ProductViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel(),
) {
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val products by productViewModel.products.collectAsState()
    var isAuthLoading by remember { mutableStateOf(true) }

    // Fixed: Use remember with mutableStateOf properly
    var selectedProductId by remember { mutableStateOf<Int?>(null) }
    val selectedProduct = selectedProductId?.let { id -> products.find { it.id == id } }

    val startDestination = if (isAuthenticated) {
        RushiqDestination.Welcome.route
    } else {
        RushiqDestination.Auth.route
    }

    LaunchedEffect(Unit) {
        delay(200)
        authViewModel.checkAuthStatus()
        isAuthLoading = false
        Log.d("ZeptoNavGraph", "Initial Auth check Complete : isAuthenticated = $isAuthenticated")
    }

    LaunchedEffect(isAuthenticated) {
        Log.d("ZeptoNavGraph", "Auth state change into navGraph : $isAuthenticated")
        if (navController.currentBackStackEntry != null) {
            val currentRoute = navController.currentBackStackEntry?.destination?.route
            Log.d("ZeptoNavGraph", "Current Route : $currentRoute")
            if (isAuthenticated && currentRoute == RushiqDestination.Auth.route) {
                Log.d("ZeptoNavGraph", "navigate from auth to welcome")
                navController.navigate(RushiqDestination.Welcome.route) {
                    popUpTo(RushiqDestination.Auth.route) { inclusive = true }
                }
                onBottomBarVisibilityChange(false)
            }
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(RushiqDestination.Welcome.route) {
                LaunchedEffect(Unit) {
                    onBottomBarVisibilityChange(false)
                }
                WelcomeScreen(
                    navController = navController,
                    onCategorySelected = { category ->
                        when (category) {
                            CategoryType.EVERYDAY -> {
                                navController.navigate(RushiqDestination.Home.route)
                                onBottomBarVisibilityChange(true)
                            }
                            CategoryType.CAFE -> {
                                navController.navigate(RushiqDestination.Cafe.route)
                                onBottomBarVisibilityChange(false)
                            }
                        }
                    }
                )
            }

            // Fixed Cafe screen composable
            composable(RushiqDestination.Cafe.route) {
                LaunchedEffect(Unit) {
                    onBottomBarVisibilityChange(false)
                }
                CafeScreen(
                    paddingValues = paddingValues,
                    onNavigationToCategory = { categoryId ->
                        Log.d("Navigation", "Navigating to cafe category: $categoryId")
                        navController.navigate(RushiqDestination.CategoryDetail.createRoute(categoryId))
                    },
                    navHostController = navController,
                    onProductClick = { productId ->
                        selectedProductId = productId
                    },
                    cartViewModel = cartViewModel
                )
            }

            composable(RushiqDestination.Auth.route) {
                LaunchedEffect(Unit) {
                    onBottomBarVisibilityChange(false)
                }
                AuthScreen(
                    onAuthSuccess = {
                        navController.navigate(RushiqDestination.Welcome.route) {
                            popUpTo(RushiqDestination.Auth.route) { inclusive = true }
                        }
                        onBottomBarVisibilityChange(true)
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    viewModel = authViewModel
                )
            }

            // Fixed HomeScreen composable
            composable(RushiqDestination.Home.route) {
                LaunchedEffect(Unit) {
                    onBottomBarVisibilityChange(true)
                }
                HomeScreen(
                    paddingValues = PaddingValues(),
                    onNavigationToCategory = { categoryId ->
                        Log.d("Navigation", "Navigating to category : $categoryId")
                        navController.navigate(RushiqDestination.CategoryDetail.createRoute(categoryId))
                    },
                    navHostController = navController,  // Changed from navController to navHostController
                    onProductClick = { productId ->
                        selectedProductId = productId
                    },
                    cartViewModel = cartViewModel  // Added missing cartViewModel parameter
                )
            }

            composable(RushiqDestination.Search.route) {
                LaunchedEffect(Unit) {
                    onBottomBarVisibilityChange(false)
                }
                // Uncomment when SearchScreen is available
                /*
                SearchScreen(
                    paddingValues = PaddingValues(),
                    navController = navController,
                    onNavigateBack = {
                        navController.navigateUp()
                        onBottomBarVisibilityChange(true)
                    }
                )
                */
            }

            composable(RushiqDestination.Cart.route) {
                LaunchedEffect(Unit) {
                    onBottomBarVisibilityChange(true)
                }
                CartScreen(
                    paddingValues = PaddingValues(),
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(RushiqDestination.Account.route) {
                LaunchedEffect(Unit) {
                    onBottomBarVisibilityChange(true)
                }
                AccountScreen(
                    paddingValues = PaddingValues(),
                    navController = navController,
                    authViewModel = authViewModel,
                    onLogout = {
                        Log.d("ZeptoNavGraph", "Logout initiated from account screen")
                        authViewModel.signOut()
                    }
                )
            }

            // Category detail route
            composable(
                route = RushiqDestination.CategoryDetail.route,
                arguments = listOf(
                    navArgument("categoryId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId") ?: ""
                LaunchedEffect(Unit) {
                    onBottomBarVisibilityChange(false)
                }
                // Uncomment when CategoriesScreen is available
                /*
                CategoriesScreen(
                    categoryId = categoryId,
                    onNavigateBack = {
                        navController.navigateUp()
                        onBottomBarVisibilityChange(true)
                    },
                    onProductClick = { productId ->
                        selectedProductId = productId // Fixed: Direct assignment
                    }
                )
                */
            }

            composable(
                RushiqDestination.ProductDetails.route,
                arguments = listOf(
                    navArgument("productId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                LaunchedEffect(Unit) {
                    selectedProductId = productId // Fixed: Direct assignment
                    onBottomBarVisibilityChange(false)
                }
            }
        }

        // Product detail overlay
        selectedProduct?.let { product ->
            ProductDetailScreen(
                product = product,
                isVisible = true,
                cartViewModel = cartViewModel,
                onDismiss = {
                    selectedProductId = null // Fixed: Direct assignment
                    onBottomBarVisibilityChange(true)
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        onNavGraphInitialized()
    }
}