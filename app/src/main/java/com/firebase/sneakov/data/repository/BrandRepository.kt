package com.firebase.sneakov.data.repository

import com.firebase.sneakov.data.model.Brand
import com.firebase.sneakov.utils.CollectionName
import com.firebase.sneakov.utils.FieldName
import com.firebase.sneakov.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BrandRepository(private val db: FirebaseFirestore) {
    suspend fun getBrands(): Result<List<Brand>> {
        return try {
            val snapshot = db.collection(CollectionName.BRANDS)
                .get()
                .await()

            val brands = snapshot.toObjects(Brand::class.java)
            Result.Success(data = brands)
        } catch (e: Exception){
            Result.Error(message = e.message ?: "Lỗi không xác định $e")
        }
    }

    suspend fun getBrandsName(): Result<List<String>> {
        return try {
            val snapshot = db.collection(CollectionName.BRANDS)
                .get()
                .await()

            val brandsName = snapshot.documents.mapNotNull { it.getString(FieldName.NAME) }
            Result.Success(data = brandsName)
        } catch (e: Exception){
            Result.Error(message = e.message ?: "Lỗi không xác định $e")
        }
    }
}