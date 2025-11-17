package com.firebase.sneakov.data.repository


import com.firebase.sneakov.data.model.Order
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class OrderRepository(
    private val fireStore: FirebaseFirestore
) {
    private val orderRef = fireStore.collection("orders")

    suspend fun createOrder(order: Order): String? = try {
        // Tạo docRef và lấy orderId
        val docRef = orderRef.document()
        val orderId = docRef.id

        // Trả về orderId, và trong khối also, thực hiện việc lưu dữ liệu
        orderId.also { id ->
            val finalOrder = order.copy(orderId = id, orderedAt = Timestamp.now())
            docRef.set(finalOrder).await()
        }
    } catch (e: Exception) {
        // Xử lý lỗi, ví dụ: log lỗi ra console để debug
        e.printStackTrace()
        null
    }


    suspend fun getOrdersByUser(userId: String): List<Order> = try {
        orderRef.whereEqualTo("userId", userId)
            .get()
            .await()
            .toObjects(Order::class.java)
    }catch (e: Exception) {
        emptyList()
    }

    suspend fun getOrderById(orderId: String): Order? {
        return try {
            orderRef.document(orderId).get().await().toObject(Order::class.java)
        }catch (e: Exception) {
            println("Error getting order by ID: ${e.message}")
            null
        }

    }

}