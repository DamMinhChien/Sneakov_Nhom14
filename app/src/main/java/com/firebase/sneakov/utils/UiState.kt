package com.firebase.sneakov.utils

data class UiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null,
    val action: String? = null,
)
