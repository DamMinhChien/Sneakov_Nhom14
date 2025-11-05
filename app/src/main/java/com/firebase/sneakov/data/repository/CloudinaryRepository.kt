package com.firebase.sneakov.data.repository

import android.content.Context
import android.net.Uri
import com.firebase.sneakov.data.api.CloudinaryApi
import com.firebase.sneakov.utils.Cloudinary
import com.firebase.sneakov.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class CloudinaryRepository(private val cloudinaryApi: CloudinaryApi) {
    suspend fun uploadImageToCloudinary(
        context: Context,
        uri: Uri
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.Error("Không thể đọc ảnh từ URI")

            val bytes = inputStream.readBytes()
            inputStream.close()

            val requestFile = MultipartBody.Part.createFormData(
                name = "file",
                filename = "avatar.jpg",
                body = bytes.toRequestBody("image/*".toMediaTypeOrNull())
            )

            val uploadPreset = Cloudinary.PRESET_NAME
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val url = Cloudinary.BASE_URL + "v1_1/" + Cloudinary.CLOUD_NAME + "/image/upload"

            val response = cloudinaryApi.uploadImage(url, requestFile, uploadPreset)
            Result.Success(response.secure_url)

        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error("Upload thất bại: ${e.message}")
        }
    }
}