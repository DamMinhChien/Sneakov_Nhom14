package com.firebase.sneakov.data.repository

import com.firebase.sneakov.data.model.Review
import com.firebase.sneakov.utils.CollectionName
import com.firebase.sneakov.utils.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ReviewRepository(private val db: FirebaseFirestore, private val auth: FirebaseAuth) {
    suspend fun getReviewsByProduct(productId: String): Result<List<Review>> {
        return try {
            val snapshot = db.collection(CollectionName.REVIEWS)
                .whereEqualTo("productId", productId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val reviews = snapshot.toObjects(Review::class.java)
            Result.Success(reviews)
        } catch (e: FirebaseFirestoreException) {
            Result.Error("Lỗi Firestore (${e.code.name}): ${e.message}")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Lỗi không xác định: $e")
        }
    }

    suspend fun createReview(review: Review): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val newReview = review.copy(
                userId = currentUser.uid
            )

            db.collection(CollectionName.REVIEWS)
                .add(newReview)
                .await()

            Result.Success(Unit)
        }catch (e: FirebaseFirestoreException) {
            Result.Error("Lỗi Firestore (${e.code.name}): ${e.message}")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Không thể thêm đánh giá: $e")
        }
    }

    suspend fun updateReview(review: Review): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val snapshot = db.collection(CollectionName.REVIEWS)
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("productId", review.productId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.Error("Không tìm thấy đánh giá để cập nhật")
            }

            val docId = snapshot.documents.first().id
            db.collection(CollectionName.REVIEWS)
                .document(docId)
                .update(
                    mapOf(
                        "rating" to review.rating,
                        "comment" to review.comment,
                        "images" to review.images,
                        "isUpdated" to true
                    )
                )
                .await()

            Result.Success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Result.Error("Lỗi Firestore (${e.code.name}): ${e.message}")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Không thể cập nhật đánh giá: $e")
        }
    }

    suspend fun removeReview(productId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error("Người dùng chưa đăng nhập")

            val snapshot = db.collection(CollectionName.REVIEWS)
                .whereEqualTo("userId", currentUser.uid)
                .whereEqualTo("productId", productId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                return Result.Error("Không tìm thấy đánh giá để xóa")
            }

            val docId = snapshot.documents.first().id
            db.collection(CollectionName.REVIEWS)
                .document(docId)
                .delete()
                .await()

            Result.Success(Unit)
        }catch (e: FirebaseFirestoreException) {
            Result.Error("Lỗi Firestore (${e.code.name}): ${e.message}")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Không thể xóa đánh giá: $e")
        }
    }
}