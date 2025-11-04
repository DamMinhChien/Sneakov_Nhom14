package com.firebase.sneakov.viewmodel

import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.repository.LocationRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.launch

class LocationViewModel(private val repo: LocationRepository) : BaseViewModel<Any>() {
    fun getProvinces() {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.getProvinces()
            when(result) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun getDistrictsByProvince(provinceCode: Int) {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.getDistrictsByProvince(provinceCode)
            when(result) {
                is Result.Success -> setData(result.data)
                is Result.Error -> setError(result.message)
            }
            setLoading(false)
        }
    }

    fun getWardsByDistrict(districtCode: Int) {
        viewModelScope.launch{
            setLoading(true)
            val result = repo.getWardsByDistrict(districtCode)
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