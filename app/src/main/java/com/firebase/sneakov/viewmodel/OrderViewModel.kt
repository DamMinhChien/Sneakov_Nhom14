package com.firebase.sneakov.viewmodel

import androidx.compose.animation.core.copy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Cart
import com.firebase.sneakov.data.model.Order
import com.firebase.sneakov.data.model.OrderItem
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.model.ShippingAddress
import com.firebase.sneakov.data.repository.AuthRepository
import com.firebase.sneakov.data.repository.CartRepository
import com.firebase.sneakov.data.repository.OrderRepository
import com.firebase.sneakov.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutSessionState(
    val itemsToCheckout: List<Pair<Cart, Product?>> = emptyList(),
    val subtotal: Long = 0,
    val shippingFee: Long = 0,
    val totalCost: Long = 0,
)

class OrderViewModel(
    private val orderRepo: OrderRepository,
    private val cartRepo: CartRepository,
    private val authRepo: AuthRepository
) : ViewModel() {
    // State mới để giữ thông tin phiên checkout
    private val _sessionState = MutableStateFlow(CheckoutSessionState())
    val sessionState: StateFlow<CheckoutSessionState> = _sessionState

    // State giữ kết quả đặt hàng
    private val _checkoutState = MutableStateFlow<CheckoutResultState>(CheckoutResultState.Idle)
    val checkoutState: StateFlow<CheckoutResultState> = _checkoutState

    /**
     * Hàm này được gọi từ CartScreen để "gửi" các sản phẩm đã chọn sang.
     */
    fun startCheckoutSession(
        items: List<Pair<Cart, Product?>>,
        subtotal: Long,
        shippingFee: Long,
        totalCost: Long
    ) {
        _sessionState.update {
            it.copy(
                itemsToCheckout = items,
                subtotal = subtotal,
                shippingFee = shippingFee,
                totalCost = totalCost
            )
        }
    }

    fun placeOrder(address: ShippingAddress, payment: String) = viewModelScope.launch {
        _checkoutState.value = CheckoutResultState.Loading

        val userId = authRepo.currentUserId
        if (userId == null) {
            _checkoutState.value = CheckoutResultState.Error("Người dùng chưa đăng nhập.")
            return@launch
        }

        // Lấy dữ liệu từ phiên thanh toán, không gọi lại repo
        val itemsToCheckout = _sessionState.value.itemsToCheckout
        if (itemsToCheckout.isEmpty()) {
            _checkoutState.value =
                CheckoutResultState.Error("Vui lòng chọn sản phẩm để thanh toán.")
            return@launch
        }

        try {
            // Chuyển đổi List<Pair<Cart, Product?>> thành List<OrderItem>
            val orderItems = itemsToCheckout.map { (cartItem, product) ->
                val pVariant = product?.variants?.find { it.id == cartItem.variantId }
                    ?: throw IllegalStateException("Không tìm thấy biến thể sản phẩm cho ${product?.name}")

                OrderItem(
                    productId = cartItem.productId,
                    variantId = cartItem.variantId,
                    quantity = cartItem.quantity,
                    price = pVariant.price
                )
            }

            val order = Order(
                userId = userId,
                products = orderItems,
                shippingAddress = address,
                paymentMethod = payment,
                status = "pending"
            )

            val orderId = orderRepo.createOrder(order)
            if (orderId != null) {
                // Xoá các sản phẩm đã chọn khỏi giỏ hàng
                itemsToCheckout.forEach { (cartItem, _) ->
                    cartRepo.removeFromCart(cartItem.id)
                }
                _checkoutState.value = CheckoutResultState.Success(orderId)
            } else {
                _checkoutState.value = CheckoutResultState.Error("Không thể tạo đơn hàng")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _checkoutState.value = CheckoutResultState.Error("Có lỗi xảy ra: ${e.message}")
        }
    }
    /**
     * Reset lại trạng thái lỗi để có thể đóng dialog.
     */
    fun dismissError() {
        _checkoutState.value = CheckoutResultState.Idle
    }
}




sealed class CheckoutResultState {
    object Idle : CheckoutResultState()
    object Loading : CheckoutResultState()
    data class Success(val orderId: String) : CheckoutResultState()
    data class Error(val message: String) : CheckoutResultState()
}