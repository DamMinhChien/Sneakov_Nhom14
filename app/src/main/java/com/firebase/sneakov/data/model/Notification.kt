package com.firebase.sneakov.data.model

import com.google.firebase.Timestamp

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val userId: String,
    val createdAt: Timestamp = Timestamp.now(),
    val read: Boolean = false
) {

}