package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.repository.ProductRepository
import com.firebase.sneakov.utils.BaseViewModel
import kotlinx.coroutines.launch
import com.firebase.sneakov.utils.Result

class ColorViewModel(
    private val productRepository: ProductRepository
) : BaseViewModel<List<String>>() {
    init {
        getColorsName()
    }
    fun getColorsName() {
        viewModelScope.launch {
            setLoading(true)
            when (val result = productRepository.getColorsName()) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }
}