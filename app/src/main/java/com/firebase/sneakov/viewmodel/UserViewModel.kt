package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.User
import com.firebase.sneakov.data.repository.AuthRepository
import com.firebase.sneakov.utils.BaseViewModel
import kotlinx.coroutines.launch
import com.firebase.sneakov.utils.Result

class UserViewModel(private val repo: AuthRepository): BaseViewModel<User>(){
    init {
        fetchCurrentUser()
    }
    fun fetchCurrentUser() {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.getCurrentUser()
            when(result) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun dismissError(){
        clearError()
    }
}