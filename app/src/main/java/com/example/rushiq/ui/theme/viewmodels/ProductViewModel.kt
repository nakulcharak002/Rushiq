package com.example.rushiq.ui.theme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rushiq.data.models.fakeapi.Products
import com.example.rushiq.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProductViewModel @Inject constructor(
   private val productRepository: ProductRepository,
): ViewModel() {
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
   val products : StateFlow<List<Products>> = productRepository.getProducts()
       .catch { e ->
           _error.value = e.message?: "Unknown error occurred"
           emit(emptyList())
       }
       .stateIn(
           scope = viewModelScope,
           started = SharingStarted.Lazily,
           initialValue = emptyList()
       )
    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _error.value = null
            } catch (e: Exception ){
                _error.value = e.message?: "Unknown error occurred "
            }
            finally {
                _loading.value =false
            }
        }
    }
    fun getProductById(id : String):Products? {
        val idInt = id.toIntOrNull()
        return idInt?.let { idAsInt ->
            products.value.find {
                it.id == idAsInt
            }
        }
    }
        fun refreshProducts() {
            loadProducts()
        }
    }

