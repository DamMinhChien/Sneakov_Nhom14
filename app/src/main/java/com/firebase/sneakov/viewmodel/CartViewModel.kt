package com.firebase.sneakov.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Cart
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.repository.AuthRepository
import com.firebase.sneakov.data.repository.CartRepository
import com.firebase.sneakov.utils.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.update

class CartViewModel(
    private val cartRepo: CartRepository,
    private val app: Application,
    private val authRepository: AuthRepository
): BaseViewModel<List<Cart>>() {
    // dữ liệu là list Pair<Cart, Product?>
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()
    private val _cartItems = MutableStateFlow<List<Pair<Cart, Product?>>>(emptyList())
    val cartItems: StateFlow<List<Pair<Cart, Product?>>> = _cartItems
    private val _selectedItems = MutableStateFlow<Set<String>>(emptySet())
    val selectedItems = _selectedItems.asStateFlow()

    /**
     *  Chọn hoặc bỏ chọn một sản phẩm trong giỏ hàng.
     *  @param cartId ID của mục trong giỏ hàng (không phải productId)
     */

    init {
        loadCart()
    }
    fun toggleSelection(cartId: String) {
        _selectedItems.update { currentSelection ->
            if (currentSelection.contains(cartId)) {
                currentSelection - cartId // Nếu đã chọn -> bỏ chọn
            } else {
                currentSelection + cartId // Nếu chưa chọn -> thêm vào
            }
        }
    }

    /**
     *  Chọn hoặc bỏ chọn tất cả sản phẩm.
     */
    fun toggleSelectAll() {
        _selectedItems.update { currentSelection ->
            val allCartIds = _cartItems.value.map { it.first.id }.toSet()
            if (currentSelection.size == allCartIds.size) {
                emptySet() // Nếu đã chọn tất cả -> bỏ chọn tất cả
            } else {
                allCartIds // Nếu chưa chọn tất cả -> chọn tất cả
            }
        }
    }

    /**
     *  Xóa các item đã chọn khỏi StateFlow khi tải lại giỏ hàng
     */
    private fun clearSelections() {
        _selectedItems.value = emptySet()
    }

    fun loadCart() {
        val userId = authRepository.currentUserId
        if(userId == null) {
            _cartItems.value = emptyList()
            clearSelections()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            clearSelections()
            try {
                _cartItems.value = cartRepo.getUserCart(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun addToCart(productId: String, variantId: String, quantity: Int) {
        val userId = authRepository.currentUserId ?: return
        viewModelScope.launch {
            val success = cartRepo.addToCart(userId, productId, variantId, quantity)
            if(!success) {
                Toast.makeText(app, "Số lượng vượt quá số lượng tồn kho", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(app, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
            }
            loadCart()
        }
    }

    fun updateQuantity(cartId: String, newQuantity: Int) {
        viewModelScope.launch {
            val success = cartRepo.updateQuantity(cartId, newQuantity)
            if (!success) {
                Toast.makeText(app, "Số lượng vượt quá tồn kho", Toast.LENGTH_SHORT).show()
            }
            loadCart()
        }
    }

    fun removeFromCart(cartId: String) {
        viewModelScope.launch {
            _selectedItems.update { it - cartId }
            cartRepo.removeFromCart(cartId)
            loadCart()
        }
    }
}