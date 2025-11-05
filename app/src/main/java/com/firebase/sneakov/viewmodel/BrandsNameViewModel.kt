package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.repository.BrandRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class BrandsNameViewModel(
    private val brandRepository: BrandRepository
) : BaseViewModel<List<String>>() {

    fun getBrandsName() {
        viewModelScope.launch {
            setLoading(true)
            when (val result = brandRepository.getBrandsName()) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }
}
