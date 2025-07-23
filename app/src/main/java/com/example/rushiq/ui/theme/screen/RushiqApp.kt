import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import com.example.rushiq.data.models.fakeapi.Category
import com.example.rushiq.ui.theme.navigation.RushiqDestination
import com.example.rushiq.ui.theme.navigation.RushiqNavHost
import com.example.rushiq.ui.theme.viewmodels.AuthViewModel
import com.example.rushiq.ui.theme.viewmodels.HomeViewModel
import kotlinx.coroutines.Job

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RushiqApp(
    initialAuthenticated: Boolean = false,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    var selectedTab by remember { mutableStateOf(0) }
    // Remember previous tab to handle navigation
    var previousTab by remember { mutableStateOf(0) }

    // Use proper viewModel instance with hiltViewModel()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    var authCheckComplete by remember { mutableStateOf(false) }

    val navController = rememberNavController() // Fixed typo: was "remeberNavController"
    //track if the navigation graph have been initialized
    var navGraphInitialized by remember { mutableStateOf(false) }

    var bottomBarVisible by remember { mutableStateOf(initialAuthenticated) }
    // Create a debounced coroutine scope for handling scroll events with slight delay
    val coroutineScope = rememberCoroutineScope()
    var showBottomJob: Job? = remember { null }

    // Effect to handle authentication state changes
    LaunchedEffect(isAuthenticated, authCheckComplete) {
        if (!authCheckComplete) return@LaunchedEffect

        if (navGraphInitialized) {
            if (isAuthenticated) {
                bottomBarVisible = false // Start with bottom bar hidden for welcome screen
            } else {
                navController.navigate(RushiqDestination.Auth.route) {
                    popUpTo(navController.graph.id) { inclusive = true }
                }
                bottomBarVisible = false
            }
        }
    }

    LaunchedEffect(Unit) {
        Log.d("RushiqApp", "Initial startUp :Checking authentication status ")
        // check authentication status immediately
        authViewModel.checkAuthStatus()
        // mark the check at the once so that mean we have checked it once
        authCheckComplete = true
    }

    // hide bottom bar when cart screen is active
    LaunchedEffect(selectedTab) {
        // only execute
        if (!navGraphInitialized) return@LaunchedEffect
        // if we are navigating to cart screen(tab2), hide the bottom bar
        if (selectedTab == 2) {
            bottomBarVisible = false
            if (previousTab != 2) {
                previousTab = selectedTab
            }
        } else {
            bottomBarVisible = true
            previousTab = selectedTab
        }
    }

    LaunchedEffect(selectedTab, navGraphInitialized) {
        if (!navGraphInitialized) return@LaunchedEffect

        val currentRoute = navController.currentBackStackEntry?.destination?.route // Fixed typo: was "currBackStackEntry"
        val isOnWelcomeOrAuthScreen =
            currentRoute == RushiqDestination.Welcome.route || currentRoute == RushiqDestination.Auth.route
        if (isOnWelcomeOrAuthScreen) {
            return@LaunchedEffect
        }
        when (selectedTab) {
            0 -> navController.navigate(RushiqDestination.Home.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }

            1 -> navController.navigate(RushiqDestination.Cafe.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }

            2 -> navController.navigate(RushiqDestination.Cart.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }

            3 -> navController.navigate(RushiqDestination.Account.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    val viewModel: HomeViewModel = hiltViewModel()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val defaultGradient =
        Brush.verticalGradient(
            colors =
                listOf(
                    Color(color = 0xFF5521AB),
                    Color(color = 0xFF8C4FFE),
                ),
        )

    val backgroundBrush =
        if (selectedTab == 0) {
            selectedCategory?.let { getCategoryGradient(it) } ?: defaultGradient
        } else {
            // For other tabs, use a different background as a brush
            Brush.verticalGradient(listOf(Color.White, Color.White))
        }

    // Configure edge-to-edge display and transparent status bar
    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        window.statusBarColor = Color.Transparent.toArgb()
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        // Main content
        RushiqNavHost( // This should be your custom NavHost composable
            navController = navController,
            paddingValues = PaddingValues(bottom = 0.dp),
            onBottomBarVisibilityChange = { visible ->
                bottomBarVisible = visible
            },
            onNavGraphInitialized = {
                navGraphInitialized = true
            }
        )

        // Overlay the navigation bar on top
        if (bottomBarVisible) {
            Box(
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                BottomNavigationBar(
                    navController = navController,
                    cartViewModel = hiltViewModel(),
                    currentRoute = navController.currentBackStackEntry?.destination?.route
                        ?: "",
                    showBackButton = true,
                    onBackClick = {
                        navController.navigate(RushiqDestination.Welcome.route) {
                            launchSingleTop = true
                        }
                        bottomBarVisible = false
                    },
                    selectedTab = selectedTab,
                    onTabSelected = { newTab ->
                        selectedTab = newTab
                    }
                )
            }
        }
    }
}

fun getCategoryGradient(category: Category): Brush {
    return when (category.name.lowercase()) {
        "jewelery" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF9D6300), // Dark gold
                Color(0xFFFDD38F)  // Light gold
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        "electronics" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF012654), // Deep navy blue
                Color(0xFF78C4FF)  // Bright blue
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        "women's clothing" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF77011F), //  Rich red
                Color(0xFFFF89A3)  // Light pink
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        "men's clothing" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF005F1C), // Deep green
                Color(0xFF7AFAA5)  //  Light mint
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        "all" -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF2B0466), // Deep violet
                Color(0xFFA669FF)  // Light purple
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )

        else -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF985C00), // Deep amber
                Color(0xFFFFCC80)  // Peachy gold
            ),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )
    }
}