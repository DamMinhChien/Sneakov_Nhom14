package com.firebase.sneakov.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.repository.ProductRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class ProductViewModel(private val repo: ProductRepository): BaseViewModel<List<Product>>() {
    fun fetchProducts() {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.getProducts()
            when(result) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun fetch10NewestProducts() {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.get10NewestProducts()
            Log.d("products", "products form view model: ${result}")
            when(result) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun dismissError(){
        clearError()
    }
}