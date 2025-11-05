package com.firebase.sneakov.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import java.util.Date

@IgnoreExtraProperties
data class User(
    @DocumentId
    val id: String= "",
    val name: String= "",
    @PropertyName("email")
    val email: String= "",
    val role: String = "user",
    @PropertyName("avatar_url")
    val avatar_url: String = "",
    val createdAt: Date = Date(),
    val phone: String = "",
    val address: Address = Address()
)


data class Address(
    val province: String = "",
    val district: String = "",
    val municipality: String = "",
    val detail: String = ""
)
