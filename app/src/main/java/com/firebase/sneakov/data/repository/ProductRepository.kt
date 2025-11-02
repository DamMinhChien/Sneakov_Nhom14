package com.firebase.sneakov.data.repository

import android.util.Log.e
import com.firebase.sneakov.data.model.Product
import com.firebase.sneakov.data.model.ProductVariant
import com.firebase.sneakov.utils.CollectionName
import com.firebase.sneakov.utils.Result
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kotlin.jvm.java

class ProductRepository(private val db: FirebaseFirestore) {
    suspend fun getProducts(): Result<List<Product>> = coroutineScope {
        return@coroutineScope try {
            val snapshot = db.collection(CollectionName.PRODUCTS)
                .get()
                .await()

            val products = snapshot.toObjects(Product::class.java)

            // Chạy song song các request variants
            val productsWithVariants = products.map { product ->
                async {
                    val variantsSnapshot = db.collection(CollectionName.PRODUCTS)
                        .document(product.id)
                        .collection(CollectionName.VARIANTS)
                        .get()
                        .await()

                    val variants = variantsSnapshot.toObjects(ProductVariant::class.java)
                    product.copy(variants = variants)
                }
            }.awaitAll()

            Result.Success(productsWithVariants)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Lỗi không xác định $e")
        }
    }


    suspend fun get10NewestProducts(): Result<List<Product>> = coroutineScope {
        return@coroutineScope try {
            val snapshot = db.collection(CollectionName.PRODUCTS)
                .whereNotEqualTo("created_at", null)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .await()

            val products = snapshot.toObjects(Product::class.java)

            val productsWithVariants = products.map { product ->
                async {
                    val variantsSnapshot = db.collection(CollectionName.PRODUCTS)
                        .document(product.id)
                        .collection(CollectionName.VARIANTS)
                        .get()
                        .await()

                    val variants = variantsSnapshot.toObjects(ProductVariant::class.java)
                    product.copy(variants = variants)
                }
            }.awaitAll()

            Result.Success(productsWithVariants)
        } catch (e: Exception){
            Result.Error(message = e.message ?: "Lỗi không xác định $e")
        }
    }

    suspend fun getProduct(id: String): Result<Product> {
        return try {
            val snapshot = db.collection(CollectionName.PRODUCTS)
                .document(id)
                .get()
                .await()

            if(!snapshot.exists()) return Result.Error("Sản phẩm không tồn tại!")

            val product = snapshot.toObject(Product::class.java)

            val variantsSnapshot = db.collection(CollectionName.PRODUCTS)
                .document(product!!.id)
                .collection(CollectionName.VARIANTS)
                .get()
                .await()

            val variants = variantsSnapshot.toObjects(ProductVariant::class.java)
            val productWithVariants = product.copy(variants = variants)

            Result.Success(productWithVariants)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Lỗi không xác định $e")
        }
    }
}