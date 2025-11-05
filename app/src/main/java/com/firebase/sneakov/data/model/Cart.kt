package com.firebase.sneakov.data.model

import com.google.firebase.Timestamp

data class Cart (
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val variantId: String = "",
    val quantity: Int = 0,
    val addedAt: Timestamp? = null,
)