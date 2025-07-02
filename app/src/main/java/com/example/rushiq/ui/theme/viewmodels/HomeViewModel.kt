package com.example.rushiq.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rushiq.data.models.fakeapi.Category
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.data.repository.ProductRepository
import com.example.zepto.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel(){
   private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories : StateFlow<List<Category>> = _categories.asStateFlow()

    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products : StateFlow<List<Products>> = _products.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory  : StateFlow<Category?> = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading  : StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error  : StateFlow<String?> = _error.asStateFlow()

    // cache of category upon repeated call

    private val productCache = mutableMapOf<String , List<Products>>()

    init {
        fetchData()
    }

    private fun fetchData() {
        fetchCategories()
        fetchAllProducts()
    }

    private fun fetchAllProducts() {
       viewModelScope.launch {
           _isLoading.value = true
           try {
               repository.getCategories()
                   .flowOn(Dispatchers.IO)
                   .collect { apiCategories ->
                       val mappedCategories = withContext(Dispatchers.Default) {
                           mapApiCategoriesToUiCategories(apiCategories)
                       }
                       _categories.value = mappedCategories
                   }

                   } catch (e:Exception){
                   _error.value = "failed tp load categories: ${e.message}"


               } finally {
                   _isLoading.value = false

               }
           }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if(productCache.containsKey("all")){
                    _products.value = productCache["all"]?: emptyList()
                    _isLoading.value = false
                    return@launch
                }
                repository.getProducts()
                    .flowOn(Dispatchers.IO)
                    .collect{apiProducts ->
                        val processedProducts = withContext(Dispatchers.Default){
                            // process product on cpu threads
                            processProducts(apiProducts)
                        }
                        _products.value = processedProducts
                        // cache the products
                        productCache["cacheKey"] = processedProducts



                    }
            }catch (e:Exception){
                _error.value = "failed tp load products: ${e.message}"
            }finally {
                _isLoading.value = false
            }

        }

    }



    private fun processProducts(products: List<Products>):List<Products>{
        return products.map { product ->
            val fixedImageUrl = when{
                product.imageUrl.isEmpty() -> ""
                !product.imageUrl.startsWith("http") && product.imageUrl.startsWith("/") ->
                    "https://fakestore.api.com${product.imageUrl}"
                !product.imageUrl.startsWith("http") -> "https://${product.imageUrl}"
                else -> product.imageUrl
            }
            product.copy(imageUrl = fixedImageUrl)
        }

    }

    private fun fetchProductByCategory(categoryName: String){
        val cacheKey = categoryName.lowercase()
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if(productCache.containsKey(cacheKey)) {
                    _products.value = productCache[cacheKey] ?: emptyList()
                    _isLoading.value = false
                    return@launch
                }
                repository.getProductsByCategory(categoryName)
                    .flowOn(Dispatchers.IO)
                    .collect{apiProducts ->
                        val processedProducts = withContext(Dispatchers.Default){
                            processProducts(apiProducts)

                        }
                        _products.value = processedProducts
                        //catch the products
                        productCache[cacheKey] = processedProducts

                    }

            }catch (e:Exception){
                _error.value = "Failed to load products: ${e.message}"

            }finally {
                _isLoading.value = false
            }

        }

    }



    fun selectCategory(category: Category){
        if(_selectedCategory.value?.id == category.id){
            //don't re- fetch it if the same category
            return
        }
         _selectedCategory.value = category
        if(category.name == "All"){
            fetchAllProducts()

        }

        else{
            fetchProductByCategory(category.name.lowercase())
        }
    }


     private fun mapApiCategoriesToUiCategories(apiCategory: List<String>): List<Category>{
         val allCategory = Category(0, "All" , R.drawable.allll)

         val categoryIconMap = mapOf(
             "electronics" to R.drawable.headphones_round_svgrepo_com,
             "jewelery"  to R.drawable.diamond_01_svgrepo_com,
             "men's clothing" to R.drawable.fashion,
             "women,s clothing"  to R.drawable.beauty

         )
          val mappedCategories = apiCategory.mapIndexed{ index , categoryName ->
              val iconRes = categoryIconMap[categoryName.lowercase()]?: R.drawable.allll
              Category(index+1 , formatCategoryName(categoryName), iconRes)
          }
         return listOf(allCategory )+ mappedCategories
     }

    private fun formatCategoryName(name: String): String {
        return name.split(" ")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercaseChar() }
            }
    }



}