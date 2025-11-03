package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.repository.WishlistRepository
import com.firebase.sneakov.utils.BaseViewModel
import kotlinx.coroutines.launch
import com.firebase.sneakov.utils.Result

class HelperViewModel(private val repo: WishlistRepository): BaseViewModel<List<String>>() {

    fun fetchWishlistIds() {
        viewModelScope.launch {
            setLoading(true)
            when (val result = repo.getWishlistProductIds()) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }
}
