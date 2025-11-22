package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.repository.WishlistRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class HelperViewModel(
    private val wishlistRepository: WishlistRepository,
) : BaseViewModel<List<String>>() {
    init {
        fetchWishlistIds()
    }
    fun fetchWishlistIds() {
        viewModelScope.launch {
            setLoading(true)
            when (val result = wishlistRepository.getWishlistProductIds()) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }
}
