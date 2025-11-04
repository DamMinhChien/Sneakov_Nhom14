package com.firebase.sneakov.data.api

import com.firebase.sneakov.data.model.District
import com.firebase.sneakov.data.model.Province
import com.firebase.sneakov.data.model.Ward
import retrofit2.http.GET

interface LocationApi {
    @GET("p.json")
    suspend fun fetchProvinces(): List<Province>

    @GET("d.json")
    suspend fun fetchDistricts(): List<District>

    @GET("w.json")
    suspend fun fetchWards(): List<Ward>
}