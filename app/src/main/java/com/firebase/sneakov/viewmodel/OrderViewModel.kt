package com.firebase.sneakov.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Order
import com.firebase.sneakov.data.model.OrderItem
import com.firebase.sneakov.data.model.ShippingAddress
import com.firebase.sneakov.data.repository.CartRepository
import com.firebase.sneakov.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepo: OrderRepository,
    private val cartRepo: CartRepository
) : ViewModel(){
    private val _checkoutState = MutableStateFlow<CheckoutState>(CheckoutState.Idle)
    val checkoutState: StateFlow<CheckoutState> = _checkoutState

    fun placeOrder(userId: String, address: ShippingAddress, payment: String) = viewModelScope.launch {
        _checkoutState.value = CheckoutState.Loading

        try {
            // lấy danh sách các cặp (cartItem, product)
            val cartData = cartRepo.getUserCart(userId)
            if(cartData.isEmpty()) {
                // Sửa lại thành
                _checkoutState.value = CheckoutState.Error("Giỏ hàng trống")
                return@launch
            }
            // chuyển đổi List<Pair<Cart, Product?>> thành List<OrderItem>
            val orderItem = cartData.mapNotNull { (cartItem, product) ->
                val pVariant = product?.variants?.find { it.id == cartItem.variantId }
                if(product != null && pVariant != null) {
                    OrderItem(
                        productId = cartItem.productId,
                        variantId = cartItem.variantId,
                        quantity = cartItem.quantity,
                        price = pVariant.price
                    )
                }else {
                    null
                }
            }
            // kiểm tra lại sau khi lọc
            if(orderItem.isEmpty()) {
                _checkoutState.value = CheckoutState.Error("Không tìm thấy thông tin sản phẩm")
                return@launch
            }
            val order = Order(
                userId = userId,
                products = orderItem,
                shippingAddress = address,
                paymentMethod = payment,
                status = "pending"
            )

            val orderId = orderRepo.createOrder(order)
            if(orderId != null) {
                // xoá giỏ hàng sau khi đặt hàng thành công
                cartData.forEach { (cartItem, _) ->
                    cartRepo.removeFromCart(cartItem.id)
                }
                _checkoutState.value = CheckoutState.Success(orderId)
            }else {
                _checkoutState.value = CheckoutState.Error("Không thể tạo đơn hàng")
            }
            }catch (e: Exception) {
                e.printStackTrace()
            _checkoutState.value = CheckoutState.Error("Có lỗi xảy ra: ${e.message}")
            }
        }
    }


sealed class CheckoutState {
    object Idle : CheckoutState()
    object Loading : CheckoutState()
    data class Success(val orderId: String) : CheckoutState()
    data class Error(val message: String) : CheckoutState()
}