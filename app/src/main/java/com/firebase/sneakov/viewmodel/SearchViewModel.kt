package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.repository.ProductRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductFilter(
    val keyword: String = "",
    val brand: String? = null,
    val color: String? = null,
    val size: Int? = null,
    val sortBy: SortField = SortField.NAME,
    val sortOrder: SortOrder = SortOrder.ASC
)

enum class SortField { NAME, PRICE, CREATED_AT }
enum class SortOrder { ASC, DESC }

class SearchViewModel(private val repo: ProductRepository): BaseViewModel<List<Product>>() {
    private var allProducts: List<Product> = emptyList()
    private val _filter = MutableStateFlow(ProductFilter())
    val filter: StateFlow<ProductFilter> = _filter

    init {
        viewModelScope.launch {
            // Mỗi khi filter thay đổi → lọc lại data trong uiState
            _filter.collect { applyFilter(it) }
        }
    }

    fun fetchProducts() {
        viewModelScope.launch{
            setLoading(true)
            when(val result = repo.getProducts()) {
                is Result.Success -> {
                    allProducts = result.data
                    applyFilter(_filter.value)
                }
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun updateFilter(update: ProductFilter.() -> ProductFilter) {
        _filter.value = _filter.value.update()
    }

    // Hàm lọc
    private fun applyFilter(filter: ProductFilter){
        if(allProducts.isEmpty()) return

        var list = allProducts

        // --- Tìm theo keyword ---
        if (filter.keyword.isNotBlank()){
            list = list.filter {
                it.name.contains(filter.keyword, ignoreCase = true) || it.description?.contains(filter.keyword, ignoreCase = true) ?: false
            }
        }

        // --- Tìm theo brand ---
        filter.brand?.let { brand ->
            list = list.filter { product ->
                product.brand.equals(brand, ignoreCase = true)
            }
        }

        // --- Lọc theo màu ---
        filter.color?.let { color ->
            list = list.filter { product ->
                product.variants.any { variant ->
                    variant.color.equals(color, ignoreCase = true)
                }
            }
        }

        // --- Lọc theo size ---
        filter.size?.let { size ->
            list = list.filter { product ->
                product.variants.any { variant ->
                    variant.size == size
                }
            }
        }

        // --- Sắp xếp theo ---
        list = when(filter.sortBy){
            SortField.NAME -> list.sortedBy { it.name }
            SortField.PRICE -> list.sortedBy { product ->
                product.variants.minOf { it.price }
            }
            SortField.CREATED_AT -> list.sortedBy { it.createdAt }
        }

        // --- Kiểu Sắp xếp  ---
        if (filter.sortOrder == SortOrder.DESC) list = list.reversed()

        setData(list)
    }
}