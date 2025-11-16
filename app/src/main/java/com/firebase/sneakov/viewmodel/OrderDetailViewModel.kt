package com.firebase.sneakov.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Order
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.repository.OrderRepository
import com.firebase.sneakov.data.repository.ProductRepository
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class OrderDetailState(
    val order: Order? = null,
    val products: Map<String, Product> = emptyMap(),
    val isLoading: Boolean = true
)

class OrderDetailViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val orderRepo: OrderRepository,
    private val productRepo: ProductRepository
): ViewModel() {
    private val _state = MutableStateFlow(OrderDetailState())
    val state = _state.asStateFlow()

    init {
        val orderId = savedStateHandle.get<String>("orderId")
        if(orderId != null) {
            fetchOrderDetail(orderId)
        }else {
            _state.value = OrderDetailState(isLoading = false)
        }
    }

    fun refreshOrder() {
        val orderId = savedStateHandle.get<String>("orderId")
        if(orderId != null) {
            fetchOrderDetail(orderId)
        }else {
            _state.value = OrderDetailState(isLoading = false)
        }
    }


    private fun fetchOrderDetail(orderId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // 1. Lấy thông tin đơn hàng
            val order = orderRepo.getOrderById(orderId)

            if(order != null) {
                // 2. Từ đơn hàng, lấy ra danh sách các product ID
                val productIds = order.products.map { it.productId }.distinct()
                if(productIds.isNotEmpty()) {
                    // 3. Lấy thông tin các sản phẩm tương ứng
                    when(val productsResult = productRepo.getProductsByIds(productIds)) {
                        is Result.Success -> {
                            val productsData = productsResult.data
                            val productsMap = productsData.associateBy { it.id }
                            // 4. Cập nhật state với đầy đủ dữ liệu
                            _state.value = OrderDetailState(
                                order = order,
                                products = productsMap,
                                isLoading = false
                            )
                        }
                        is Result.Error -> {
                            // Lấy sản phẩm lỗi, nhưng vẫn hiển thị đơn hàng
                            _state.value = OrderDetailState(
                                order = order,
                                products = emptyMap(), // Không có thông tin sản phẩm
                                isLoading = false
                            )
                        }
                    }
                } else {
                    _state.value = OrderDetailState(order = order, products = emptyMap(), isLoading = false)
                }
            }else {
                // Trường hợp không tìm thấy đơn hàng
                _state.value = OrderDetailState(isLoading = false)
            }
        }
    }
}