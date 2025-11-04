package com.firebase.sneakov.data.request

import com.firebase.sneakov.data.model.Address
import com.google.firebase.firestore.PropertyName

data class UpdateUserRequest(
    val name: String? = null,
    val phone: String? = null,
    @PropertyName("avatar_url")
    val avatarUrl: String? = null,
    val address: Address? = null
)
