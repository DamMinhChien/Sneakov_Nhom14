package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.repository.ProductRepository
import com.firebase.sneakov.data.repository.WishlistRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class WishlistViewModel(private val wishlistRepo: WishlistRepository, private val productRepo: ProductRepository): BaseViewModel<Any>() {
    fun fetchWishlistWithProducts() {
        viewModelScope.launch{
            setLoading(true)
            when(val result = wishlistRepo.getWishlist()) {
                is Result.Success -> {
                    val wishlist = result.data
                    val productResults = wishlist.map { w ->
                        async {
                            productRepo.getProduct(w.productId)
                        }
                    }.awaitAll()

                    val products = productResults.mapNotNull {
                        if (it is Result.Success) it.data else null
                    }

                    setData(products)
                }
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun addToWishlist(productId: String) {
        viewModelScope.launch{
            setLoading(true)
            val result = wishlistRepo.addToWishlist(productId)
            when(result) {
                is Result.Success -> {
                    fetchWishlistWithProducts()
                }
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun removeFromWishlist(productId: String) {
        viewModelScope.launch{
            setLoading(true)
            val result = wishlistRepo.removeFromWishlist(productId)
            when(result) {
                is Result.Success -> {
                    fetchWishlistWithProducts()
                }
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun dismissError(){
        clearError()
    }
}