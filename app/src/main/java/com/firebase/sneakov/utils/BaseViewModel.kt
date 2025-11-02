package com.firebase.sneakov.utils

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

open class BaseViewModel<T>: ViewModel() {
    private val _uiState = MutableStateFlow(UiState<T>())
    val uiState: StateFlow<UiState<T>> = _uiState

    protected fun setLoading(isLoading: Boolean){
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    protected fun setData(data: T){
        _uiState.update { it.copy(data = data, isLoading = false, error = null) }
    }

    protected fun setError(message: String){
        _uiState.update { it.copy(error = message, isLoading = false) }
    }

    protected fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}