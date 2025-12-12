package com.firebase.sneakov.data.repository

import com.firebase.sneakov.data.model.Wishlist
import com.firebase.sneakov.utils.CollectionName
import com.firebase.sneakov.utils.FieldName
import com.firebase.sneakov.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class WishlistRepository(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {
    suspend fun getWishlist(): Result<List<Wishlist>>{
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error("Người dùng chưa đăng nhập")
            val snapshot = db.collection(CollectionName.WISHLIST)
                .whereEqualTo(FieldName.USER_ID, userId)
                .orderBy(FieldName.ADDED_AT, Query.Direction.DESCENDING)
                .get()
                .await()

            val wishlist = snapshot.toObjects(Wishlist::class.java)
            Result.Success(data = wishlist)
        } catch (e: Exception){
            Result.Error(message = e.message ?: "Lỗi không xác định $e")
        }
    }

    suspend fun addToWishlist(productId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val wishlistItem = hashMapOf(
                FieldName.USER_ID to userId,
                FieldName.PRODUCT_ID to productId,
                FieldName.ADDED_AT to FieldValue.serverTimestamp()
            )

            // Check trùng trước khi thêm
            val existing = db.collection(CollectionName.WISHLIST)
                .whereEqualTo(FieldName.USER_ID, userId)
                .whereEqualTo(FieldName.PRODUCT_ID, productId)
                .get()
                .await()

            if (!existing.isEmpty) {
                return Result.Error("Sản phẩm đã có trong wishlist")
            }

            db.collection(CollectionName.WISHLIST)
                .add(wishlistItem)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(message = e.message ?: "Lỗi khi thêm wishlist")
        }
    }

    suspend fun removeFromWishlist(productId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val snapshot = db.collection(CollectionName.WISHLIST)
                .whereEqualTo(FieldName.USER_ID, userId)
                .whereEqualTo(FieldName.PRODUCT_ID, productId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.Error("Không tìm thấy sản phẩm trong wishlist")
            }

            for (doc in snapshot.documents) {
                db.collection(CollectionName.WISHLIST).document(doc.id).delete().await()
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(message = e.message ?: "Lỗi khi xóa wishlist")
        }
    }

    suspend fun isInWishlist(productId: String): Result<Boolean>{
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val snapshot = db.collection(CollectionName.WISHLIST)
                .whereEqualTo(FieldName.USER_ID, userId)
                .whereEqualTo(FieldName.PRODUCT_ID, productId)
                .get()
                .await()

            Result.Success(!snapshot.isEmpty)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Lỗi không xác định $e")
        }
    }

    suspend fun getWishlistProductIds(): Result<List<String>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val snapshot = db.collection(CollectionName.WISHLIST)
                .whereEqualTo(FieldName.USER_ID, userId)
                .get()
                .await()

            val ids = snapshot.documents.mapNotNull { it.getString(FieldName.PRODUCT_ID) }
            Result.Success(ids)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Lỗi không xác định $e")
        }
    }

}