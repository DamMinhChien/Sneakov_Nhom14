package com.firebase.sneakov.data.model

import com.squareup.moshi.Json

data class Province(
    val name: String,
    val code: Int
)

data class District(
    val name: String,
    val code: Int,
    @Json(name = "province_code")
    val provinceCode: Int
)

data class Ward(
    val name: String,
    val code: Int,
    @Json(name = "district_code")
    val districtCode: Int
)
