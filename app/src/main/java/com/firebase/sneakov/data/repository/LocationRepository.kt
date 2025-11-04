package com.firebase.sneakov.data.repository

import com.firebase.sneakov.data.api.LocationApi
import com.firebase.sneakov.data.model.District
import com.firebase.sneakov.data.model.Province
import com.firebase.sneakov.data.model.Ward
import com.firebase.sneakov.utils.Result

class LocationRepository(private val api: LocationApi) {

    suspend fun getProvinces(): Result<List<Province>> {
        return try {
            val provinces = api.fetchProvinces()
            Result.Success(provinces)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Không thể tải danh sách tỉnh")
        }
    }

    suspend fun getDistrictsByProvince(provinceCode: Int): Result<List<District>> {
        return try {
            val districts = api.fetchDistricts().filter { it.provinceCode == provinceCode }
            Result.Success(districts)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Không thể tải danh sách huyện")
        }
    }

    suspend fun getWardsByDistrict(districtCode: Int): Result<List<Ward>> {
        return try {
            val wards = api.fetchWards().filter { it.districtCode == districtCode }
            Result.Success(wards)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Không thể tải danh sách xã")
        }
    }
}