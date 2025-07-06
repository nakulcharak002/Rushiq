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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import androidx.constraintlayout.compose.MotionLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.ui.theme.navigation.ZeptoDestinations
import com.example.rushiq.ui.theme.viewmodels.HomeViewModel
import com.example.zepto.R
import getCategoryGradient
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import androidx.constraintlayout.compose.Dimension
import com.example.rushiq.ui.theme.screen.components.CategoriesSection
import com.example.rushiq.ui.theme.screen.components.LocationBar
import com.example.rushiq.ui.theme.screen.components.ProductCard
import com.example.rushiq.ui.theme.screen.components.SearchBar
import com.example.rushiq.ui.theme.screen.components.TrendingProductsSection
import com.example.rushiq.ui.theme.viewmodels.CartViewModel

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalMotionApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    onNavigationToCategory: (String) -> Unit,
    navHostController: NavHostController,
    onProductClick: (Int) -> Unit = {},
    cartViewModel: CartViewModel
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val scrollState = rememberLazyListState()

    val categories by viewModel.categories.collectAsState()
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val productGrid = remember(products) { products.chunked(3) }

    LaunchedEffect(categories) {
        if (categories.isNotEmpty() && selectedCategory == null) {
            val allCategory = categories.find { it.name.equals("All", ignoreCase = true) }
                ?: categories.first()
            viewModel.selectCategory(allCategory)
        }
    }

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
        selectedCategory?.let { getCategoryGradient(it) }
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
            start = homeScreenStartConstraintSet(),
            end = homeScreenEndConstraintSet(),
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
                    navHostController.navigate(ZeptoDestinations.Search.route)
                })
            }

            Box(
                modifier = Modifier
                    .layoutId("categories_section")
                    .background(Color.Transparent)
                    .zIndex(0.8f)
            ) {
                CategoriesSection(
                    categories = categories,
                   onCategorySelection = viewModel::selectCategory,
                    selectedCategory = selectedCategory,

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
                        OptimizedProductList(
                            scrollState = scrollState,
                            products = products,
                            productGrid = productGrid,
                            onNavigateToCategory = onNavigationToCategory,
                            viewModel = viewModel,
                            cartViewModel = cartViewModel,
                            onProductClick = onProductClick
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(scrollState) {
        snapshotFlow {
            scrollState.layoutInfo.visibleItemsInfo.lastOrNull()?.let {
                it.index >= scrollState.layoutInfo.totalItemsCount - 5
            } ?: false
        }.distinctUntilChanged()
            .filter { it }
            .collect {
                // Trigger pagination API call here
            }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OptimizedProductList(
    scrollState: LazyListState,
    products: List<Products>,
    productGrid: List<List<Products>>,
    onNavigateToCategory: (String) -> Unit,
    viewModel: HomeViewModel,
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
        item(key = "trending_section") {
            Box(modifier = Modifier.animateItemPlacement()) {
                TrendingProductsSection(
                    products = products,
                    onNavigateToCategory = onNavigateToCategory,
                    viewModel = viewModel
                )
            }
        }
        items(
            items = productGrid,
            key = { row -> row.firstOrNull()?.id?.toString() ?: row.hashCode().toString() }
        ) { rowProducts ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
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
        item(key = "not_found_selection"){
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp , bottom = 16.dp)
                        .animateItemPlacement(),

            ){
                NotFoundSection(
                    imageLoader = imageLoader,
                    sadEmojiRequest = sadEmojiRequest,
                    onRentry = {  }
                )

            }
        }
    }
}
// Home screen constraint sets
fun homeScreenStartConstraintSet(): ConstraintSet =
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
fun homeScreenEndConstraintSet(): ConstraintSet =
    ConstraintSet {
        val locationBar = createRefFor(id = "location_bar")
        val searchBar = createRefFor(id = "search_bar")
        val categorySection = createRefFor(id = "category_section")
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



