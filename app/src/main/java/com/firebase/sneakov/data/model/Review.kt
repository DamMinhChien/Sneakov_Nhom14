package com.firebase.sneakov.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Review(
    @DocumentId
    val id: String = "",
    val username: String = "",
    val province: String = "",
    val productId: String = "",
    val userId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val images: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val isUpdated: Boolean = false,
    val orderId: String = ""
)
