package com.firebase.sneakov.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Brand(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val thumbnail: String? = null,
    val created_at: Timestamp? = null
)
