package com.example.rushiq.ui.theme.screen

import CustomCircularProgressIndicator
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.request.ImageRequest
import coil.compose.AsyncImage
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.ui.theme.navigation.RushiqDestination
import com.example.rushiq.ui.theme.viewmodels.CafeViewModel
import com.example.rushiq.ui.theme.screen.components.CategoriesSection
import com.example.rushiq.ui.theme.screen.components.LocationBar
import com.example.rushiq.ui.theme.screen.components.ProductCard
import com.example.rushiq.ui.theme.screen.components.SearchBar
import com.example.rushiq.ui.theme.viewmodels.CartViewModel
import com.example.rushiq.R
import com.example.rushiq.data.models.fakeapi.Category
import com.example.rushiq.data.models.mealDB.MealCategory
import getCategoryGradient

// Extension function to convert MealCategory to Category
fun MealCategory.toCategory(): Category {
    return Category(
        id = this.id.hashCode(), // Convert String to Int using hashCode
        name = this.name,
        iconRes = this.imageRes.hashCode() // Convert String to Int using hashCode
    )
}


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMotionApi::class, ExperimentalFoundationApi::class)
@Composable
fun CafeScreen(
    paddingValues: PaddingValues,
    onNavigationToCategory: (String) -> Unit,
    navHostController: NavHostController,
    onProductClick: (Int) -> Unit = {},
    cartViewModel: CartViewModel
) {
    val viewModel: CafeViewModel = hiltViewModel()
    val scrollState = rememberLazyListState()

    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val productGrid = remember(products) { products.chunked(3) }

    // State for product detail popup
    var selectedProduct by remember { mutableStateOf<Products?>(null) }
    var showDetailPopup by remember { mutableStateOf(false) }

    // Fixed scroll progress calculation
    val scrollProgress by remember {
        derivedStateOf {
            when {
                scrollState.firstVisibleItemIndex > 0 -> 1f
                else -> (scrollState.firstVisibleItemScrollOffset / 1000f).coerceIn(0f, 1f)
            }
        }
    }

    val animatedScrollProgress by animateFloatAsState(
        targetValue = scrollProgress,
        animationSpec = tween(durationMillis = 50, easing = FastOutLinearInEasing),
        label = "scroll"
    )

    val categoryBackground = remember(selectedCategory?.id) {
        selectedCategory?.let { getCategoryGradient(it.toCategory()) }
            ?: Brush.horizontalGradient(listOf(Color.White, Color.White))
    }

    var isPastThreshold by remember { mutableStateOf(false) }

    LaunchedEffect(scrollProgress) {
        val newValue = scrollProgress > 0.25f
        if (isPastThreshold != newValue) {
            isPastThreshold = newValue
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(categoryBackground)
    ) {
        MotionLayout(
            start = cafeScreenStartConstraintSet(),
            end = cafeScreenEndConstraintSet(),
            progress = animatedScrollProgress,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    renderEffect = null
                    alpha = 0.99f
                }
        ) {
            Box(
                modifier = Modifier
                    .layoutId("location_bar")
                    .background(Color.Transparent)
                    .zIndex(1f)
            ) {
                LocationBar(contentColor = Color.White)
            }

            Box(
                modifier = Modifier
                    .layoutId("search_bar")
                    .background(Color.Transparent)
                    .zIndex(0.9f)
            ) {
                SearchBar(onSearchClick = {
                    navHostController.navigate(RushiqDestination.Search.route)
                })
            }

            Box(
                modifier = Modifier
                    .layoutId("categories_section")
                    .background(Color.Transparent)
                    .zIndex(0.8f)
            ) {
                CategoriesSection(
                    categories = categories.map { it.toCategory() },
                    onCategorySelection = { category ->
                        // Since CafeViewModel doesn't have selectCategory method,
                        // we'll need to add it or handle category selection differently
                        // For now, this is a placeholder
                    },
                    selectedCategory = selectedCategory?.toCategory(),
                )
            }

            val cornerShape = remember { RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp) }
            Box(
                modifier = Modifier
                    .layoutId("main_content")
                    .background(Color.White, cornerShape)
                    .clip(cornerShape)
                    .zIndex(0.7f)
            ) {
                when {
                    isLoading && products.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CustomCircularProgressIndicator()
                        }
                    }
                    error != null && products.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(error ?: "Unknown error occurred")
                        }
                    }
                    else -> {
                        OptimizedCafeProductList(
                            scrollState = scrollState,
                            products = products,
                            productGrid = productGrid,
                            cartViewModel = cartViewModel,
                            onProductClick = { productId ->
                                val product = products.find { it.id == productId }
                                product?.let {
                                    selectedProduct = it
                                    showDetailPopup = true
                                }
                            }
                        )
                    }
                }
            }
        }

        // Error snackbar - moved outside MotionLayout
        if (error != null && products.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Snackbar {
                    Text(error ?: "Unknown error occurred")
                }
            }
        }

        // Product detail popup - moved outside MotionLayout
        selectedProduct?.let { product ->
            ProductDetailScreen(
                product = product,
                isVisible = showDetailPopup,
                cartViewModel = cartViewModel,
                onDismiss = { showDetailPopup = false }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptimizedCafeProductList(
    scrollState: LazyListState,
    products: List<Products>,
    productGrid: List<List<Products>>,
    cartViewModel: CartViewModel,
    onProductClick: (Int) -> Unit = {}
) {
    val context = LocalContext.current
    val imageLoader = remember {
        ImageLoader.Builder(context).crossfade(true).build()
    }

    val sadEmojiRequest = remember {
        ImageRequest.Builder(context)
            .data(R.raw.oops)
            .crossfade(true)
            .build()
    }

    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        flingBehavior = ScrollableDefaults.flingBehavior()
    ) {
        items(
            items = productGrid,
            key = { row -> row.firstOrNull()?.id?.toString() ?: row.hashCode().toString() }
        ) { rowProducts ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .animateItemPlacement(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (product in rowProducts) {
                    Box(modifier = Modifier.weight(1f)) {
                        key(product.id) {
                            ProductCard(
                                products = product,
                                cardViewModel = cartViewModel,
                                onProductClick = onProductClick
                            )
                        }
                    }
                }
                repeat(3 - rowProducts.size) {
                    Box(modifier = Modifier.weight(1f))
                }
            }
        }

        if (products.isEmpty() && !scrollState.isScrollInProgress) {
            item(key = "not_found_selection") {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, bottom = 16.dp)
                        .animateItemPlacement(),
                ) {
                    CafeNotFoundSection(
                        imageLoader = imageLoader,
                        sadEmojiRequest = sadEmojiRequest,
                    )
                }
            }
        }
    }
}

