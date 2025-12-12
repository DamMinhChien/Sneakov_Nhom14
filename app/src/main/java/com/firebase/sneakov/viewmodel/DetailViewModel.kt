package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.repository.ProductRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class DetailViewModel(private val repo: ProductRepository): BaseViewModel<Product>() {
    private var lastId: String? = null

    fun loadProduct(id: String) {
        if (id == lastId) return // Không làm gì nếu ID không thay đổi
        lastId = id
        fetchProduct(id)
    }
    fun fetchProduct(id: String) {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.getProduct(id)
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