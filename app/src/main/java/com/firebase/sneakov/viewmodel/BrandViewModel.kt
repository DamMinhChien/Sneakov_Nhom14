package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Brand
import com.firebase.sneakov.data.repository.BrandRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class BrandViewModel(private val repo: BrandRepository) : BaseViewModel<List<Brand>>() {
    init {
        fetchBrands()
    }
    fun fetchBrands() {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.getBrands()
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