// Cafe screen constraint sets
fun cafeScreenStartConstraintSet(): ConstraintSet =
    ConstraintSet {
        val locationBar = createRefFor("location_bar")
        val searchBar = createRefFor("search_bar")
        val categorySection = createRefFor("categories_section")
        val mainContent = createRefFor("main_content")

        constrain(locationBar) {
            width = Dimension.fillToConstraints
            height = Dimension.value(95.dp)
            top.linkTo(parent.top, margin = 30.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(searchBar) {
            width = Dimension.fillToConstraints
            height = Dimension.value(60.dp)
            top.linkTo(locationBar.bottom, margin = (-12).dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(categorySection) {
            width = Dimension.fillToConstraints
            height = Dimension.value(100.dp)
            top.linkTo(searchBar.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(mainContent) {
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
            top.linkTo(categorySection.bottom, margin = (-17).dp)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
    }

fun cafeScreenEndConstraintSet(): ConstraintSet =
    ConstraintSet {
        val locationBar = createRefFor(id = "location_bar")
        val searchBar = createRefFor(id = "search_bar")
        val categorySection = createRefFor(id = "categories_section")
        val mainContent = createRefFor(id = "main_content")

        constrain(locationBar) {
            width = Dimension.fillToConstraints
            height = Dimension.value(75.dp)
            top.linkTo(parent.top, margin = 15.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            alpha = 0f
        }

        constrain(searchBar) {
            width = Dimension.fillToConstraints
            height = Dimension.value(70.dp)
            top.linkTo(parent.top, margin = 40.dp)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(categorySection) {
            width = Dimension.fillToConstraints
            height = Dimension.value(100.dp)
            top.linkTo(searchBar.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }

        constrain(mainContent) {
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
            top.linkTo(categorySection.bottom, margin = (-17).dp)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
    }

@Composable
fun CafeNotFoundSection(
    imageLoader: ImageLoader,
    sadEmojiRequest: ImageRequest
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Sad emoji image
        AsyncImage(
            model = sadEmojiRequest,
            contentDescription = "No cafe items found",
            imageLoader = imageLoader,
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )

        // Title
        Text(
            text = "No Cafe Items Available",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Description
        Text(
            text = "We're currently loading our cafe menu. Please check back in a moment!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}