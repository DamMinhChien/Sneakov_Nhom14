package com.firebase.sneakov.data.repository

import com.firebase.sneakov.data.model.Notification
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


class NotificationRepository {
    private val db = Firebase.firestore.collection("notifications")

    suspend fun getNotifications(userId: String): List<Notification> {
        return try {
            val snapshot = db.whereEqualTo("userId", userId)
                .get()
                .await()

            // Dùng mapNotNull để tự động bỏ qua các document không thể chuyển đổi được
            val list = snapshot.documents.mapNotNull { doc ->
                try {
                    // Chuyển đổi an toàn, cung cấp giá trị mặc định cho MỌI TRƯỜNG
                    Notification(
                        id = doc.id,
                        title = doc.getString("title") ?: "Không có tiêu đề",
                        body = doc.getString("body") ?: "Không có nội dung",
                        type = doc.getString("type") ?: "general",
                        userId = doc.getString("userId") ?: "",
                        // Xử lý null an toàn tuyệt đối cho Timestamp
                        createdAt = doc.getTimestamp("createdAt") ?: Timestamp.now(),
                        read = doc.getBoolean("read") ?: false
                    )
                } catch (e: Exception) {
                    // Nếu có lỗi khi map một document cụ thể (ví dụ sai kiểu dữ liệu),
                    // in lỗi ra và bỏ qua document này thay vì làm sập app.
                    println("Failed to parse notification document ${doc.id}: ${e.message}")
                    null
                }
            }
            return list.sortedByDescending { it.createdAt }
        } catch (e: Exception) {
            // Bắt lỗi ở tầng Repository (ví dụ: mất mạng, không có quyền truy cập)
            println("Failed to fetch notifications from Firestore: ${e.message}")
            emptyList() // Trả về danh sách rỗng để UI không bị crash
        }
    }

    /**
     * Thêm một thông báo mới vào Firestore.
     * @param notification Đối tượng Notification để lưu.
     */
    suspend fun addNotification(notification: Notification) {
        try {
            // Sử dụng add() để Firestore tự động tạo ID cho document
            db.add(notification).await()
            println("Notification added successfully.")
        } catch (e: Exception) {
            println("Error adding notification: ${e.message}")
        }
    }

    suspend fun markRead(notificationId: String) {
        db.document(notificationId)
            .update("read", true)
            .await()
    }

    //xoá thông báo
    suspend fun deleteNotification(notificationId: String) {
        db.document(notificationId).delete().await()
    }

    //Xoá tất cả
    suspend fun deleteAllNotifications(userId: String) {
        val snapshots = db.whereEqualTo("userId", userId).get().await()
        val batch = Firebase.firestore.batch()

        snapshots.documents.forEach { doc ->
            batch.delete(doc.reference)
        }

        batch.commit().await()
    }
}