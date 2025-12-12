package com.firebase.sneakov.data.repository


import com.firebase.sneakov.data.model.Order
import com.firebase.sneakov.utils.CollectionName
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await


class OrderRepository(
    private val fireStore: FirebaseFirestore
) {
    private val orderRef = fireStore.collection("orders")

        suspend fun createOrder(order: Order): String? = try {
        val docRef = orderRef.document()
        val orderId = docRef.id
        val finalOrder = order.copy(orderId = orderId, orderedAt = Timestamp.now())

        fireStore.runTransaction { transaction ->
            // 1. Kiểm tra và trừ tồn kho
            for (item in order.products) {
                val variantRef = fireStore.collection(CollectionName.PRODUCTS)
                    .document(item.productId)
                    .collection(CollectionName.VARIANTS)
                    .document(item.variantId)

                val snapshot = transaction.get(variantRef)
                
                // Kiểm tra field 'stock' (dựa trên ProductVariant model)
                val currentStock = snapshot.getLong("stock") ?: 0
                if (currentStock < item.quantity) {
                    throw FirebaseFirestoreException(
                        "Sản phẩm ${item.productId} (Size: ${item.variantId}) đã hết hàng!",
                        FirebaseFirestoreException.Code.ABORTED
                    )
                }

                transaction.update(variantRef, "stock", currentStock - item.quantity)
            }

            // 2. Tạo đơn hàng
            transaction.set(docRef, finalOrder)
            
            orderId // Trả về ID nếu thành công
        }.await()
        
    } catch (e: Exception) {
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

    /**
     * Cập nhật trạng thái của một đơn hàng.
     * @param orderId ID của đơn hàng cần cập nhật.
     * @param newStatus Trạng thái mới (ví dụ: "delivered", "canceled").
     */
        suspend fun updateOrderStatus(orderId: String, newStatus: String): Boolean = try {
        if (newStatus == "canceled") {
            // Logic hủy đơn hàng và hoàn tồn kho
            fireStore.runTransaction { transaction ->
                val orderDoc = transaction.get(orderRef.document(orderId))
                val order = orderDoc.toObject(Order::class.java)
                    ?: throw FirebaseFirestoreException("Order not found", FirebaseFirestoreException.Code.ABORTED)

                // Chỉ hoàn kho nếu đơn chưa bị hủy trước đó
                if (order.status != "canceled") {
                    for (item in order.products) {
                        val variantRef = fireStore.collection(CollectionName.PRODUCTS)
                            .document(item.productId)
                            .collection(CollectionName.VARIANTS)
                            .document(item.variantId)

                        val variantSnapshot = transaction.get(variantRef)
                        val currentStock = variantSnapshot.getLong("stock") ?: 0
                        transaction.update(variantRef, "stock", currentStock + item.quantity)
                    }
                }
                transaction.update(orderRef.document(orderId), "status", newStatus)
            }.await()
        } else {
            // Logic cập nhật trạng thái bình thường (delivered, etc.)
            orderRef.document(orderId)
                .update("status", newStatus)
                .await()
        }
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

}