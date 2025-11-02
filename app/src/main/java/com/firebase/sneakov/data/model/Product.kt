package com.firebase.sneakov.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.util.Date

@IgnoreExtraProperties
data class Product(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val brand: String = "",
    val thumbnail: String? = null,
    val colors: List<ProductColor> = emptyList(),
    val variants: List<ProductVariant> = emptyList(),
    @PropertyName("created_at")
    val createdAt: Date? = null
)

data class ProductColor(
    val name: String = "",
    val hex: String = "",
    val images: List<String> = emptyList()
)

data class ProductVariant(
    @DocumentId
    val id: String = "",
    val size: Int = 0,
    val color: String = "",
    val price: Long = 0L,
    val stock: Int = 0
)