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

    //State để giữ danh sách tất cả đơn hàng cua người dùng
    private  val _userOrders = MutableStateFlow<List<Order>>(emptyList())
    val userOrders: StateFlow<List<Order>> = _userOrders

    //state để báo hiệu đang tải danh sách
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /**
     * Hàm này được gọi từ OrderScreen để lấy tất cả đơn hàng của người dùng
     */
    fun fetchUserOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            val userId = authRepo.currentUserId
            if(userId != null) {
                _userOrders.value = orderRepo.getOrdersByUser(userId)
            }
            _isLoading.value = false
        }
    }

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

    /**
     * Cập nhật trạng thái đơn hàng sang 'delivered'.
     */
    fun shipOrder(orderId: String) {
        viewModelScope.launch {
            val success = orderRepo.updateOrderStatus(orderId, "delivered")
            if (success) {
                // Cập nhật lại danh sách đơn hàng trên UI
                _userOrders.update { currentOrders ->
                    currentOrders.map { order ->
                        if (order.orderId == orderId) order.copy(status = "delivered") else order
                    }
                }
            }
        }
    }

    /**
     * Cập nhật trạng thái đơn hàng sang 'canceled'.
     */
    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            val success = orderRepo.updateOrderStatus(orderId, "canceled")
            if (success) {
                // Cập nhật lại danh sách đơn hàng trên UI
                _userOrders.update { currentOrders ->
                    currentOrders.map { order ->
                        if (order.orderId == orderId) order.copy(status = "canceled") else order
                    }
                }
            }
        }
    }
}




sealed class CheckoutResultState {
    object Idle : CheckoutResultState()
    object Loading : CheckoutResultState()
    data class Success(val orderId: String) : CheckoutResultState()
    data class Error(val message: String) : CheckoutResultState()
}