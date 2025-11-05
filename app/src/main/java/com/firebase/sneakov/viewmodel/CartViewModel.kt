package com.firebase.sneakov.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Cart
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.repository.CartRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CartViewModel(
    private val cartRepo: CartRepository,
    private val app: Application
): ViewModel() {
    // dữ liệu là list Pair<Cart, Product?>
    private val _cartItems = MutableStateFlow<List<Pair<Cart, Product?>>>(emptyList())
    val cartItems: StateFlow<List<Pair<Cart, Product?>>> = _cartItems

    fun loadCart(userId: String) {
        viewModelScope.launch {
            try {
                _cartItems.value = cartRepo.getUserCart(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun addToCart(userId: String, productId: String, variantId: String, quantity: Int) {
        viewModelScope.launch {
            val success = cartRepo.addToCart(userId, productId, variantId, quantity)
            if(!success) {
                Toast.makeText(app, "Số lượng vượt quá số lượng tồn kho", Toast.LENGTH_SHORT).show()
            }
            loadCart(userId)
        }
    }

    fun updateQuantity(cartId: String, newQuantity: Int, userId: String) {
        viewModelScope.launch {
            val success = cartRepo.updateQuantity(cartId, newQuantity)
            if (!success) {
                Toast.makeText(app, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show()
            }
            loadCart(userId)
        }
    }

    fun removeFromCart(cartId: String, userId: String) {
        viewModelScope.launch {
            cartRepo.removeFromCart(cartId)
            loadCart(userId)
        }
    }
}