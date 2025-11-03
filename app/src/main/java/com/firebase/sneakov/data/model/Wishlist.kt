package com.firebase.sneakov.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.Date

@IgnoreExtraProperties
data class Wishlist(
    @DocumentId
    val id: String = "",
    val userId: String = "",
    val productId: String = "",
    val addedAtt: Date? = null
)

