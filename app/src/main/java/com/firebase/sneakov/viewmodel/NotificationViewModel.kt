package com.firebase.sneakov.viewmodel

import android.util.Printer
import androidx.lifecycle.viewModelScope
import com.firebase.sneakov.data.model.Notification
import com.firebase.sneakov.data.repository.AuthRepository
import com.firebase.sneakov.data.repository.NotificationRepository
import com.firebase.sneakov.data.repository.OrderRepository
import com.firebase.sneakov.utils.BaseViewModel
import com.firebase.sneakov.utils.formatMoney
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class NotificationViewModel(
    private val repository: NotificationRepository,
    private val authRepo: AuthRepository,
    private val orderRepo: OrderRepository
) : BaseViewModel<List<Notification>>() {
    private val _state = MutableStateFlow<List<Notification>>(emptyList())
    val state: StateFlow<List<Notification>> = _state

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    val unreadCount = state.map { notifications ->
        notifications.count { !it.read}
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    init {
        loadNotifications()
    }
    fun loadNotifications() {
        val userId = authRepo.currentUserId
        if (userId == null) {
            _isLoading.value = false
            _state.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Toàn bộ logic lấy dữ liệu được đặt trong khối try
                val data = repository.getNotifications(userId)
                _state.value = data.reversed()

            } catch (e: CancellationException) {
                // Khi coroutine bị hủy (ví dụ: người dùng thoát khỏi màn hình),
                // bắt lỗi ở đây và in ra log thay vì để app crash.
                println("Notification loading was cancelled: ${e.message}")
                // Ném lại exception này để đảm bảo coroutine con cũng được hủy đúng cách
                throw e
            } catch (e: Exception) {
                // Bắt các lỗi khác có thể xảy ra (mất mạng, lỗi server, v.v.)
                // Ở đây bạn có thể cập nhật UI để hiển thị thông báo lỗi
                println("Failed to load notifications: ${e.message}")
                _state.value = emptyList()
            }
            finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Tạo thông báo khi đặt hàng thành công.
     * @param totalAmount Tổng số tiền của đơn hàng.
     * @param itemCount Số lượng sản phẩm trong đơn hàng.
     */
    fun createOrderNotification(orderId: String) {
        viewModelScope.launch {
            // Lấy ID của người dùng đang đăng nhập
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            if (currentUserId == null) {
                println("Cannot create notification: User not logged in.")
                return@launch
            }
            // Dùng OrderRepository để lấy chi tiết đơn hàng
            val order = orderRepo.getOrderById(orderId)
            if (order == null) {
                println("Cannot create notification: Order with ID $orderId not found.")
                return@launch
            }
            // Tạo đối tượng Notification
            val newNotification = Notification(
                id = "", // ID sẽ được Firestore tự tạo, để trống
                title = "Đặt hàng thành công! 🎉",
                body = "Đơn hàng #${orderId.take(6).uppercase()} của bạn với ${order.products.size} sản phẩm đã được xác nhận. Sản phẩm sẽ được chuyển đến bạn trong thời gian sớm nhất",
                type = "order_success",
                userId = currentUserId,
                createdAt = Timestamp.now(),
                read = false
            )
            try {
                repository.addNotification(newNotification)
                loadNotifications()
            }catch (e: Exception) {
                println("Lỗi khi tạo thông báo: ${e.message}" )
            }

        }
    }

    fun markRead(id: String) {
        viewModelScope.launch {
            repository.markRead(id)

            // Cập nhật lại UI
            _state.value = _state.value.map {
                if (it.id == id) it.copy(read = true) else it
            }
        }
    }

    fun delNotification(id: String) {
        viewModelScope.launch {
            repository.deleteNotification(id)
            _state.value = _state.value.filter { it.id != id }
        }
    }

    fun delAll() {
        viewModelScope.launch {
            var userId = authRepo.currentUserId ?: return@launch
            _isLoading.value = true
            try {
                // 2. Thực hiện hành động xóa trên server.
                // `await()` có thể được thêm vào trong repository để đảm bảo nó hoàn tất.
                repository.deleteAllNotifications(userId)

                // 3. Cập nhật trạng thái client một cách rõ ràng.
                // Không cần gọi lại loadNotifications() nữa.
                _state.value = emptyList()

            } catch (e: Exception) {
                // Nếu có lỗi, bạn có thể muốn tải lại dữ liệu cũ.
                println("Lỗi khi xóa tất cả thông báo: ${e.message}")
                // Có thể gọi loadNotifications() ở đây để khôi phục.
            } finally {
                // 4. Luôn đảm bảo rằng trạng thái loading được tắt sau khi hoàn tất.
                _isLoading.value = false
            }
        }
    }
}