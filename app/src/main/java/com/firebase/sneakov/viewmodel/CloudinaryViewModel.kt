package com.firebase.sneakov.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.repository.CloudinaryRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class CloudinaryViewModel(private val repo: CloudinaryRepository): BaseViewModel<String>() {
    fun uploadImage(context: Context, uri: Uri) {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.uploadImageToCloudinary(context, uri)
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