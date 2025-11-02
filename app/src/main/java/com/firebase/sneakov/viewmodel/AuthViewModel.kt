package com.firebase.sneakov.viewmodel

import android.R.attr.name
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.User
import com.firebase.sneakov.data.repository.AuthRepository
import com.firebase.sneakov.data.request.LoginRequest
import com.firebase.sneakov.data.request.RegisterRequest
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: AuthRepository): BaseViewModel<Unit>() {
    fun register(email: String, password: String, name: String) {
        val request = RegisterRequest(email, password, name)
        viewModelScope.launch{
            setLoading(true)
            val result = repo.register(request)
            when(result) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun login(email: String, password: String) {
        val request = LoginRequest(email, password)
        viewModelScope.launch{
            setLoading(true)
            val result = repo.login(request)
            when(result) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun logout() {
        repo.logout()
    }

    fun dismissError(){
        clearError()
    }
}