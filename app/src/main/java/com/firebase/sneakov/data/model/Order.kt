package com.firebase.sneakov.data.model

import com.google.firebase.Timestamp


data class Order(
    val orderId: String = "",
    val userId: String = "",
    val products: List<OrderItem> = emptyList(),
    val shippingAddress: ShippingAddress? = null,
    val paymentMethod: String = "COD",
    val status: String = "pending",
    val orderedAt: Timestamp? = null
)

data class OrderItem(
    val productId: String = "",
    val variantId: String = "",
    val quantity: Int = 1,
    val price: Long = 0L
)

data class ShippingAddress(
    val name: String = "",
    val phone: String = "",
    val province: String = "",
    val district: String = "",
    val commune: String = "",
    val detail: String = ""
)