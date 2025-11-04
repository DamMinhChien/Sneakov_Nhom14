package com.firebase.sneakov.data.api

import com.firebase.sneakov.data.model.CloudinaryResponse
import com.firebase.sneakov.utils.Cloudinary
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface CloudinaryApi {
    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @Part(Cloudinary.PRESET_NAME) uploadPreset: RequestBody
    ): CloudinaryResponse
}